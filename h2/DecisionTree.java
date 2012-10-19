/**
 * This class provides a framework for accessing a decision tree.
 * Do not modify or place code here, instead create an
 * implementation in a file DecisionTreeImpl. 
 * 
 * Direct questions to nixon@cs.wisc.edu
 */
abstract class DecisionTree {
	/**
	 * Evaluates the learned decision tree on a single instance.
	 * @return the classification of the instance
	 */
	abstract public String classify(Instance instance);
	
	/**
	 * Prints the tree in specified format. It is recommended, but not
	 * necessary, that you use the print method of DecTreeNode.
	 * 
	 * Example:
	 * Root {Patrons?}
	 *   None (No)
	 *   Some (Yes)
	 *   Full {Hungry?}
	 *     No (No)
	 *     Yes {Type?}
	 *       French (Yes)
	 *       Italian (No)
	 *       Thai {Fri/Sat?}
	 *         No (No)
	 *         Yes (Yes)
	 *       Burger (Yes)
	 */
	abstract public void print();
	
	/**
	 *  Print the mutual information of each attribute as computed
	 *  from creating the root node for the given DataSet.
	 *  
	 *  Print the Attribute name then a space then the mutual information.
	 *  Use precision of precisely 3 decimal places in output.
	 *  
	 *  Example:
	 *  PTYPE 0.123
     *  AGE 0.456
     *  SEX 0.248
	 */
	abstract public void rootMutualInformation(DataSet train);
}
