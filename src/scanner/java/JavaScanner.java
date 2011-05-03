package scanner.java;

import static scanner.util.Util.asSet;
import static scanner.util.Util.readFileAsString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
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
	private Set<String> keyWords;
	
	@SuppressWarnings("unchecked")
	public JavaScanner() throws Exception{
		tokensFound = new ArrayList<Token>();
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
						//ejecuta las acciones de estado no final
						cola+=currentChar;
					} else { //ejecuta las acciones de estado final
						Token token = new Token();
						token.setCode((String)actions.get("token_id"));
						Boolean returnValue = actions.getBoolean("return_value");
						Boolean reset = actions.getBoolean("reset");
						Boolean checkKeyWord = actions.getBoolean("check_keyword");
						if(returnValue)
							token.setValue(cola);
						if(reset){
							cola = "";
							pos--;
						}
						if(checkKeyWord && keyWords.contains(token.getValue())){
							token.setCode("KEY");
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
