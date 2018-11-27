
package com.dl.algorithm;

import java.io.File;
import java.util.Stack;

public class RecursionTest {

	public static void main(String[] args) {
		System.out.println(f(3));
		System.out.println(f_1(3));
		
		Node root = initTree();
		previsit(root);
		previsit_1(root);
		
		traverseDir(new File("src"));
		traverseDir_1(new File("src"));
	}

	/**
	 * f(1)=2
	 * f(n)=f(n-1)*f(n-1)
	 */
	static int f(int x){
        if (x == 1)
            return 2;
        return f(x - 1)*f(x - 1);
	}
	
	static int f_1(int x){
		Stack<Integer> stack = new Stack<Integer>();
		for(int i=1;i<=x;i++){
			if (i == 1){
				stack.push(2);
			}else{
				int tmp = stack.pop();
				stack.push(tmp*tmp);
			}
		}
		return stack.pop();
	}
	
	static Node initTree(){
		Node node_a = new Node("a");
		Node node_b = new Node("b");
		Node node_c = new Node("c");
		Node node_d = new Node("d");
		Node node_e = new Node("e");
		Node node_f = new Node("f");
		Node node_g = new Node("g");
		Node node_h = new Node("h");
		Node node_i = new Node("i");
        //a-b-d
		//   -e-h
		//     -i
		// -c-f
		//   -g
        node_a.assignchild(node_b, node_c);
        node_b.assignchild(node_d, node_e);
        node_c.assignchild(node_f, node_g);
        node_e.assignchild(node_h, node_i);
        return node_a;
	}
	
	static void previsit(Node root){
		System.out.println(root);
		if(root.hasleftchild()){
			previsit(root.leftchild);
		}
		if(root.hasrightchild()){
			previsit(root.rightchild);
		}
	}
	
	static void previsit_1(Node root){
		Stack<Node> stack = new Stack<>();
		stack.push(root);
		while(stack.size()>0){
			Node node = stack.pop();
			System.out.println(node);
			if(node.hasrightchild()){
				stack.push(node.rightchild);
			}
			if(node.hasleftchild()){
				stack.push(node.leftchild);
			}
		}
	}
	
	static void traverseDir(File file){
		for (File f : file.listFiles()){
			if(f.isDirectory()){
				traverseDir(f);
			}else{
				System.out.println(f.getAbsolutePath());
			}
		}

	}
	
	static void traverseDir_1(File file){
		Stack<File> stack = new Stack<>();
		stack.push(file);
		while(stack.size()>0){
			File fi = stack.pop();
			for (File f : fi.listFiles()){
				if(f.isDirectory()){
					stack.push(f);
				}else{
					System.out.println(f.getAbsolutePath());
				}
			}
		}
	}
	
	public static class Node{
	    public String nodevalue;
	    public Node leftchild, rightchild;
	    public Node(){
	    }
	    public Node(String value){
	        nodevalue = value;
	    }
	    public void assignchild(Node left, Node right){
	        this.leftchild = left;
	        this.rightchild = right;
	    }
	    public boolean hasleftchild(){
            return (leftchild != null);
	    }
	    public boolean hasrightchild(){
	    	return (rightchild != null);
	    }
	    public String toString(){
	        return nodevalue;
	    }
	}
}
