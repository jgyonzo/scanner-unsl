package scanner.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import scanner.token.Token;


public class Prueba {
	public static void main(String[] args) throws Exception{
		String forscan = "aa b ";
		JavaScanner scanner = new JavaScanner();
		List<Token> tokens = scanner.scan(forscan);
		System.out.println(tokens);
		
//		String json = Prueba.readFileAsString("javaconf.json");
//		JSONObject jsonOb = new JSONObject(json);
//		JSONObject delta = (JSONObject)jsonOb.get("delta");
//		System.out.println(jsonOb.toString());
	}
	
	/**
	 * @param filePath
	 *            the name of the file to open
	 */
	private static String readFileAsString(String filePath)
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
}
