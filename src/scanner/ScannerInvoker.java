package scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import scanner.token.Token;

/**
 * Main for Scanner - Automatas y Lenguajes 2011
 * @author jgyonzo-msoria
 */
public class ScannerInvoker {

	/**
	 * @param args path to input source & path to config file
	 */
	public static void main(String[] args) {
		if(args == null || (args != null && args.length < 2)){
			throw new IllegalArgumentException("Please, specify input file and conf file");
		}
		String input = args[0];
		String conf = args[1];
		try{
			Scanner scanner = new Scanner(input,conf);
			Token token = null;
			List<Token> tokensToWrite = new ArrayList<Token>();
			//get all tokens recognized
			while((token=scanner.next()) != null){
				tokensToWrite.add(token);
				//print only for info purpose
				System.out.println(token.toStringComplete());
			}
			scanner.closeFile();
			//write all tokens to salida.tok
			if(!tokensToWrite.isEmpty()){
				File output = new File("salida.tok");
				if(output.exists()){
					output.delete();
				}
				output.createNewFile();
				FileUtils.writeLines(output, tokensToWrite, "\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
