package scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import scanner.token.Token;

public class ScannerInvoker {

	/**
	 * @param args input & config
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
			while((token=scanner.next()) != null){
				tokensToWrite.add(token);
				System.out.println(token.toStringComplete());
			}
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
