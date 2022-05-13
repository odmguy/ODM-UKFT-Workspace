package baca;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Line
{
    public String LineID = "";
    public int LineStartX;
    public int LineStartY;
    public int LineWidth;
    public int LineHeight;
    public List<Word> WordList;
    public String LineFontFace = "";
    
    //---------------- CONSTRUCTOR --------------
    public Line() {
    	// WordList = new ArrayList<Word>();  // -- TODO is there ever a case where this is needed?
	}

    //---------------- HELPERS --------------

	private String lineTextCache = null; // -- Initialised the first time getCompleteText is called as it may be called often

	@JsonIgnore     // -- To stop this appearing as an attribute in the Generated JSON parameter 
	public String getCompleteText() {
    	if (lineTextCache != null) 
    		return lineTextCache; // -- Already constructed so return it.

    	for (Word word : WordList) {
    		if (word.WordValue.equals(WordList.get(0).WordValue) ) // First word in list so don't concatenate extra space
    			lineTextCache = word.WordValue;
    		else
    			lineTextCache = lineTextCache + " " + word.WordValue;
    	}
    	return lineTextCache;
    }

	@JsonIgnore
	public String getFirstWord() {
    	if (WordList != null) 
    		return WordList.get(0).WordValue;
    	else
    		return "";
    }
	
	@JsonIgnore	public String getFirstUnderlinedPhrase() {
		String phrase = "";
		boolean started = false;
    	for (Word word : WordList) {
    		if (started) {
    			if (!word.underlined.equals("none")) // -- continuing so append 
    				phrase = phrase + " " + word.WordValue;
    			else // -- Gone past end of underlining
    				break;  // -- Jump out of For loop  			
    		}
    		else if (!word.underlined.equals("none")) { // -- Found first"single" or "double" underlined word
    			started = true;
    			phrase = word.WordValue;
    		}
    	}
    	// -- Remove unwanted trailing punctuation
    	if (phrase.endsWith(";")||phrase.endsWith(".")||phrase.endsWith(",") ) {
    		phrase = phrase.substring(0, phrase.length()-1);
    	}
		return phrase;
	}


}
			
			