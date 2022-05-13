package baca;

public class Word
{
    public String WordID;
    public int	  WordStartX;
    public int	  WordStartY;
    public int	  WordWidth;
    public int	  WordHeight;
    public String WordValue;
    public String WordOCRConfidence;
    public int 	  WordCharN;
    public String bold;
    public String WordFontSize;
    public int	  WordFontSizeGroup;
    public String italic;
    public String underlined;

    // ------------ CONSTRUCTOR ---------------
    public Word() {} 
    
    // ------------ HELPERS ---------------
    
    // -- Returns true if both words start in the same X pixel position +/- 1 (THRESHOLD)
    // -- or if the coords of 'word' are completely within the width of 'this'   
    public boolean isVerticallyAligned(Word word) {
    	// --     {0} is vertically aligned with {this}
    	int THRESHOLD = 1;
    	if (word == null) return false;
    	else if (Math.abs(this.WordStartX - word.WordStartX) <= THRESHOLD) // -- Start X coords are "same"
    		return true;
    	else if ( this.WordStartX <= word.WordStartX && 
    			  (this.WordStartX + this.WordWidth) >= (word.WordStartX + word.WordWidth) )
    		return true;     // -- 'this' word extends further left and right of 'word'
    	
    	return false;
    }

}