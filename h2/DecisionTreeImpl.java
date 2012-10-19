import java.util.*;


/**
 * Fill in the implementation details of the class DecisionTree
 * using this file. Any methods or secondary classes
 * that you want are fine but we will only interact
 * with those methods in the DecisionTree framework.
 * 
 * You must add code for the 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	DecTreeNode rootNode;
	DataSet trainingSet;
	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary
		// this is void purposefully
	}
	
	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train the training set
	 */
	DecisionTreeImpl(DataSet train) {
		//Set up some private variables
		trainingSet = train;
		
		List<Instance>instances = train.instances;
		
		assert(instances.size() > 0);
		
		//Let attributes left = all
		ArrayList<Integer> attributesLeft = new ArrayList<Integer>();
		for (int i = 0; i < train.attributes.size(); i++) {
			attributesLeft.add(i);
		}
		
		buildTree(null, instances, attributesLeft, "");
	}
	
	private int getMajority(List<Instance> instances, int noOfLabelTypes){
		assert(instances.size() > 0);
		int[] labelCounts = new int[noOfLabelTypes];
		for (Instance instance : instances) {
			labelCounts[instance.label]++;
		}	
		int majorityCanidate = -1;
		int canidateCount = -1;
		for (int i = 0; i < labelCounts.length; i++) {
			if(labelCounts[i] > canidateCount){
				majorityCanidate = i;
				canidateCount = labelCounts[i];
			}
		}
		return majorityCanidate;
	}
	
	private boolean isPure(List<Instance> instances){
		int canidate = -1;
		for (Instance instance : instances) {
			if (canidate == -1){
				canidate = instance.label;
				continue;
			}
			if (canidate != instance.label){
				return false;
			}
		}	
		return true;
	}
	
	private void buildTree(DecTreeNode parent, List<Instance> instances, List<Integer> attributesLeft, String attributeName){
		if (attributesLeft.size() == 0){
			parent.addChild(new DecTreeNode(trainingSet.labels.get(getMajority(instances, trainingSet.labels.size())), "", attributeName, true));
			return;
		}
		double entropy = calculateEntropy(instances, trainingSet.labels.size());
		Integer highestEntropyId = -1;
		double highestMutualInfo = -1;
		for (int i = 0; i < attributesLeft.size(); i++) { //For each attribute
			double conditionalEntropy = calculateConditionalEntropy(attributesLeft.get(i), instances, trainingSet.attributeValues.get(trainingSet.attributes.get(attributesLeft.get(i))).size(), trainingSet.labels.size());
			double mutualInformation = entropy - conditionalEntropy;
			if (mutualInformation > highestMutualInfo){
				highestMutualInfo = mutualInformation;
				highestEntropyId = attributesLeft.get(i);
			}
		}
		DecTreeNode currentNode;
		if(parent == null){
			rootNode = new DecTreeNode(trainingSet.labels.get(getMajority(instances, trainingSet.labels.size())), trainingSet.attributes.get(highestEntropyId), "ROOT", isPure(instances) || attributesLeft.size() == 1);
			currentNode = rootNode;
		} else {
			currentNode = new DecTreeNode(trainingSet.labels.get(getMajority(instances, trainingSet.labels.size())), trainingSet.attributes.get(highestEntropyId), attributeName, isPure(instances) || attributesLeft.size() == 0);
			parent.addChild(currentNode);
		}
		
		int noOfChildren = trainingSet.attributeValues.get(trainingSet.attributes.get(highestEntropyId)).size();
		ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
		for (int i = 0; i < noOfChildren; i++) {
			labelInstances.add(new ArrayList<Instance>());
		}
		for (Instance instance : instances) {
			labelInstances.get(instance.attributes.get(highestEntropyId)).add(instance);
		}
		for (int i = 0; i < noOfChildren; i++) {
			attributesLeft.remove(attributesLeft.indexOf(highestEntropyId));
			buildTree(currentNode, labelInstances.get(i), attributesLeft, trainingSet.attributeValues.get(trainingSet.attributes.get(highestEntropyId)).get(i));
			attributesLeft.add(highestEntropyId);
		}
	}

	/**
	 * Build a decision tree given a training set then prune it
	 * using a tuning set.
	 * 
	 * @param train the training set
	 * @param tune the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {
		this(train);
		double lastAccuracy = calculateAccuracy(tune);
		DecTreeNode nodeCandiate = null;
		ArrayList<DecTreeNode> nonTerminalNodes = new ArrayList<DecTreeNode>();
		nonTerminalDFS(rootNode, nonTerminalNodes);
		double candidateAccuracy = -1;
		int candidateCount = 0;
		for (DecTreeNode currNode : nonTerminalNodes) {
			currNode.terminal = true;
			double accuracy = calculateAccuracy(tune);
			currNode.terminal = false;
			int childrenCount = countDFS(currNode);
			if (accuracy > candidateAccuracy || (accuracy == candidateAccuracy && childrenCount > candidateCount)){
				candidateAccuracy = accuracy;
				candidateCount = childrenCount; 
				nodeCandiate = currNode;
			}
		}
		assert(nodeCandiate != null);
		if (candidateAccuracy >= lastAccuracy) {
			nodeCandiate.children = null;
			nodeCandiate.terminal = true;
		}
	}
	
	private double calculateAccuracy(DataSet tune){
		int correctClassification = 0;
		for (Instance instance : tune.instances) {
			if(classify(instance).equals(tune.labels.get(instance.label))){
				correctClassification++;
			}
		}
		double accuracy = Double.valueOf(correctClassification)/Double.valueOf(tune.instances.size());
		return accuracy;
	}
	
	private void nonTerminalDFS(DecTreeNode node, ArrayList<DecTreeNode> basket){
		if (!node.terminal) {
			basket.add(node);
		} else {
			return;
		}
		for (DecTreeNode currNode : node.children) {
			if (currNode.children != null && currNode.children.size() != 0){
				nonTerminalDFS(currNode, basket);
			}
		}
		
	}
	
	private int countDFS(DecTreeNode node){
		int count = 0;
		if (node.terminal == true) {
			assert(node.children == null || node.children.size() == 0);
			return 1;
		}
		for (DecTreeNode currNode : node.children) {
			count += countDFS(currNode);
		}
		return count;
	}

	@Override
	public String classify(Instance instance) {
		//Build reverse index
		Map<String, Integer > attributeMap = new HashMap<String, Integer>();
		for (int i = 0; i < trainingSet.attributes.size(); i++) {
			attributeMap.put(trainingSet.attributes.get(i), i);
		}
		DecTreeNode currNode = rootNode;
		while(true){
			if (currNode.terminal){
				break;
			} else {
				int currAttr = attributeMap.get(currNode.attribute);
				int instanceValue = instance.attributes.get(currAttr);
				DecTreeNode foundNode = null;
				for (DecTreeNode node : currNode.children) {
					if (node.parentAttributeValue.equals(trainingSet.attributeValues.get(currNode.attribute).get(instanceValue))){
						foundNode = node;
						break;
					}
				}
				assert(foundNode != null);
				currNode = foundNode;
			}
		}
		return currNode.label;
	}

	@Override
	public void print() {
		assert(rootNode != null);
		rootNode.print(0);
	}

	@Override
	public void rootMutualInformation(DataSet train) {
		double entropy = calculateEntropy(train.instances, train.labels.size());
		double mutualInformation = 0.0;
		for (int i = 0; i < train.attributes.size(); i++) { //For each attribute
			double conditionalEntropy = calculateConditionalEntropy(i, train.instances, train.attributeValues.get(train.attributes.get(i)).size(), train.labels.size());
			mutualInformation = entropy - conditionalEntropy;
			System.out.printf("%s %.3f\n", train.attributes.get(i) + " ", mutualInformation);
		}
	}
	
	
	private double calculateEntropy(List<Instance> instances, int noOfLabelTypes){
		int[] labelCounts = new int[noOfLabelTypes];
		int totalInstances = instances.size();
		for (Instance instance : instances) {
			labelCounts[instance.label]++;
		}
		double entropy = 0;
		for (int i = 0; i < labelCounts.length; i++) {
			if (totalInstances != 0 && labelCounts[i] != 0) {
				double probability = Double.valueOf(labelCounts[i])/Double.valueOf(totalInstances); 
				entropy += probability * Math.log10(probability) / Math.log10(2);
			}
		}
		return -entropy;
	}
	
	private double calculateConditionalEntropy(int attributeId, List<Instance> instances, int noOfAtrributeTypes, int noOfLabelTypes){

		int[] labelCounts = new int[noOfAtrributeTypes];
		ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
		//Create empty lists
		for (int i = 0; i < noOfAtrributeTypes; i++) {
			labelInstances.add(new ArrayList<Instance>());
		}
		int totalInstances = instances.size();
		for (Instance instance : instances) {
			labelCounts[instance.attributes.get(attributeId)]++;
			labelInstances.get(instance.attributes.get(attributeId)).add(instance);
		}
		double conditionalEntropy = 0;
		for (int i = 0; i < labelCounts.length; i++) {
			if (totalInstances != 0 && labelCounts[i] != 0) {
				double probability = Double.valueOf(labelCounts[i])/Double.valueOf(totalInstances); 
				double subConditionalEntropy = calculateEntropy(labelInstances.get(i), noOfLabelTypes);
				conditionalEntropy += probability * subConditionalEntropy;
			}
		}
		
		return conditionalEntropy;
	}
}