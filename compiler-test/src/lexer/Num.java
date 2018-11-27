package lexer;

public class Num extends Token {
	public int value;
	
	public Num(int v) {
		super(Tag.NUM);
		this.value = v;
	}

	public String toString() {
		return ""+value;
	}
}
