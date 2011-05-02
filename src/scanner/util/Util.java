package scanner.util;

import java.io.BufferedReader;
import java.io.FileReader;

import org.codehaus.jettison.json.JSONObject;

public class Util {
	
	/**
	 * @param filePath
	 *            the name of the file to open
	 */
	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
	public static JSONObject asJson(Object o) throws ClassCastException{
		if(o != null && o instanceof JSONObject){
			return (JSONObject)o;
		}
		throw new ClassCastException("Cannot convert this object to JSONObject");
	}
}
