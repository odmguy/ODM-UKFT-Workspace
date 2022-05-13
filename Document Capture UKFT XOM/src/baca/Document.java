package baca;

import com.fasterxml.jackson.annotation.JsonIgnore;

/***
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
***/

public class Document {
///// NOTE can use @JsonProperty("my-name") to convert any JSON name to a different Java attribute name.	

	// -- These 3 fields are not in the JSON downloaded from UI but they are in API payload
	// -- They are in the Model definitions on the Swagger UI page under CAJSONResponse
	public String analyzerId;	 
	public String createDate;	
	public String CustomerUniqueIdentifier;	

	public AIOutput[] AIOutput;  // -- TODO. Untested - will be WatsonInfo - List [ "Label", "Result" ] ??
	public Classification Classification;
	public String CustomerCode;
	public DSOutput[] DSOutput;
	public String DocumentArrivalTime;
	public String DocumentExtension;
	public String DocumentName;
	public double DocumentOCRConfidence;
	public Error[] ErrorList;
	public ExtraInformation ExtraInformation;
	public MandatoryInformation MandatoryInformation;
	public int UserID;
	public Page[] pageList;

	// -- CONSTRUCTOR --
	public Document() {}	
	
	// --------- Helpers ----------
	// -- @JsonIgnore is needed to stop this becoming a required field on the JSON input parameter object
	// -- and it will also be generated in the default payload in the REST tester in the RES console and cause an error!!!
	@JsonIgnore
	public String getActualClassification() {
		// -- Auto Verbalises as          {actual classification} of {this}
		return Classification.DocumentClass.Actual;
	}

	@JsonIgnore
	public String getCompleteText() {
		// -- Auto Verbalises as          the complete text of {this}
		if (DSOutput == null || DSOutput.length == 0)
			return "";
		else
			return DSOutput[0].Content;
	}

	@JsonIgnore
	public int getNumberOfPages() {
		// -- Auto Verbalises as          the number of pages of {this}
		return ExtraInformation.pages;
	}
	
	
	/************
	public static Document createFromJSONString(String jsonString) {
		ObjectMapper objectMapper = new ObjectMapper();
		Document doc = null;
		try {
			doc = objectMapper.readValue(jsonString, Document.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

*****************/	
	
/*********
 	// -- TODO 
	// *******************************************************
	//    Multi-Column Detection Stuff
	//	  For each Page in turn - check if the captured text lines were originally in multiple columns and try to split them out.
	// *******************************************************
	public void parseMultiColumns() {
		
		PageList page;
		Block block;
		for (int pageindex = 0; pageindex < pageList.length; pageindex++) {
			page = pageList[pageindex];
			// -- Init column widths. Columns may not be symmetrical
			page.leftColumnXStart = 0;
			page.leftColumnXEnd = 0;
			page.rightColumnXStart = page.PageInfo.PageWidth;
			page.rightColumnXEnd = page.PageInfo.PageWidth;
			int headerFactor = page.PageInfo.PageHeight/20; // -- % of page indicating header/footer

			for (int blockindex = 0; blockindex < page.BlockList.length; blockindex++) {
				block = page.BlockList[blockindex];
				if (Integer.parseInt(block.BlockStartY) > headerFactor 
						&& Integer.parseInt(block.BlockStartY) < (page.PageInfo.PageWidth - headerFactor)) {  
					// -- Ignore headers and footers near page end
					
				} 
				
			} // -- End of Block Loop
			
		} // -- End of Page Loop
		
	} // -- End of parseMultiColumns
**********/	
	

}
