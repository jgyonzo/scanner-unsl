package scanner.java;

import static scanner.util.Util.asSet;
import static scanner.util.Util.isFinal;
import static scanner.util.Util.readFileAsString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import scanner.token.Token;

public class JavaFileScanner {
	private String currentState;
	private String cola;
	private JSONObject delta;
	private JSONObject stateActions;
	private Set<String> keyWords;
	private BufferedReader sourceReader;
	private int pos;
	
	@SuppressWarnings("unchecked")
	public JavaFileScanner(String sourceFile) throws Exception{
		
		try{
			sourceReader = new BufferedReader(new FileReader(sourceFile));
		} catch(IOException e) {
			String msj = "Error opening source file named: " + sourceFile;
			throw new Exception(msj,e);
		}
		
		pos = 0;
		cola = "";
		currentState = "q0";
		keyWords = new HashSet<String>();
		JSONObject cfg = new JSONObject(readFileAsString("javaconf.json"));
		delta = cfg.getJSONObject("delta");
		stateActions = cfg.getJSONObject("stateActions");
		
		//expand alias
		if(cfg.has("alias")){
			JSONObject alias = cfg.getJSONObject("alias");
			Iterator<String> deltaIt = delta.keys();
			JSONObject expanDelta = new JSONObject();
			while(deltaIt.hasNext()){
				String stateFrom = deltaIt.next();
				JSONObject transitionJson = delta.getJSONObject(stateFrom);
				JSONObject expanState = new JSONObject();
				Iterator<String> stateIt = transitionJson.keys();
				while(stateIt.hasNext()){
					String w = stateIt.next();
					String stateTo = transitionJson.getString(w);
					if(alias.has(w)){ //la transicion es un alias, expando
						JSONArray expanList = alias.getJSONArray(w);
						int len = expanList.length();
						for(int i = 0; i < len; i++){
							String s = expanList.getString(i);
							expanState.put(s, stateTo);
						}
					} else {
						expanState.put(w, stateTo);
					}
				}
				expanDelta.put(stateFrom, expanState);
			}
			delta = expanDelta;
		}
		
		//keywords
		if(cfg.has("key_words")){
			JSONArray keyw = cfg.getJSONArray("key_words");
			keyWords = asSet(keyw);
		}
	}
	
	public void closeFile() throws IOException{
		sourceReader.close();
	}
	
	/**
	 * Mark current pos in the reader and then reads a char from it
	 * @return next char in the reader in int form, -1 if EOF
	 * @throws IOException
	 */
	private int getNextChar() throws IOException{
		pos++;
		sourceReader.mark(100);
		return sourceReader.read();
	}
	
	/**
	 * Reset the reader to the last mark
	 * @throws IOException
	 */
	private void goBack() throws IOException{
		pos--;
		sourceReader.reset();
	}
	
	/**
	 * reset the current lexeme
	 */
	private void resetLexeme(){
		cola = "";
	}
	
	/**
	 * executes an epsilon to the initial state
	 */
	private void doEpsilon(){
		currentState = "q0";
	}
	
	/**
	 * @return null if EOF token reached, next token otherwise
	 * @throws Exception
	 */
	public Token next() throws Exception {
		Token token = null;
		//iterate till I find a token or EOF
		while (token == null) {
			
			//read next char and detect EOF
			int charint = getNextChar();
			if(charint == -1){
				return null;
			}
			String currentChar = Character.valueOf((char)charint).toString();
			
			//processing the char
			if (delta.getJSONObject(currentState).has(currentChar)) {
				String nextState = delta.getJSONObject(currentState).getString(currentChar);
				currentState = nextState;
				if (stateActions.has(currentState)) {
					JSONObject actions = stateActions.getJSONObject(currentState);
					if (isFinal(currentState)) {
						// ejecuta las acciones de estado final
						token = new Token();
						token.setCode((String) actions.get("token_id"));
						Boolean returnValue = actions.getBoolean("return_value");
						Boolean reset = actions.getBoolean("reset");
						Boolean goBack = actions.getBoolean("go_back");
						Boolean checkKeyWord = actions.getBoolean("check_keyword");
						if (returnValue) {
							Boolean checkLen = actions.getBoolean("check_length");
							if (checkLen) {
								Integer maxLen = actions.getInt("max_length");
								if (cola.length() > maxLen) {
									cola = cola.substring(0, maxLen);
									System.out.println("WARNING: element longer than max_length configured at pos="+ pos);
								}
							}
							token.setValue(cola);
						}
						if (reset) {
							resetLexeme();
						}
						if (goBack) {
							goBack();
						}
						if (checkKeyWord && keyWords.contains(token.getValue())) {
							token.setCode("KEY");
						}
						doEpsilon();
					} else {
						// ejecuta las acciones de estado no final
						if (actions.has("push") && actions.getBoolean("push"))
							cola += currentChar;
					}
				}
			}
		}
		return token;
	}
	
	/**
	 * @return all tokens found in sourceFile
	 * @throws Exception
	 */
	public List<Token> getAll() throws Exception{
		List<Token> tokens = new ArrayList<Token>();
		Token token = null;
		while((token=next()) != null){
			tokens.add(token);
		}
		return tokens;
	}
}
