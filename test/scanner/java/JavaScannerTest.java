package scanner.java;

import static org.junit.Assert.assertTrue;
import static scanner.util.Util.addAll;
import static scanner.util.Util.*;

import java.io.IOException;
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
	
	@Test
	public void testString(){
		String input = "String \"esto es un string\"$";
		Token key = new Token("KEY","String");
		Token blk = new Token("BLK",null);
		Token string = new Token("STRING","esto es un string");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(key, blk, string, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringInc(){
		String input = "String \"esto es un string\n$";
		Token key = new Token("KEY","String");
		Token blk = new Token("BLK",null);
		Token string = new Token("STR_INC","esto es un string");
		Token eol = new Token("EOL",null);
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(key, blk, string, eol, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringTab(){
		String input = "String \"esto es un 	string\"$";
		Token key = new Token("KEY","String");
		Token blk = new Token("BLK",null);
		Token string = new Token("STRING","esto es un 	string");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(key, blk, string, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEol() throws IOException{
		String input = "int lalnala \n";
		Token key = new Token("KEY","int");
		Token blk = new Token("BLK",null);
		Token id = new Token("ID","lalnala");
		Token eol = new Token("EOL",null);
		List<Token> expected = addAll(key, blk, id, blk, eol);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTab() throws IOException{
		String input = "int lalnala 	";
		Token key = new Token("KEY","int");
		Token blk = new Token("BLK",null);
		Token id = new Token("ID","lalnala");
		Token tab = new Token("TAB",null);
		List<Token> expected = addAll(key, blk, id, blk, tab);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSuma()  throws IOException{
		String input = "aa + b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token suma = new Token("SUMA",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, suma, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSumaSuma()  throws IOException{
		String input = "aa ++ b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token suma = new Token("AUTOINC",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, suma, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSumaIgual()  throws IOException{
		String input = "aa += b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token suma = new Token("SUMAASIGN",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, suma, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testResta()  throws IOException{
		String input = "aa - b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token resta = new Token("RESTA",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, resta, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMul()  throws IOException{
		String input = "aa * b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token mul = new Token("MUL",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, mul, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLess()  throws IOException{
		String input = "aa < b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token less = new Token("LESSER",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, less, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAssign()  throws IOException{
		String input = "aa = b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token assign = new Token("ASSIGN",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, assign, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOR()  throws IOException{
		String input = "aa || b3$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token or = new Token("OR",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, or, blk, id2, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBrackets()  throws IOException{
		String input = "aa = (b3)$";
		Token id = new Token("ID","aa");
		Token blk = new Token("BLK",null);
		Token assign = new Token("ASSIGN",null);
		Token braop = new Token("BRACKETOPEN",null);
		Token bracl = new Token("BRACKETCLOSE",null);
		Token id2 = new Token("ID","b3");
		Token eof = new Token("EOF",null);
		List<Token> expected = addAll(id, blk, assign, blk, braop, id2, bracl, eof);
		
		try {
			List<Token> result = scanner.scan(input);
			assertTrue("Expected=" + expected + " Result=" + result, isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
