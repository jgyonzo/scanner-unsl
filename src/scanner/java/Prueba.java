package scanner.java;

import java.util.List;

import scanner.token.Token;


public class Prueba {
	public static void main(String[] args) throws Exception{
		String forscan = "aa.b10 ";
		JavaScanner scanner = new JavaScanner();
		List<Token> tokens = scanner.scan(forscan);
		System.out.println(tokens);
	}
}
