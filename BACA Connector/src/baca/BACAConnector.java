package baca;
/**
 * @author Andrew Macdonald
 *
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BACAConnector {
	
	// -- inputs --
							   	// -- Copy the Request URL from the Content Analyzer API page	
	private String serviceURL; 	// -- e.g. https://bacademo-mt47.bpm.ibmcloud.com/backendbaca1/ca/rest/content/v1
	private String apiKey;		// -- Copy this from the Content Analyzer API page
	private String basicAuth;  	// -- The Base64 encoded FID username and password
								// -- e.g. Basic YW5keW1hYy5madDQ0DE6VGhzcclZ4cDFCQVFRNaWkxPQ0ZteGd5Z1UycVpZaQ0F2RDZtZA
	
	// -- outputs --
	public String analyzerDocID; // -- The unique key generated for each uploaded doc
	public String responseJSON;  // -- The full response from the different REST calls
	
	// -- internal --
	public String processingOptions =  "\"ocr\", \"dc\", \"kvp\",\"sn\",\"mt\",\"hr\",\"th\",\"ds\"";
	private OkHttpClient client;
	
	// -----------------------------------------
	// ------------- Constructors --------------
	// -----------------------------------------
	public BACAConnector() {
		client = new OkHttpClient().newBuilder().build();
	}

	public BACAConnector(String url, String key, String auth) {
		serviceURL = url;
		apiKey = key;
		basicAuth = auth;
		client = new OkHttpClient().newBuilder().build();
	}

	
	// -----------------------------------------
	// ------------- Methods -------------------
	// -----------------------------------------
	// -- Get the most recent responseJSON which is stored for all calls
	// -- Should only be needed for debugging issues
	public String getLastResponse() {
		return responseJSON;
	}

	//=========================================
	//   SUBMIT document to BACA from Base 64 string of the content retrieved from ECMDocument info 
	//       Returns analyzerID of the document
	//=========================================
	public String submitDocumentContent(String url, String key, String auth, String content) throws IOException {

		byte[] decoder = Base64.getDecoder().decode(content);
		// -- Need to create temp file from base64 data.
		File f = null;
		FileOutputStream fos=null;
		try {
			f = File.createTempFile("upload", ".pdf");
			fos = new FileOutputStream(f);
			f.deleteOnExit();
			fos.write(decoder);
			System.out.println(f.getPath());
		} catch (IOException e1) {
			e1.printStackTrace();
			return "";
		};
		
		MediaType mediaType = MediaType.parse("multipart/form-data");
		RequestBody body = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("responseType", "\"json\"")
				.addFormDataPart("jsonOptions", processingOptions)
		        .addFormDataPart("file", "upload.pdf", RequestBody.create(mediaType, f))
				.build();
		
		Request request = new Request.Builder()
				.url(url+"/contentAnalyzer")
				.method("POST", body)
				.addHeader("Content-Type", "multipart/form-data")
				.addHeader("Accept", "application/json")
				.addHeader("apiKey", key)
				.addHeader("Authorization", auth)
				.build();
		try {
			Response response = client.newCall(request).execute();
			responseJSON = response.body().string();
			analyzerDocID = "<unset>";
			// -- GET ANALYZERID from response string 
			Pattern p = Pattern.compile("(?:\"analyzerId\":\")(.*?)(?:\")"); 
			Matcher m = p.matcher(responseJSON);
			if (m.find()) {
				String match = m.group();
				if (m.groupCount() > 0) {// -- Return the group rather than the whole match (to remove non capturing groups)
					analyzerDocID = m.group(1); // -- Groups start at 1 !!!
				}	
			}					
		} catch (IOException e) {
			e.printStackTrace();
		}
		fos.close();
		return analyzerDocID;
	}
	
	//=========================================
	//   SUBMIT document to BACA from local path name 
	//		This is unlikely to be possible in most container/server contexts so use ByteStream method instead
	//       Returns analyzerID of the document
	//=========================================
	public String submitDocumentFilename(String url, String key, String auth, String filename) throws IOException {
//		MediaType mediaType = MediaType.parse("multipart/form-data");
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("file", filename, RequestBody.create(MediaType.parse("application/octet-stream"), new File(filename)))
				.addFormDataPart("responseType", "\"json\"")
				.addFormDataPart("jsonOptions", processingOptions).build();
		Request request = new Request.Builder()
				.url(url+"/contentAnalyzer")
				.method("POST", body).addHeader("Content-Type", "multipart/form-data")
				.addHeader("Accept", "application/json")
				.addHeader("apiKey", key)
				.addHeader("Authorization", auth)
				.build();
		Response response = client.newCall(request).execute();
		responseJSON = response.body().string();
		analyzerDocID = "<unset>";

		// -- GET ANALYZERID from response string 
/***
		{"status":{"code":200,"messageId":"CIWCA11109","message":"Successfully filtered JSON output"},
		"data":{"analyzerId":"672c90d0-c714-11ec-9707-bdeb19cc5fc8","createDate":"2022-04-28T16:58:24.305Z",
		"CustomerUniqueIdentifier":"","Classification":{"DocumentLanguage":["English"],"PageTitle":...
***/		
		Pattern p = Pattern.compile("(?:\"analyzerId\":\")(.*?)(?:\")"); 
		Matcher m = p.matcher(responseJSON);
		if (m.find()) {
			String match = m.group();
			if (m.groupCount() > 0) {// -- Return the group rather than the whole match (to remove non capturing groups)
				analyzerDocID = m.group(1); // -- Groups start at 1 !!!
			}	
		}		
		return analyzerDocID;
	}
	
	//=========================================
	//   STATUS CHECK the processing completion status 
	//=========================================
	public String checkProgressJSON(String url, String key, String auth, String docID) {
		Request request = new Request.Builder()
				  .url(url + "/contentAnalyzer/"+docID)
				  .get()   // -- same as   '.method("GET", null)' ??
				  .addHeader("Accept", "application/json")
				  .addHeader("apiKey", key)
				  .addHeader("Authorization", auth)
				  .build();
		Response response;
		responseJSON = "<unset>";
		try {
			response = client.newCall(request).execute();
			responseJSON = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return responseJSON;
	}

	//=========================================
	//   STATUS CHECK the processing completion status as a percentage 0-100.0
	//=========================================
	public double checkProgressPercentage(String url, String key, String auth, String docID) {
		
		checkProgressJSON(url, key, auth, docID);  // -- Need to call this each time to get latest status rather than use last cached version 

		// -- Get the progress of the first doc from string e.g.    ...4729 s","completedPages":2,"progress":"100.00"}]}}
		Pattern p = Pattern.compile("(?:\"progress\":\")(.*)(?:\")"); 
		Matcher m = p.matcher(responseJSON);
		if (m.find()) {
			String match = m.group();
			if (m.groupCount() > 0) {// -- Return the group rather than the whole match (to remove non capturing groups)
				match = m.group(1); // -- Groups start at 1 !!!
			}	
		    return Double.parseDouble(match);		
		}
		else {
			return -1.0; // -- Some kind of error occurred
		}
	}

	//=========================================
	//   GET the full Document JSON payload from BACA
	//=========================================
	public String getJSON(String url, String key, String auth, String docID) {
		String responseJSON = "";
		
		Request request = new Request.Builder()
				  .url(url + "/contentAnalyzer/"+docID +"/json")
				  .get()   // -- same as   '.method("GET", null)' ??
				  .addHeader("Accept", "application/json")
				  .addHeader("apiKey", key)
				  .addHeader("Authorization", auth)
				  .addHeader("Cache-Control", "no-cache")
				  .build();
		Response response;
		try {
			response = client.newCall(request).execute();
			responseJSON = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseJSON;
	}
	
	
	//=========================================
	//   DELETE the doc and payload from BACA
	//=========================================
	public String deleteJSON(String url, String key, String auth, String docID) {
		String result = "";
		MediaType mediaType = MediaType.parse("text/plain");
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
		  .addFormDataPart("responseType","json")
		  .build();
		Request request = new Request.Builder()
				  .url(url + "/contentAnalyzer/"+analyzerDocID)
				  .method("DELETE", body)
				  .addHeader("Accept", "application/json")
				  .addHeader("apiKey", key)
				  .addHeader("Authorization", auth)
//				  .addHeader("Cache-Control", "no-cache")
				  .build();
		Response response;
		try {
			response = client.newCall(request).execute();
			result = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	//=========================================
	//   Test for BAW jar refresh
	//=========================================
	public String test() {
		return "OK";
	}
	
}

