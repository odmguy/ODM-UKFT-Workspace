package baca;

import ilog.rules.bom.annotations.BusinessName;

public class LineItemGroup {
	
	// -- Public fields that will auto parse from the BACA JSON
	public int LineItemGroupID;
	public String[] Headers;
	public LineItem[] LineItems;
	
	// ---- Utility Methods 
	public int numberOfColumns() {
		// --     {number of columns} of {this}
		return Headers.length;
	}
	
	public int numberOfRows() {
		// --     {number of rows} of {this}
		return LineItems.length;
	}
	public String cellValue(@BusinessName("row") int row, @BusinessName("col") int col) {
		if (row >= numberOfRows() || col >= LineItems[row].KVPs.length) return "";
		return LineItems[row].KVPs[col].Value;
	}

	public String cellValueFromHeader(@BusinessName("row") int row, @BusinessName("headerLabel") String header) {
		// -- Get the Value in 'row' [0..n-1] corresponding to the provided column header label 
		if (row >= numberOfRows() ) return "";
		for (int col = 0; col < Headers.length; col++) {
			if (Headers[col].equals(header)) {
				return LineItems[row].KVPs[col].Value;
			}
		}
		return "";
	}
	
	// -- Constructor --
	public LineItemGroup() {
	}
}

/********* ---- Sample output of Content Analyzer 1.3

			"TableLineItems": [
				{
					"TableID": "table_1",
					"LineItemGroups": [
						{
							"LineItemGroupID": 0,
							"Headers": [
								"Reference",
								"Description Generator Inc Model AB271638/32 (2019)",
								"Quantity 1"
							],
							"LineItems": [
								{
									"LineItemID": 0,
									"KVPs": [
										{
											"Key": "Reference",
											"Value": "XVS7312"
										}
									]
								},
								{
									"LineItemID": 1,
									"KVPs": [
										{
											"Key": "Reference",
											"Value": "XVS443"
										},
										{
											"Key": "Description Generator Inc Model AB271638/32 (2019)",
											"Value": "PPE Face Mask"
										},
										{
											"Key": "Quantity 1",
											"Value": "2"
										}
									]
								},
								{
									"LineItemID": 2,
									"KVPs": [
										{
											"Key": "Reference",
											"Value": "XVX223"
										},
										{
											"Key": "Description Generator Inc Model AB271638/32 (2019)",
											"Value": "Ear Defenders"
										},
										{
											"Key": "Quantity 1",
											"Value": "4"
										}
									]
								},
							]
						}
					]
				}
			],
**************/