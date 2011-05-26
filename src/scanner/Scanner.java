package scanner;

import static scanner.util.Util.asSet;
import static scanner.util.Util.isFinal;
import static scanner.util.Util.readFileAsString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import scanner.token.Token;

/**
 * Generic Scanner - Automatas y Lenguajes 2011
 * @author jgyonzo-msoria
 */
public class Scanner {
	private String currentState;
	/**
	 * used to push character of a lexeme
	 */
	private String cola;
	/**
	 * transitions: state x char -> state
	 */
	private JSONObject delta;
	/**
	 * semantic actions associated to states
	 */
	private JSONObject stateActions;
	/**
	 * map with (token_name,numeric_code)
	 */
	private JSONObject tokenCodes;
	private Set<String> keyWords;
	private BufferedReader sourceReader;
	private int pos;
	private int line = 1;
	private int column = 0;
	private String currentChar;
	
	/**
	 * Opens a reader with the given filename
	 * @param sourceFile complete filename of the source to be scanned
	 */
	public Scanner(String sourceFile, String confFile) throws Exception{
		try{
			sourceReader = new BufferedReader(new FileReader(sourceFile));
		} catch(IOException e) {
			String msj = "Error opening source file named: " + sourceFile;
			throw new Exception(msj,e);
		}
		try{
			init(confFile);
		}catch(Exception e){
			String msj = "Error during initializacion of scanner - init()";
			throw new Exception(msj,e);
		}
	}
	
	/**
	 * Use this if you wanna read from other source.
	 * If your source is a file, you may use the other overload
	 * @param reader reader pointing to the input to be scanned
	 */
	public Scanner(Reader reader, String confFile) throws Exception{
		sourceReader = new BufferedReader(reader);
		try{
			init(confFile);
		}catch(Exception e){
			String msj = "Error during initializacion of scanner - init()";
			throw new Exception(msj,e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void init(String confFile) throws Exception{
		pos = 0;
		cola = "";
		currentState = "q0";
		keyWords = new HashSet<String>();
		JSONObject cfg = new JSONObject(readFileAsString(confFile));
		delta = cfg.getJSONObject("delta");
		stateActions = cfg.getJSONObject("stateActions");
		tokenCodes = cfg.getJSONObject("token_codes");
		
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
	/**
	 * closes the source reader
	 * @throws IOException
	 */
	public void closeFile() throws IOException{
		sourceReader.close();
	}
	
	/**
	 * Mark current pos in the reader and then reads a char from it
	 * @return next char in the reader as int, -1 if EOF
	 * @throws IOException
	 */
	private int getNextChar() throws IOException{
		pos++;
		sourceReader.mark(20);
		return sourceReader.read();
	}
	
	/**
	 * Reset the reader to the last mark
	 * @throws IOException
	 */
	private void goBack() throws IOException{
		if("\n".equals(currentChar)){
			line--;
		}
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
				currentChar = "·"; //character to simulate EOF
			}else{
				currentChar = Character.valueOf((char)charint).toString();
			}
			
			countLineAndCol(currentChar);
			
			//processing the char
			if (delta.getJSONObject(currentState).has(currentChar)) {
				String nextState = delta.getJSONObject(currentState).getString(currentChar);
				currentState = nextState;
				if (stateActions.has(currentState)) {
					JSONObject actions = stateActions.getJSONObject(currentState);
					if (isFinal(currentState)) {
						// ejecuta las acciones de estado final
						Boolean returnValue = actions.has("return_value") && actions.getBoolean("return_value");
						Boolean reset = actions.has("reset") && actions.getBoolean("reset");
						Boolean goBack = actions.has("go_back") && actions.getBoolean("go_back");
						Boolean checkKeyWord = actions.has("check_keyword") && actions.getBoolean("check_keyword");
						Boolean ignore = actions.has("ignore") && actions.getBoolean("ignore");
						Boolean returnNull = actions.has("null_token") && actions.getBoolean("null_token");
						Boolean checkLen = actions.has("check_length") && actions.getBoolean("check_length");
						
						if(returnNull){
							return null; //EOF reached
						}
						if(ignore){ //shouldn't return this token, ignore and epislon to q0
							doEpsilon();
							continue;
						}
						token = new Token();
						token.setCode((String) actions.get("token_id"));
						if (returnValue) { //this token should return value (lexeme in cola)
							if (checkLen) { //we have to check the length of the lexeme
								Integer maxLen = actions.getInt("max_length");
								if (cola.length() > maxLen) {
									cola = cola.substring(0, maxLen);
									System.out.println("WARNING: element longer than max_length at line="+ (line-1));
								}
							}
							token.setValue(cola);
						}
						if (reset) { //cola = ""
							resetLexeme();
						}
						if (goBack) { //reprocess char (pos--)
							goBack();
						}
						if (checkKeyWord && keyWords.contains(token.getValue())) { //we have to check if this lexeme is a keyword
							token.setCode(token.getValue());
							token.setValue("");
						}
						token.setNumCode(tokenCodes.getInt(token.getCode()));
						doEpsilon();
					} else {
						// ejecuta las acciones de estado no final
						if (actions.has("push") && actions.getBoolean("push")) //push the current char to the queue
							cola += currentChar;
						else if(actions.has("special_push") && actions.getBoolean("special_push")){//Special case for html
							boolean isKey = keyWords.contains(cola);
							if(isKey){ //check if what I read till now is a keyword
								token = new Token();
								token.setCode(cola);
								token.setValue("");
								token.setNumCode(tokenCodes.getInt(token.getCode()));
								resetLexeme();
								doEpsilon();
							}else{
								cola += currentChar;
							}
						}
					}
				}
			} else {
				doEpsilon();
				System.out.println("ERROR at line=" + line + " column=" + column);
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
	
	private void countLineAndCol(String curChar){
		if("\n".equals(curChar)){
			line++;
			column = 0;
		}else{
			column++;
		}
	}
}