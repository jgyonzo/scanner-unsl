package scanner;

import static org.junit.Assert.assertTrue;
import static scanner.util.Util.addAll;
import static scanner.util.Util.isEqual;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import scanner.Scanner;
import scanner.token.Token;

public class HtmlFileScannerTest {
	
	Scanner scanner = null;
	Token text = new Token ("TEXT","");
	Token qtext = new Token ("QUOTEDTEXT","");
	Token claop = new Token ("CLAPSOPEN",null);
	Token clacl = new Token ("CLAPSCLOSE",null);
	Token bar = new Token ("BAR",null);
	
	
	
	@Test
	public void testId() {
		try {
			String input = "myvar";
			reInitScan(input);
			text.setValue(input);
			List<Token> expected = addAll(text);
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
		scanner = new Scanner(reader,"htmlconf.json");
	}

}
