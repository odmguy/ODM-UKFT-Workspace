package baca;

public class ExtraInformation
{
	public String author;
    public String creator; // Could be String or large int with/without minus sign e.g.  -50648889600000
    // -- ODM is OK when a number is used instead of a string but BAW is NOT as the Discovered business objects throw an error
    // -- Best solution for now it to set the ExtraInformation to {} before it is parsed to the discovered object
    public String producer; // -- Could be String or large int
    public String creationdate; //-- BAW does not like int64's in REST calls generated when a 'long' is used
    public String moddate;      //-- BAW does not like int64's in REST calls generated when a 'long' is used
    // -- TODO - sort out dates as this will lose information. Could also add helpers to convert number to a real Date
    // --   the two dates were 'int's in v1.2 but that loses data. TODO to add helpers to convert to real dates 
    public String tagged;
    public String userproperties;
    public String suspects;
    public String form;
    public String javascript;
    public int 	  pages;
    public String encrypted;
    public String page_size;
    public double page_rot;
    public String mediabox;
    public String cropbox;
    public String bleedbox;
    public String trimbox;
    public String artbox;
    public String file_size;
    public String optimized;
    public double pdf_version;
// -- These occasionally appear !!! 
    public String title;	// -- examples!!!:  "Invoice Example v2.xlsx", 1627776000000
	public String subject; // -- Only appears in very few documents
    public String keywords;	// -- Rare occurrence in odd documents. 

    // ----- CONSTRUCTOR -----
    public ExtraInformation() {
    }
    
}
			
			