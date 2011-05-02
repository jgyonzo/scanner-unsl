package scanner.token;

public class Token {
	private String code;
	private String value;
	
	@Override
	public String toString(){
		return String.format("token:[code: %s,value: %s]",code,value);
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
