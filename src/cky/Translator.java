package cky;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Translator {

	private Cell[][] table;
	private Map<String, List<String>> italianGrammar = new TreeMap<String, List<String>>();

	public Translator() {
		italianGrammar.putAll(Parser.parserGrammar("assets\\GrammaticaItaliana.txt"));
	}

	public String algorithm(String sentence) {
		String[] words = sentence.toLowerCase().split(" ");
		applyCKY(words);

//		printTable();
		for (int n = 0; n < table[0][words.length - 1].size(); n++)
			if (table[0][words.length - 1].getValue(n).equalsIgnoreCase("S")) {
				
				Tree tree = table[0][words.length - 1].get(n);
//				tree.print("");
				Tree translate = translate(tree);
				
				if(translate != null)
					tree.addChild(translate, 0);
				
				return tree.findLeaves();
			}
		return "";
	}


	public Tree translate(Tree tree) {
		
		if(tree.getValue().equalsIgnoreCase("VP") && tree.getValue(0).equalsIgnoreCase("V") && !tree.getValue(1).equalsIgnoreCase("ADV"))
			return tree.remove(1);
		
		for (Tree child : tree.getChildren()){
			Tree translated = translate(child);
			if(translated != null)
				return translated;
		}
		
		return null;
	}
	
	public void applyCKY(String[] words) {

		table = new Cell[words.length][words.length];
		
		for (int i = 0; i < words.length; i++)
			for (int j = 0; j < words.length; j++)
				table[i][j] = new Cell();

		// i = righe, j = colonne
		for (int i = 0; i < words.length; i++) {

			List<Tree> word = new ArrayList<Tree>();
			word.add(new Tree(words[i]));

			List<String> possibleTags = italianGrammar.get(words[i]);
			for (String tag : possibleTags)
				table[i][i].add(new Tree(tag, word));

			for (int j = i - 1; j >= 0; j--)
				for (int k = j + 1; k <= i; k++) {

					for (int n = 0; n < table[j][k-1].size(); n++)
						for (int m = 0; m < table[k][i].size(); m++) {
							List<String> tags = italianGrammar.get(table[j][k-1].getValue(n) + " "	+ table[k][i].getValue(m));

							if (tags != null) {
								for (String tag : tags) {
									List<Tree> children = new ArrayList<Tree>();
									children.add(table[j][k-1].get(n));
									children.add(table[k][i].get(m));
									table[j][i].add(new Tree(tag, children));
								}
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

		System.out.println(translator.algorithm("tu hai amici lì") + "\n");					//amici tu hai li
		System.out.println(translator.algorithm("noi siamo illuminati") + "\n");				//illuminati noi siamo
		System.out.println(translator.algorithm("tu avrai novecento anni di età") + "\n");		//novecento anni di eta tu avrai
		System.out.println(translator.algorithm("tu hai molto da apprendere ancora") + "\n");		//Molto da apprendere tu hai ancora
		System.out.println(translator.algorithm("il futuro di questo ragazzo è nebuloso") + "\n");	//Nebuloso il futuro di questo ragazzo è
		System.out.println(translator.algorithm("Paolo corre veloce") + "\n");				// Veloce Paolo corre

//		translator.printTable();
	}

}
