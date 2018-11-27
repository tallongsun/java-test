package lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

import symbols.Type;

/**
 * 词法分析
 *
 */
public class Lexer {
	public static int line = 1;
	
	private char peek = ' ';
	
	private Hashtable<String,Word> words = new Hashtable<>();
	
	private File file = new File("file.txt");
	private Reader reader = null;
	
	private void reserve(Word w) {
		words.put(w.lexeme, w);
	}
	
	
	private void readch() throws IOException{
		this.peek = (char)reader.read();
	}
	
	private boolean readch(char c) throws IOException{
		readch();
		if (peek != c)
			return false;
		peek = ' ';
		return true;
	}
	
	public Lexer() {
		try {
			reader = new InputStreamReader(new FileInputStream(file));
		}catch (Exception e) {
		}
		
		reserve(new Word("if",Tag.IF));
		reserve(new Word("else",Tag.ELSE));
		reserve(new Word("while", Tag.WHILE));
		reserve(new Word("do", Tag.DO));
		reserve(new Word("break", Tag.BREAK));
		reserve(Word.True);
		reserve(Type.Bool);
		reserve(Word.False);
		reserve(Type.Char);
		reserve(Type.Int);
		reserve(Type.Float);
	}
	
	public Token scan()  throws IOException{
		//略过空白
		for(;;readch()) {
			if(peek == ' ' || peek == '\t') {
				continue;
			}else if(peek == '\n') {
				line++;
			}else {
				break;
			}
		}
		
		//识别复合token：二目表达式或ASCII字符
		switch (peek) {
		case '&':
			if (readch('&')) {
				return Word.and;
			} else {
				return new Token(peek);
			}
		case '|':
			if (readch('|')) {
				return Word.or;
			} else {
				return new Token('|');
			}
		case '=':
			if (readch('=')) {
				return Word.eq;
			} else {
				return new Token('=');
			}
		case '!':
			if (readch('=')) {
				return Word.ne;
			} else {
				return new Token('!');
			}
		case '<': {
			if (readch('=')) {
				return Word.le;
			} else {
				return new Token('<');
			}
		}
		case '>':
			if (readch('=')) {
				return Word.ge;
			} else {
				return new Token('>');
			}
		}

		//识别数字：整数或小数
		if (Character.isDigit(peek)) {
			int v = 0;
			do {
				v = 10 * v + Character.digit(peek, 10);
				readch();
			} while (Character.isDigit(peek));
			if (peek != '.') {
				return new Num(v);
			}
			float x = v;
			float d = 10;
			while (true) {
				readch();
				if (!Character.isDigit(peek))
					break;

				x = x + Character.digit(peek, 10) / d;
				d = d * 10;
			}
			return new Real(x);
		}
		
		//识别字符串：预留字或标识符
		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(peek);
				readch();
			} while (Character.isLetterOrDigit(peek));
			String s = b.toString();
			Word w = (Word) words.get(s);
			if (w != null) {
				return w;
			}
			w = new Word(s, Tag.ID);
			words.put(s, w);
			return w;
		}
		
		//识别ASCII字符
		Token tok = new Token(peek);
		peek = ' ';

		return tok;
	}

	
	
	public static void main(String[] args) throws Exception{
		Lexer lexer = new Lexer();
		while(true) {
			Token token = lexer.scan();
			if(token.tag == 65535) {//EOF
				break;
			}
			System.out.println(token);
		}
	}
}
