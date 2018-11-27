package lexer;

public class Real extends Token{
	public float value;
	
	public Real(float v) {
		super(Tag.REAL);
		this.value = v;
	}
	
	public String toString() {
		return ""+value;
	}
}
