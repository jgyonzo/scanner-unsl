package scanner.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scanner.token.Token;

/**
 * Analizador lexicografico para java (simplificado)
 * @author jgyonzo, msoria
 */
public class JavaScanner {
	private List<Token> tokensFound;
	private String currentState;
	private String cola;
	private Map<String,Map<Character,String>> delta;
	private Map<String,Map<String,Object>> stateActions;
	
	public JavaScanner(){
		tokensFound = new ArrayList<Token>();
		cola = "";
		currentState = "q0";
		initDelta();
		initStateActions();
	}
	
	public List<Token> scan(String input) throws Exception{
		if(input == null || (input!=null && input.equals(""))){
			return tokensFound;
		}
		
		int pos = 0;
		while(pos < input.length()){
			Character currentChar = input.charAt(pos++);
			
			if(delta.get(currentState).containsKey(currentChar)){
				String nextState = delta.get(currentState).get(currentChar);
				currentState = nextState;
				if(stateActions.containsKey(currentState)){
					Map<String,Object> actions = stateActions.get(currentState);
					if(actions.containsKey("push") && (Boolean)actions.get("push")){
						cola+=currentChar;
					} else { //estado final
						Token token = new Token();
						Boolean returnValue = (Boolean)actions.get("return_value");
						Boolean reset = (Boolean)actions.get("reset");
						token.setCode((String)actions.get("token_id"));
						if(returnValue)
							token.setValue(cola);
						if(reset){
							cola = "";
							pos--;
						}
						currentState = "q0";
						tokensFound.add(token);
					}
				}
			} else {
				System.out.println("Error en pos=" + pos);
				currentState = "q0";
				cola = "";
			}
		}
		return tokensFound;
	}
	
	private void initDelta(){
		delta = new HashMap<String,Map<Character,String>>();
		Map<Character,String> q0 = new HashMap<Character,String>();
		q0.put('a', "id");
		q0.put('b', "id");
		q0.put('c', "id");
		q0.put('0', "int");
		q0.put('1', "int");
		q0.put(' ', "blk_final");
		q0.put('$', "eof_final");
		q0.put(';', "eos_final");
		
		delta.put("q0", q0);
		
		Map<Character,String> id = new HashMap<Character,String>();
		id.put('a', "id");
		id.put('b', "id");
		id.put('c', "id");
		id.put('0', "id");
		id.put('1', "id");
		id.put(' ', "id_final");
		id.put('$', "id_final");
		id.put(';', "id_final");
		
		delta.put("id", id);
	}
	
	private void initStateActions(){
		stateActions = new HashMap<String,Map<String,Object>>();
		Map<String,Object> ida = new HashMap<String,Object>();
		ida.put("push", true);
		
		stateActions.put("id", ida);
		
		Map<String,Object> idf = new HashMap<String,Object>();
		idf.put("token_id", "ID");
		idf.put("reset", true);
		idf.put("return_value", true);
		
		stateActions.put("id_final", idf);
		
		Map<String,Object> blkf = new HashMap<String,Object>();
		blkf.put("token_id", "BLK");
		blkf.put("reset", false);
		blkf.put("return_value", false);
		
		stateActions.put("blk_final", blkf);
		
		Map<String,Object> eosf = new HashMap<String,Object>();
		eosf.put("token_id", "EOS");
		eosf.put("reset", false);
		eosf.put("return_value", false);
		
		stateActions.put("eos_final", eosf);
	}
}
