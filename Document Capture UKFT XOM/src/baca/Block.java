package baca;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Block {

	public String BlockID;
	public int	 BlockStartX;
	public int	 BlockStartY;
	public int	 BlockWidth;
	public int	 BlockHeight;
	public Line[] LineList;

/****   Used any more ???
	public String language;
	public String BlockBGColor;
	public String BlockFGColor;
	public String BlockType;
	public String Remark;
	public String Miscellaneous;
*****/
	
	// ------------ CONSTRUCTOR --------------
	public Block() {} 
	
	// ----------------- UTILITIES -----------------
	private String blockTextCache = null; // -- Initialised the first time getCompleteText is called as it may be called often
	private String lineTextCache = null; // -- Initialised the first time getTheFirstLine is called as it may be called often

	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public String getTextConfidence(String phrase) {
		// -- 
		// -- Find the 'phrase' with the block complete text and return a confidence value based on all the words
		// -- if any word char is 0-3 then return 'low'
		// --                     4-6 "medium"
		// --                     7-9 "high"
		int lowest = 9;
		String[] wordList = phrase.split(" ");
//		System.out.println("### Found "+wordList.length+" words in: "+phrase);
		for (String wordValue: wordList) {
			// -- Find each word in Block/Lines to get confidence
			Word foundWord = findWord(wordValue);
			if (foundWord != null) {
//				System.out.println("### Word "+ foundWord.WordValue+" confidence = "+foundWord.WordOCRConfidence);
				if (lowest > 0 && foundWord.WordOCRConfidence.contains("0")) lowest = 0;
				else if (lowest > 1 && foundWord.WordOCRConfidence.contains("1")) lowest = 1;
				else if (lowest > 2 && foundWord.WordOCRConfidence.contains("2")) lowest = 2;
				else if (lowest > 3 && foundWord.WordOCRConfidence.contains("3")) lowest = 3;
				else if (lowest > 4 && foundWord.WordOCRConfidence.contains("4")) lowest = 4;
				else if (lowest > 5 && foundWord.WordOCRConfidence.contains("5")) lowest = 5;
				else if (lowest > 6 && foundWord.WordOCRConfidence.contains("6")) lowest = 6;
	//			else if (lowest > 7 && foundWord.WordOCRConfidence.contains("7")) lowest = 7; // -- no need to check if this is high
	//			else if (lowest > 8 && foundWord.WordOCRConfidence.contains("8")) lowest = 8; // -- no need to check if this is high
			}
		}
//		System.out.println("### Lowest OCR char = "+lowest);

		if (lowest <= 3) return "low";
		else if (lowest <= 6) return "medium";
		else return "high";
	}

	private Word findWord(String searchWord) {
		for (Line line: LineList) {
			for (Word word: line.WordList) {
				if (word.WordValue.equals(searchWord))
					return word;
			}
		}
		return null;
	}
	
	@JsonIgnore      
	public String getTextLocation(String phrase) {
		// @BusinessVerbalization("the location of {0} in {this}")
		// @BusinessDocumentation("Returns the minimal bounding box of the phrase as \"[X,Y,W,H]\"")
		// -- find the bounding box of the specified words. These may be line wrapped so add all together
		// -- returns string in format "[X,Y,Width, Height]" where Height is measured down the page from the Y point
		int X = 100000; // -- as we are looking for minimum X
		int Y = 100000; // -- as we are looking for minimum X
		int XMax = 0; // -- as we are looking for max X
		int YMax = 0;// -- as we are looking for max Y
		
		String[] wordList = phrase.split(" ");
		System.out.println("### Found "+wordList.length+" words in: "+phrase);
		for (String wordValue: wordList) {
			// -- Find each word in Block/Lines to get location
			Word foundWord = findWord(wordValue);
			if (foundWord != null) {
				if (foundWord.WordStartX < X) X = foundWord.WordStartX; 
				if (foundWord.WordStartY < Y) Y = foundWord.WordStartY; 
				if (foundWord.WordStartX+foundWord.WordWidth > XMax) XMax = foundWord.WordStartX+foundWord.WordWidth; 
				if (foundWord.WordStartY+foundWord.WordHeight > YMax) YMax = foundWord.WordStartY+foundWord.WordHeight; 
			}
		}

		return "["+ X +","+ Y +","+ (XMax-X) +","+ (YMax-Y) +"]";
	}

	//------------------------------------------
	@JsonIgnore  
	public String getCompleteText() {
			// -- Auto Verbalises as       the complete text of {this}
		if(blockTextCache != null) {
			return blockTextCache; // -- Already initialised so return it.
		}
		else {  // -- First access time so build string from all words 
			blockTextCache = "";
			for (Line l: LineList ) {
				for (Word w: l.WordList) {
					blockTextCache = blockTextCache + w.WordValue + " ";
				}
//				blockTextCache = blockTextCache + "\n";  // -- If you need to split on each line
			}
			blockTextCache = blockTextCache.trim(); // -- Remove leading or trailing spaces.
			return blockTextCache;
		}
	}

	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public int getNumberOfLines() {
		// -- Auto Verbalises as         the number of lines of {this}
		return LineList.length;
	}

	@JsonIgnore  
	public int getIndexOfLine(Line line) {
		// -- Manual         the index of {0} in {this}
		// -- Doc			returns the position index (0..n-1) of the line with the line list of the block 
		if (LineList == null || LineList.length == 0)
			return -1;
		for (int index = 0; index < LineList.length ; index++ ) {
				if (LineList[index].LineID.equals(line.LineID) ) {
					return index+1; // -- Uses coding numbering !!
				}
			}
		return -1;
	}

	@JsonIgnore    
	public int getNumberOfWords() {
		// -- Auto Verbalises as  - the number of words of {this}
		int count = 0;
		for (Line l: LineList ) {
			count = count + l.WordList.size();
		}
		return count;
	}
	
	@JsonIgnore     
	public String getFirstLineText() {
		// -- Auto Verbalises as    the first line of {this}
		if (lineTextCache != null) {
			return lineTextCache;
		}

		if (LineList == null 
				|| LineList[0].WordList == null 
				|| LineList[0].WordList.size() == 0) {
			lineTextCache = "";
			return lineTextCache;
		}
		
		lineTextCache = "";
		// -- Add each Word in List to the string with a space between each word.
		for (Word w: LineList[0].WordList) {
			lineTextCache = lineTextCache + w.WordValue + " ";
		}
		lineTextCache = lineTextCache.trim(); // -- Remove the trailing space we just added one
		return lineTextCache;		
	}

	public String lineXText(int lineNumber) {   // -- Business numbering 1..n
		// -- Manual BOM Verbalisation:     the text of line {0} of {this}
		// -- Manual BOM Documentation:     Returns all the Words of the chosen line (in range 1..n) concatenated together into a single string. An empty string is returned if any empty lists or bad line number is used.
		int lnum = lineNumber - 1;	// -- Business number to Java numbering
		String thisLineText = "";
		if (LineList == null 
				|| lineNumber > LineList.length
				|| lineNumber < 1
				|| LineList[lnum].WordList == null 
				|| LineList[lnum].WordList.size() == 0) {
			return "";
		}
		// -- Add each Word in Line to the string with a space between each word.
		for (Word w: LineList[lnum].WordList) {
			thisLineText = thisLineText + w.WordValue + " ";
		}
		thisLineText = thisLineText.trim(); // -- Remove the trailing space we just added
		return thisLineText;		
	}

	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public Line getFirstLine() {
		if (LineList != null && LineList.length > 0)
			return LineList[0];
		else
			return null; // -- Should never happen as a Block should always have at least one line ???
	}		

	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public String getLocation() {
		// -- Auto Verbalises as         the location of {this}  
		// -- Highlight Location box as per Adobe Acrobat URL parameters - https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/pdf_open_parameters.pdf#page=8 
		// -- i.e. highlight=lt,rt,top,btm      - left X, right X, Top Y, Bottom Y coords
		// -- TODO - Check that BACA page dimensions match PDF dimensions as they may need scaling and off setting
		// -- NOTE - This has not been tested      [top left X, tlY, bottom right X, brY]
		return "["+BlockStartX+", "+ BlockStartY+", "+ (BlockStartX + BlockWidth)+ ", "+ (BlockStartY + BlockHeight)+"]";
	}
}
