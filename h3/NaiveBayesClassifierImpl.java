import java.util.Hashtable;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {
	Hashtable<String, Integer> SPAM = new Hashtable<String, Integer>();
	Hashtable<String, Integer> HAM = new Hashtable<String, Integer>();
	Integer SPAMCount = 0;
	Integer HAMCount = 0;
	Integer SPAMInstanceCount = 0;
	Integer HAMInstanceCount = 0;
	Integer vocabSize = 0;
	/**
	 * Trains the classifier with the provided training data and vocabulary size
	 */
	@Override
	public void train(Instance[] trainingData, int v) {
		vocabSize = v;
		Integer count;
		for (Instance instance : trainingData) {
			if (instance.label == Label.SPAM){
				SPAMInstanceCount++;
			} else {
				HAMInstanceCount++;
			}
			for (String word : instance.words) {
				if (instance.label == Label.SPAM){
					SPAMCount++;
					count = SPAM.get(word);
					if(count == null){
						SPAM.put(word, 1);
					} else {
						SPAM.put(word, count + 1);
					}
				} else {
					HAMCount++;
					count = HAM.get(word);
					if(count == null){
						HAM.put(word, 1);
					} else {
						HAM.put(word, count + 1);
					}
					
				}
			}
		}
		
	}

	/**
	 * Returns the prior probability of the label parameter, i.e. P(SPAM) or P(HAM)
	 */
	@Override
	public double p_l(Label label) {
		if (label == Label.SPAM){
			return (double)SPAMInstanceCount/(double)(SPAMInstanceCount + HAMInstanceCount);
		}
			
		return (double)HAMInstanceCount/(double)(SPAMInstanceCount + HAMInstanceCount);
	}

	/**
	 * Returns the smoothed conditional probability of the word given the label,
	 * i.e. P(word|SPAM) or P(word|HAM)
	 */
	@Override
	public double p_w_given_l(String word, Label label) {
		double delta = 0.00001;
		Integer clv;
		Integer size;
		if (label == Label.SPAM){
			clv = SPAM.get(word);
			size = SPAMCount;
		} else {
			clv = HAM.get(word);
			size = HAMCount;
		}
		if (clv == null) { clv = 0; }
		
		return ((double)clv + delta)/(double)(((double)vocabSize * delta) + size);
	}
	
	/**
	 * Classifies an array of words as either SPAM or HAM. 
	 */
	@Override
	public ClassifyResult classify(String[] words) {
		ClassifyResult result = new ClassifyResult();
		for (String word : words) {
			result.log_prob_ham += Math.log10(p_w_given_l(word, Label.HAM));
			result.log_prob_spam += Math.log10(p_w_given_l(word, Label.SPAM));
		}
		result.log_prob_ham = Math.log10(p_l(Label.HAM)) + result.log_prob_ham;
		result.log_prob_spam = Math.log10(p_l(Label.SPAM)) + result.log_prob_spam;
		if (result.log_prob_ham >= result.log_prob_spam){
			result.label = Label.HAM;
		} else {
			result.label = Label.SPAM;
		}
		return result;
	}
}
