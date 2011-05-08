package scanner.java;

import static org.junit.Assert.assertTrue;
import static scanner.util.Util.addAll;
import static scanner.util.Util.isEqual;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import scanner.token.Token;

public class JavaFileScannerTest {

	JavaFileScanner scanner = null;
	Token id = new Token("ID", "");
	Token classK = new Token("class","");
	Token eos = new Token("EOS","");
	Token str = new Token("STRING","");
	Token incStr = new Token ("STR_INC","");
	//ops
	Token add = new Token("SUMA","");
	Token sub = new Token("RESTA","");
	Token autoInc = new Token("AUTOINC","");
	Token addAs = new Token("SUMASSIGN","");
	Token equal = new Token("EQUAL","");
	Token assign = new Token("ASSIGN","");

	@Test
	public void testId() {
		try {
			String input = "myvar";
			reInitScan(input);
			id.setValue(input);
			List<Token> expected = addAll(id);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIdMaxLen() {
		try {
			String input = "myvarrrrrrrrrrruo";
			reInitScan(input);
			id.setValue("myvarrrrrrrrrrru");
			List<Token> expected = addAll(id);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKeyword() {
		try {
			String input = "class";
			reInitScan(input);
			List<Token> expected = addAll(classK);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEos() {
		try {
			String input = ";";
			reInitScan(input);
			List<Token> expected = addAll(eos);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testString(){
		try {
			String input = "\"esto es un string\"";
			reInitScan(input);
			str.setValue("esto es un string");
			List<Token> expected = addAll(str);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIncString(){
		try {
			String input = "\"esto es un string \n";
			reInitScan(input);
			incStr.setValue("esto es un string ");
			List<Token> expected = addAll(incStr);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOps(){
		try {
			String input = "+ - ++ += == = +++";
			reInitScan(input);
			List<Token> expected = addAll(add,sub,autoInc,addAs,equal,assign,autoInc,add);
			List<Token> result = scanner.getAll();
			assertTrue("Expected=" + expected + " Result=" + result,
					isEqual(expected, result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void reInitScan(String input) throws Exception {
		if (scanner != null) {
			scanner.closeFile();
		}
		StringReader reader = new StringReader(input);
		scanner = new JavaFileScanner(reader);
	}
}
