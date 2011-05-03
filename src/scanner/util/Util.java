package scanner.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
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
	
	/**
	 * @param jArr jsonarray with strings inside
	 * @return hashset with the strings
	 */
	public static Set<String> asSet (JSONArray jArr){
		if(jArr == null)
			throw new IllegalArgumentException("jArr can't be null");
		Set<String> set = new HashSet<String>();
		for(int i = 0; i<jArr.length(); i++){
			try {
				set.add(jArr.getString(i));
			} catch (JSONException e) {
				//never
			}
		}
		return set;
	}
}
