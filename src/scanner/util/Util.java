package scanner.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import scanner.token.Token;

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
	
	public static boolean isEqual(List<scanner.token.Token> tokens, List<Token> tokens2) {
		if(tokens == null || tokens2 == null){
			throw new IllegalArgumentException();
		}
		
		if(tokens.isEmpty() && tokens2.isEmpty()){
			return true;
		}
		
		if(tokens.size() != tokens2.size()){
			return false;
		}
		
		for(int i = 0; i< tokens.size(); i++){
			if(!(tokens.get(i).equals(tokens2.get(i)))){
				return false;
			}
		}
		return true;
	}
	
	public static List<Token> addAll(Token... tokens){
		if(tokens == null){
			throw new IllegalArgumentException();
		}
		List<Token> result = new ArrayList<Token>();
		for(Token t : tokens){
			result.add(t);
		}
		return result;
	}
}
