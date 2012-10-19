import java.util.ArrayList;
import java.util.List;

/**
 * Holds data for particular instance.
 * Integer values refer to offsets in meta-data
 * arrays of a surrounding DataSet. 
 * 
 * Direct questions to nixon@cs.wisc.edu
 */
public class Instance {
	public Integer label;
	public List<Integer> attributes = null;

	/**
	 * Add attribute values in the order of
	 * attributes as specified by the dataset
	 */
	public void addAttribute(int i) {
		if (attributes == null) {
			attributes = new ArrayList<Integer>();
		}
		attributes.add(new Integer(i));
	}
	
	/**
	 * Clone of addAttribute(int) using
	 * Integer instead.
	 * Convenience function.
	 */
	public void addAttribute(Integer i) {
		if (attributes == null) {
			attributes = new ArrayList<Integer>();
		}
		attributes.add(i);
	}
}
