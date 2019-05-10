package cky;

import java.util.ArrayList;
import java.util.List;

public class Tree {

	private String value = "-";
	private List<Tree> children = new ArrayList<Tree>();
	
	public Tree() {

	}

	public Tree(String value){
		this.value = value;
	}
	
	public Tree(String value, List<Tree> children) {
		this.value = value;
		this.children = children;
	}
	
	public String getValue() {
		return value;
	}
	
	public List<Tree> getChildren() {
		return children;
	}
	
	public Tree getChild(int i) {
		return children.get(i);
	}
	
	
	public Tree remove(int i) {
		return children.remove(i);
	}
	
	public String getValue(int i) {
		try {
			return children.get(i).getValue();
		} catch(Exception e) {
			return "";
		}
	}
	
	public void addChildren(Tree tree) {
		children.add(tree);
	}
	
	public void addChildren(Tree tree, int i) {
		children.add(i, tree);
	}
	
	public void print(String space){
		System.out.println(space + value);

		for (Tree child : children)
			child.print("   "+space);
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public String findLeaves(){
		if(children.isEmpty())
			return value + " ";
		
		String text = "";
		for (Tree child : children)
			text += child.findLeaves();
		
		return text;
	}

}
