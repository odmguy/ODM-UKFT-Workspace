/**
 * @author Andrew Macdonald, IBM UK Ltd.
 *
 */
package metadata;

public class DataItem {

	public String key = "";
	public String value = "";
	public String confidence = "high"; // -- one of "low","medium" or "high"
	public String comment = "";
	public String location = "";

	public DataItem(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
}