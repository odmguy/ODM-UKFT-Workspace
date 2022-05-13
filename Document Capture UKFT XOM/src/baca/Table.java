package baca;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Table {

	// -- Content Analyser JSON fields
	public String TableID;
	public double TableStartX;
	public double TableStartY;
	public double TableHeight;
	public double TableWidth;
	public List<TableRow> RowList = new ArrayList<TableRow>();

	// -- No argument constructor for use by ODM
	public Table() {};
	
	
	@JsonIgnore     // -- To stop this JavaBeans 'get' appearing as an attribute in the Generated JSON parameter 
	public TableRow getFirstRow() {
		// -- Auto Verbalises as         the first row of {this}  
		if (RowList != null && RowList.size() > 0)
			return RowList.get(0);
		else
			return null;
	}

	@JsonIgnore      
	public TableRow getLastRow() {
		// -- Auto Verbalises as         the last row of {this}  
		if (RowList != null && RowList.size() > 0)
			return RowList.get( RowList.size() - 1 ); // -- 10 items implies indexes 0..9
		else
			return null;
	}

	@JsonIgnore     // -- Business method so using 1..n numbering 
	public TableRow getRowX(int index) {
		// -- Manual         the row {0,<nth>} of {this}
		if (RowList == null || RowList.size() < index) // -- 5 items in 0..4 so asking for 5th is OK any more = out of bounds
			return null;
		
		if (index == 0) index = 1; // Rule authors should not be using 0. 'index' should be passed in as 1 for first item as we will subract 1 from it below
		if (index > RowList.size()) index = RowList.size(); // -- if there are 10 rows and they ask for 15 then return last one i.e. 10th

		return RowList.get( index - 1 ); // -- 10 items implies indexes 0..9 so first item is at position get(0)
	}

	@JsonIgnore     // -- Business method so using 1..n numbering 
	public Cell getCellToRight(int offset, Cell cell, TableRow row) {
		// -- Manual   the cell {0} to right of {1,<cell>} in {2,<row>} of {this}
		boolean DBG = false;
		if (DBG) System.out.println("Table.getCellToRight: Offset="+offset+" to to right of cell:"+cell.getCompleteText()+ " in [code]row:"+RowList.indexOf(row));
		Cell nextCell = null;
		// -- Find row with max number of cells. This is only used for the Column coordinates, we are not using the cell values
		int maxRowIndex = -1;
		int maxCellCount = 0;
		for (int loop = RowList.size()-1; loop > 0; loop--) { // -- work backwards so we find the earliest max row
			if (RowList.get(loop).CellList.size() >= maxCellCount) {
				maxCellCount = RowList.get(loop).CellList.size();
				maxRowIndex = loop;
			}
		}
		if (DBG) System.out.println("Table.getCellToRight: MAX row at 'code' index: "+maxRowIndex);
		TableRow maxRow = RowList.get(maxRowIndex); 
		// -- If the cell is in the Max row then simply get the correct cell
		if (maxRow.CellList.indexOf(cell) != -1) {   // -- Cell is in this row
			nextCell = maxRow.CellList.get( maxRow.CellList.indexOf(cell) + offset); 
			if (DBG) System.out.println("Table.getCellToRight: Cell is on MAX row - Returning cell with contents = "+nextCell.getCompleteText());
			return nextCell;
		}
		// -- Using MAXrow Get the column X coord of the current Cell and the target cell
//		double currentX = 0;
		double targetX = 0;
		int currentIndex = -1;
		int targetIndex = -1;
		for (Cell c: maxRow.CellList) {
			if (c.CellStartX == cell.CellStartX) {
				// -- Found column for current cell
				// currentX = c.CellStartX;
				currentIndex = maxRow.getIndexOfCell(c) - 1;  // -- Subtract 1 as this is a business function
				if (DBG) System.out.println("Table.getCellToRight: Found real current cell column at [code]index "+ currentIndex);
				// -- Get Column of target cell
				targetIndex = currentIndex + offset;
				if (targetIndex > (maxRow.CellList.size() - 1) ) {
					System.out.println("[WARNING]Table.getCellToRight: requested cell is beyond the rightmost column!!!");
					return cell; // -- TODO : Returning original cell to avoid crash. May want to return null instead
				}
				targetX = maxRow.CellList.get(targetIndex).CellStartX;
			}
		}
		if (targetIndex == -1 || currentIndex == -1) System.out.println("[WARNING]Table.getCellToRight: FAILED to find one/both columns!!!");

		// -- We have Target column so search each row in table from current row upwards and return first cell we find in target column
		int startRow = RowList.indexOf(row); 
		for (int rloop = startRow; rloop > 0; rloop--) {
			if (DBG) System.out.println("Table.getCellToRight:   Checking [code]Row "+ rloop+ " for a cell with targetX coord");
			for (Cell c: RowList.get(rloop).CellList) {
				if (c.CellStartX == targetX) {
					// -- Found a Cell in the target column. Job done so return it.
					if (DBG) System.out.println("Table.getCellToRight: Found target cell in [code]Row "+ rloop+ ", Cell contents = "+ c.getCompleteText());
						return c;
				}
				if (DBG) System.out.println("Table.getCellToRight: Target cell NOT found in [code]Row "+ rloop);
			}
		}
		// -- Should never really hit this unless the X coords vary across rows 
		return cell; // -- TODO : Returning original cell to avoid crash. May want to return null instead
	}

	
	// -- Extra Helper fields - Work in Progress
/**************	
	protected Vector<Vector<String>> cellValues;
	public int numRows() {
		return RowList.size();
	}
	public int numColumns() {
		return RowList.get(0).CellList.size();
	}
	protected void initialiseCellValues() {
		// -- Note this code assumes there are no gaps in the column headers but there may be in the row cell values
		if (cellValues == null) cellValues = new Vector<Vector<String>>();
		RowList headerRow = RowList.get(0);
		
		for (int row = 0; row < numRows(); row++) {
			RowList currentRow = RowList.get(row);
			for (int col = 0; col < currentRow.CellList.size(); col++) {
				// -- Note the actual number of cells in a data row may be fewer than the number of Header columns
				// -- If this is the case then we need to insert spaces to pad out the missing cell values
				CellList currentCell = currentRow.CellList.get(col);
				if (currentCell.CellStartX == headerRow.CellList.get(col).CellStartX) {
					// -- Cell position matches header row
					
				}
				else {
					// -- Cell position DOES NOT match header row so insert empty cells until we find correct column
					
				}
			}
		}
	}

	public String getCell(int row, int col) {
		return "";
	}
*********/	
	
}
