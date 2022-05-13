package baca;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class TableRow {

	public String RowID;
	public double RowStartX;
	public double RowStartY;
	public double RowHeight;
	public double RowWidth;
	public List<Cell> CellList = new ArrayList<Cell>();

	// -- No args constructor for use in serialization
	public TableRow() {
	}

	// ----- Utility methods ----------
	
	@JsonIgnore     // -- To stop JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public String getCompleteText() {
		// -- TODO : cache this string if this method is likely to be called often.
		String text = "";
		for (Cell c: CellList) {
				text = text + c.getCompleteText() + ", ";
		}
		return text.trim();
	}
	

	@JsonIgnore     // -- To stop JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	// -- Complex Tables may have gaps/merged columns that change across the table rows
	// -- For BACA 1.5 it looks like if the value in the 2nd column is empty you will get "cell_0" and "cell_2" ids
	// -- So this method will return the contents of "cell_<index-1>" if it exists to convert business index to code 
	// -- i.e. the cell in the 3rd column will be "cell_2" (as the first column is "cell_0"
	// -- note that this behaviour may change between versions but there will probably never be a BACA 1.6
	
	public String getCompleteTextOfCell(int index) { // -- Business term so expecting range 1..n (not 0..n)
		// -- Manual verbalize as the complete text of cell {0} in {this}
		// -- TODO : cache this string if this method is likely to be called often.
		System.out.println("TableRow.getCompleteTextOfCell index value = "+index+ " Number of cells in "+RowID+" = "+ CellList.size());
		if (index == 0) { // -- 6 elements internally stored as 0..5 so asking for 6th is valid
			System.out.println( "[WARNING] TableRow.getCompleteTextOfCell called with an index value of 0. Cell indexes in rules should be in Business terms i.e. 1..n not 0..n. Returning empty string");
			return "";
		} else {
			for (Cell c : CellList) {
				if (c.CellID.equals("cell_"+ (index-1) )) {
					System.out.println("TableRow.getCompleteTextOfCell found it. Text = "+c.getCompleteText() );
					return c.getCompleteText();
				}
			}
			System.out.println("TableRow.getCompleteTextOfCell Not found - returning \"\"");
			return "";
		}
	}
	
/********	Code suitable for BACA version 1.4
	// -- #### NOTE ####
	// -- Complex Tables may have gaps/merged columns that change across the table rows
	// -- So one row may have only 4 cells that have IDs of cell_0, cell_1, cell_5, cell_6
	// -- This methods extracts values using the index in the array so a business 'index' of 3 will return the 'cell_5' contents
	// --    but on the next row of the table it may return the contents of 'cell_2' if it exists !!!!
	public String getCompleteTextOfCell(int index) {  // -- Business term so expecting range 1..n  (not 0..n)
		// -- Manual verbalize as      the complete text of cell {0} in {this}
		// -- TODO : cache this string if this method is likely to be called often.
		if (index == 0) {	// -- 6 elements internally stored as 0..5 so asking for 6th is valid
			System.out.println("[WARNING] TableRow.getCompleteTextOfCell called with an index value of 0. Cell indexes in rules should be in Business terms i.e. 1..n not 0..n. Returning empty string");
			return "";
		}
		else if (index > CellList.size() ) 	// -- 6 elements internally stored as 0..5 so asking for 6th is valid
			return "";
		return CellList.get(index-1).getCompleteText();
	}
***********/
	
	/***************
	// -- Earlier version that retrieved the nth cell regardless of any gaps.
	// --  Note that this behaviour changes over version so this may be better for CA v1.5
		public String getCompleteTextOfCell(int index) {  // -- Business term so expecting range 1..n  (not 0..n)
			// -- Manual verbalize as      the complete text of cell {0} in {this}		
			// -- TODO : cache this string if this method is likely to be called often.
			if (index == 0) {	// -- 6 elements internally stored as 0..5 so asking for 6th is valid
				System.out.println("[WARNING] TableRow.getCompleteTextOfCell called with an index value of 0. Cell indexes in rules should be in Business terms i.e. 1..n not 0..n. Returning empty string");
				return "";
			}
			else if (index > CellList.size() ) 	// -- 6 elements internally stored as 0..5 so asking for 6th is valid
				return "";
			return CellList.get(index-1).getCompleteText();
		}
	*********/	
	
	@JsonIgnore      
	// =================
	// -- Return the position number of this row in range 1..n
	// --    We don't have the table handle (but we could pass it in in another method
	// --    So we can get the index from the rowID as they seem to be sequential in BACA 1.5 e.g. "row_0", "row_1"
	// =================
	public int getIndex() {  
		// -- Auto verbalizes as        the index of {this} 
		// -- @BusinessVerbalization("Gets the business index of the row from the rowID e.g. 'row_0' returns 1")
		String indexString = RowID.substring(4); // -- char after the '_'
		return Integer.parseInt(indexString)+1;
	}	

	@JsonIgnore      
	public int getIndexOfCell(Cell cell) {  // -- Return the position number of the cell in this row in range 1..n
		// -- Manual verbalize as        the index of {0,<cell>} in {this} 
		//		System.out.println("TableRow.getIndexOfCell - BUSINESS Index of "+ cell.CellID +" = "+ (CellList.indexOf(cell)+1));
		return CellList.indexOf(cell)+1;
	}	
	
	@JsonIgnore     
	public Cell getFirstCell() {
		if (CellList != null && CellList.size() > 0)
			return CellList.get(0);
		else {
			System.out.println("### TableRow.getFirstCell - has no cells !!!");
			return null; // -- Should never happen as a Row should always have at least one Cell ???
		}
	}		

	@JsonIgnore      
	public Cell getCellFromIndex(int index) {
		// @BusinessDocumentation("Indexes are in the range 1..n. So the first cell is at index 1.")
		// -- the cell at column {0} in {this}
		if (CellList == null || CellList.size() < index)
			return null;
		else
			return CellList.get(index-1); // -- Converts "business" numbering 1..n to "code" numbering 0.. 
	}		
	
	
	
	@JsonIgnore      
	// --    Get the cell to the right of 'cell' if there is one - else return null
	public Cell getNextCell(Cell cell) {
		// -- Manual verbalize as      the next cell to {0,<cell>} in {this}
		int index = CellList.indexOf(cell);
		if (index == -1 || index == CellList.size() ) {
			System.out.println("[WARNING] TableRow.getNextCell - Cell not found or final cell - Returning null");
			return null;
		}
		return CellList.get(index+1);
	}
	
	
	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public String getLocation() {
		// -- Auto Verbalises as         the location of {this}  
		
		// -- TODO - Check that BACA page dimensions match PDF dimensions as they may need scaling and off setting
		// -- NOTE - This has not been tested    [top left X, tlY, bottom right X, brY]
		return "["+RowStartX+", "+ RowStartY+", "+ (RowStartX + RowWidth)+ ", "+ (RowStartY + RowHeight)+"]";
	}

}
