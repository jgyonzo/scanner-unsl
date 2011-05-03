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
}
