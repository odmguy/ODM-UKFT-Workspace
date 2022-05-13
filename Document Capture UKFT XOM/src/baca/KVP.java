package baca;

public class KVP
{
    public String Key;
    public String Value;
    public int    KeyStartX;
    public int    KeyStartY;
    public int    KeyHeight;
    public int    KeyWidth;
    public double KeyConfidence;  
    public int    ValueStartX;
    public int    ValueStartY;
    public int    ValueWidth;
    public int    ValueHeight;
    public double ValueConfidence;  
    public double Sensitivity;
    public String EditedValue;
    public String KeyClassConfidence;
    public String KeyClass;
    public String ValidatorResult;	// -- New with Object Validators will be "Pass"? or "Fail"
    public int    PageNumber;
    public String Mandatory;
    public boolean ObjectDetectionMatch; // -- New in CA 1.5

    public KVP() {}; // --  CONSTRUCTOR
    
}
			
			