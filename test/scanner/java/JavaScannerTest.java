package scanner.java;

import static org.junit.Assert.assertTrue;
import static scanner.util.Util.addAll;
import static scanner.util.Util.isEqual;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import scanner.token.Token;

public class JavaScannerTest {

	private JavaScanner scanner;
	
	@Before 
	public void setUp(){ 
		try{
			scanner = new JavaScanner();
		}catch(Exception e){
			System.out.println("Error inicializando scanner");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testId(){
		String input = "myvar$";
		Token t1 = new Token("ID","myvar");
		Token t2 = new Token("EOF",null);
		List<Token> expected = addAll(t1,t2);
		try {
			List<Token> result = scanner.scan(input);
			assertTrue(isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIdMaxLen(){
		String input = "myvarrrrrrrrrrruo 10$";
		Token t1 = new Token("ID","myvarrrrrrrrrrru");
		Token t2 = new Token("BLK",null);
		Token t3 = new Token("INT","10");
		Token t4 = new Token("EOF",null);
		List<Token> expected = addAll(t1,t2, t3, t4);
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result,isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKeyword(){
		String input = "int myvar$";
		Token t0 = new Token("KEY","int");
		Token t1 = new Token("BLK",null);
		Token t2 = new Token("ID","myvar");
		Token t3 = new Token("EOF",null);
		List<Token> expected = addAll(t0, t1, t2, t3);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue(isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKeywordEos(){
		String input = "int myvar;$";
		Token t0 = new Token("KEY","int");
		Token t1 = new Token("BLK",null);
		Token t2 = new Token("ID","myvar");
		Token t3 = new Token("EOS",null);
		Token t4 = new Token("EOF",null);
		List<Token> expected = addAll(t0, t1, t2, t3, t4);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue(isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNumId(){
		String input = "10a$";
		Token t0 = new Token("INT","10");
		Token t1 = new Token("ID","a");
		Token t2 = new Token("EOF",null);
		List<Token> expected = addAll(t0, t1, t2);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue(isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
