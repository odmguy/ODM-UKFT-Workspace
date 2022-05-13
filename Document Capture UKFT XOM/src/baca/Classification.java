package baca;

//import javax.xml.bind.annotation.XmlElement;
//import com.google.gson.annotations.SerializedName;

public class Classification
{
//    @XmlElement(name = "Page Title")     public PageTitle PageTitle;
//	@SerializedName("Page Title")    public PageTitle PageTitle;
    public String[] DocumentLanguage;	// -- WAS singleton
	public PageTitle PageTitle;
    public DocumentClass DocumentClass;
    public String[] warnings;	// -- New in V1.3
    public Model Model; 	// -- Only appears after model learning/training

    public Classification() {}; // -- Empty Constructor 
    
}
			
			
