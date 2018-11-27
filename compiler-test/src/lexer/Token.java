package lexer;

/**
 * 词法单元
 *
 */
public class Token {
	public int tag;
	
	public Token(int t) {
		this.tag = t;
	}
	
	public String toString() {
		return ""+(char)tag;
	}
	
	
}
