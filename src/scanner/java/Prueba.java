package scanner.java;

import java.io.BufferedReader;
import java.io.FileReader;

import scanner.token.Token;


public class Prueba {
	public static void main(String[] args) throws Exception{
		
		JavaFileScanner scanner = new JavaFileScanner("prueba.txt");
		Token tok = null;
		do{
			tok = scanner.next();
			System.out.println(tok);
		}while(tok != null);
		
//		String forscan = readFileAsString("source.java");
//		JavaScanner scanner = new JavaScanner();
//		List<Token> tokens = scanner.scan(forscan);
//		System.out.println(tokens);
		
//		String jsonArr = "[new,return,char]";
//		JSONArray ja = new JSONArray(jsonArr);
//		JSONObject jo = ja.toJSONObject(ja);
//		System.out.println(jo);
		
//		String json = Prueba.readFileAsString("javaconf.json");
//		JSONObject jsonOb = new JSONObject(json);
//		JSONObject delta = (JSONObject)jsonOb.get("delta");
//		System.out.println(jsonOb.toString());
	}
}
