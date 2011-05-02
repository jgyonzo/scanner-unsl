package scanner.java;

import static scanner.util.Util.readFileAsString;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import scanner.token.Token;

/**
 * Analizador lexicografico para java (simplificado)
 * - Automatas y lenguajes - UNSL - 2011
 * @author jgyonzo, msoria
 */
public class JavaScanner {
	private List<Token> tokensFound;
	private String currentState;
	private String cola;
	private JSONObject delta;
	private JSONObject stateActions;
	
	public JavaScanner() throws Exception{
		tokensFound = new ArrayList<Token>();
		cola = "";
		currentState = "q0";
		JSONObject cfg = new JSONObject(readFileAsString("javaconf.json"));
		delta = cfg.getJSONObject("delta");
		stateActions = cfg.getJSONObject("stateActions");
	}
	
	public List<Token> scan(String input) throws Exception{
		if(input == null || (input!=null && input.equals(""))){
			return tokensFound;
		}
		
		int pos = 0;
		while(pos < input.length()){
			Character charAt = input.charAt(pos++);
			String currentChar = charAt.toString();
			
			if(delta.getJSONObject(currentState).has(currentChar)){
				String nextState = delta.getJSONObject(currentState).getString(currentChar);
				currentState = nextState;
				if(stateActions.has(currentState)){
					JSONObject actions = stateActions.getJSONObject(currentState);
					if(actions.has("push") && actions.getBoolean("push")){
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
}
