package inter;

import lexer.Lexer;

/**
 *  语法树的节点
 *  
 *  Expr
 *    Constant
 *    Logical
 *      Rel
 *  Stmt
 *    Seq
 *    While
 *    Do
 *    If
 *    Break
 *    Set
 *
 */
public class Node {
	int lexline = 0;

	public Node() {
		this.lexline = Lexer.line;
	}

	void error(String s) {
		throw new Error("near line " + lexline + ": " + s);
	}

	static int labels = 0;

	public int newlabel() {
		return ++labels;
	}

	public void emitlabel(int i) {
		System.out.print("L" + i + ":");
	}

	public void emit(String s) {
		System.out.println("\t" + s);
	}
}
