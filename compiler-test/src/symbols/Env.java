package symbols;

import java.util.Hashtable;

import inter.Id;
import lexer.Token;

/**
 * 符号表：标识符<->{类型，词法单元，offset，行号}
 *
 */
public class Env {
	private Hashtable<Token,Id> table;
	
	protected Env prev;
	
	public Env(Env n) {
		this.table = new Hashtable<>();
		this.prev = n;
	}
	
	public void put(Token w,Id i) {
		table.put(w, i);
	}
	
	public Id get(Token w) {
		for( Env e = this; e != null; e = e.prev ) {
			Id found = e.table.get(w);
			if( found != null ) return found;
		}
		return null;
	}
}
