package cky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Translator {

	private Cell[][] table;
	private Map<String, String> italianGrammar = new TreeMap<String, String>();
	private Map<String, String> yodaGrammar = new TreeMap<String, String>();

	public Translator() {
		yodaGrammar.putAll(Parser.parserGrammar("assets\\GrammaticaYoda.txt"));
		italianGrammar.putAll(Parser.parserGrammar("assets\\GrammaticaItaliana.txt"));
	}

	public String algorithm(String sentence) {
		String[] words = sentence.split(" ");
		applyCKY(words);

//		printTable();
		for (int n = 0; n < table[0][words.length - 1].size(); n++)
			if (table[0][words.length - 1].getValue(n).equalsIgnoreCase("S")) {

//				System.out.println("ITALIANO");
//				table[0][words.length - 1].get(n).print("");
				List<Tree> trees = new ArrayList<Tree>();
				for (int i = 0; i < table.length; i++) 
					trees.add(table[i][i].get(0));
				
				Tree tree = table[0][words.length - 1].get(n);
				translate(tree);
//				Tree tree = translate(trees);
				
//				System.out.println("\nYODA");
//				tree.print("");
				return tree.findLeaves();
			}
		return "";
	}


	public void translate(Tree tree) {
		
		if(tree.getChildren().isEmpty())
			return;
		
		// prima regola: se ho VP[V, x], tolgo x da VP e lo attacco al padre di VP
		
		Tree removed = null;
		int pos = -1;
		for(int i=0; i<tree.getChildren().size(); i++)
			
			if(tree.getValue(i).equalsIgnoreCase("VP")) {
				removed = tree.getChildren().get(i).remove(1);
				pos = i;
			}
		if(removed != null && pos != -1)
			tree.addChildren(removed,pos+1);
		
		// seconda regola: se ho PRON VP, inserisco Pron in VP
		
		int index = -1;
		for(int i=0; i<tree.getChildren().size()-1; i++)
			if(tree.getValue(i).equalsIgnoreCase("Pron") && tree.getValue(i+1).equalsIgnoreCase("VP"))
				index = i;
	
		if(index != -1)
			tree.getChildren().get(index+1).addChildren(tree.remove(index),0);
		
		// terza regola: VP lo metto sempre alla fine dell'albero
		Tree moved = null;
		for(int i=0; i<tree.getChildren().size(); i++)
			if(tree.getValue(i).equalsIgnoreCase("VP"))
				moved = tree.remove(i);
		
		if(moved != null)
			tree.addChildren(moved);
		
		
		// quarta regola: se ho NP[x, ADV], tolgo ADV da NP e lo attacco al padre di NP
		
		Tree removedADV = null;
		for(int i=0; i<tree.getChildren().size(); i++)
			
			if(tree.getValue(i).equalsIgnoreCase("NP") && tree.getChildren().get(i).getValue(1).equalsIgnoreCase("ADV")) {
				removedADV = tree.getChildren().get(i).remove(1);
			}
		if(removedADV != null )
			tree.addChildren(removedADV);
		
		
		
		
		tree.print("");
		System.out.println("\n");
		
		for (Tree child : tree.getChildren()) 
			translate(child);
		
		
		
	}

	
	/*public Tree translate(List<Tree> trees) {

		System.out.println(trees);
		if (trees.get(0).getValue().equalsIgnoreCase("s"))
			return trees.get(0);

		List<Tree> partial = new ArrayList<Tree>();
		for (int i = trees.size() - 1; i > 0; i--) {
			try {

				String head = yodaGrammar.get(trees.get(i - 1).getValue() + " " + trees.get(i).getValue());
				if (head.equalsIgnoreCase("s") && !partial.isEmpty()) {
					partial.add(trees.get(i));
					partial.add(trees.get(i - 1));
					i--;
					continue;
				}

				List<Tree> children1 = new ArrayList<Tree>();
				children1.add(trees.get(i - 1));
				children1.add(trees.get(i));
				Tree tree1 = new Tree(head, children1);
				partial.add(tree1);
				i--;
			} catch (Exception e) {
				try {
					String head = yodaGrammar.get(trees.get(i).getValue() + " " + trees.get(i - 1).getValue());
					if (head.equalsIgnoreCase("s") && !partial.isEmpty()) {
						partial.add(trees.get(i - 1));
						partial.add(trees.get(i));
						i--;
						continue;
					}

					List<Tree> children2 = new ArrayList<Tree>();
					children2.add(trees.get(i));
					children2.add(trees.get(i - 1));
					Tree tree2 = new Tree(head, children2);
					partial.add(tree2);
					i--;
				} catch (Exception e1) {
					partial.add(trees.get(i));
				}
			}
			if (i == 1)
				partial.add(trees.get(0));

		}
		Collections.reverse(partial);
		return translate(partial);
	}*/
	public void applyCKY(String[] words) {

		table = new Cell[words.length][words.length];
		
		for (int i = 0; i < words.length; i++)
			for (int j = 0; j < words.length; j++)
				table[i][j] = new Cell();

		// i = righe, j = colonne
		for (int i = 0; i < words.length; i++) {

			List<Tree> word = new ArrayList<Tree>();
			word.add(new Tree(words[i]));

			table[i][i].add(new Tree(italianGrammar.get(words[i]), word));

			for (int j = i - 1; j >= 0; j--)
				for (int k = j + 1; k <= i; k++) {

					for (int n = 0; n < table[j][k-1].size(); n++)
						for (int m = 0; m < table[k][i].size(); m++) {
							String head = italianGrammar.get(table[j][k-1].getValue(n) + " "	+ table[k][i].getValue(m));

							if (head != null) {
								List<Tree> children = new ArrayList<Tree>();
								children.add(table[j][k-1].get(n));
								children.add(table[k][i].get(m));
								table[j][i].add(new Tree(head, children));
							}
						}
				}
		}

	}

	public void printTable() {

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.print(table[i][j].getValue(0) + "  ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {

		Translator translator = new Translator();

		System.out.println(translator.algorithm("tu hai amici lì"));					//amici tu hai li
	//	System.out.println(translator.algorithm("noi siamo illuminati"));				//illuminati noi siamo
//		System.out.println(translator.algorithm("tu avrai novecento anni di età"));		//novecento anni di eta tu avrai
		
		//translator.printTable();
	}

}
