package cky;

import java.util.ArrayList;
import java.util.List;

public class Cell {

	List<Tree> trees = new ArrayList<Tree>();
	
	public void add(Tree tree){
		trees.add(tree);
	}

	public int size() {
		return trees.size();
	}

	public String getValue(int n) {
		try{
			return trees.get(n).getValue();
		} catch(Exception e){
			return "-";
		}
	}
	
	public Tree get(int n) {
		try{
			return trees.get(n);
		} catch(Exception e){
			return null;
		}
	}
	
	public void print(){
		System.out.println(trees);
	}
}
