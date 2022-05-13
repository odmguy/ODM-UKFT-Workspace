package baca;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cell {
	// -- Cells are used in the "CellList" list in the TableRow class which are in turn  used in the RowList list in the Table class

	public String CellID;
	public double CellStartX;
	public double CellStartY;
	public double CellWidth;
	public double CellHeight;
	public List<Line> LineList; 
	
	public List<CellHeaderAttributes> CellHeaderAttributes = new ArrayList<CellHeaderAttributes>(); 

	public Cell() {
	}

	// ----- Utility methods ----------
	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public Line getFirstLine() {
		if (LineList != null && LineList.size() > 0)
			return LineList.get(0);
		else
			return null; // -- Should never happen as a Cell should always have at least one line ???
	}		


	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public String getCompleteText() {
		// -- TODO : cache this string if this method is likely to be called often.
		String text = "";
		for (Line l: LineList) {
			for (Word w: l.WordList) {
				text = text + w.WordValue + " ";
			}
		}
		return text.trim();
	}

	public String lineXText(int lineNumber) {   // -- Business numbering 1..n
		// -- Manual BOM Verbalisation:     the text of line {0} of {this}
		// -- Manual BOM Documentation:     Returns all the Words of the chosen line (in range 1..n) concatenated together into a single string. An empty string is returned if any empty lists or bad line number is used.
		int lnum = lineNumber - 1;	// -- Business number to Java numbering
		String thisLineText = "";
		if (LineList == null 
				|| lineNumber > LineList.size()
				|| lineNumber < 1
				|| LineList.get(lnum).WordList == null 
				|| LineList.get(lnum).WordList.size() == 0) {
			return "";
		}
		// -- Add each Word in Line to the string with a space between each word.
		for (Word w: LineList.get(lnum).WordList) {
			thisLineText = thisLineText + w.WordValue + " ";
		}
		thisLineText = thisLineText.trim(); // -- Remove the trailing space we just added
		return thisLineText;		
	}
	
	
	
	// -- IT WAS TOO DANGEROUS TableRows may have cell gaps e.g. cell_0, cell_1, cell_4
	// -- So we can't use this technique to find the actual number cell index ("cardinality") in the row
	// -- Use the new tableRow.getCellIndex or tableRow.getNextCell methods instead
/**********	
	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public int getIndex() {  // -- Return the position number of the cell in this row in range 1..n
		// -- Auto verbalizes as     the index of <this>
		System.out.println("Cell.getIndex returning ID from '"+CellID.substring(5)+"'");
		return  Integer.parseInt(CellID.substring(5))+1;
	}
**********/


}
