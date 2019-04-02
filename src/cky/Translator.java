package cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.util.Pair;

public class Translator {

	private Tree[][] table;
	private Map<String, List<String>> ItalianGrammar = new TreeMap<String, List<String>>();
	private Map<String, List<String>> YodaGrammar = new TreeMap<String, List<String>>();
	private Set<String> rules = new TreeSet<String>();

	public void parserGrammar(String path) {
		try {
			Map<String, List<String>> grammar = new TreeMap<String, List<String>>();
			Scanner scanner = new Scanner(new File(path));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace("\r", "");

				if (line.contains("end."))
					break;

				if (line.contains(" -> ")) {
					String[] split = line.split(" ->");
					String head = split[0];

					if (!grammar.containsKey(head))
						grammar.put(head, new ArrayList<String>());

					String[] list = split[1].split(" ");
					String rule = "";

					for (String body : list) {
						// String body = string.split("\\[")[0];
						if (!body.equals("")) {
							rule += body + " ";
						}
					}
					grammar.get(head).add(rule.substring(0, rule.length() - 1));
				}
			}
			scanner.close();
			if (path.contains("Yoda"))
				YodaGrammar.putAll(grammar);
			else if (path.contains("Italian"))
				ItalianGrammar.putAll(grammar);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// for (String key : grammar.keySet()) {
		// 		System.out.println(key);
			// for (String string : grammar.get(key))
			// 		System.out.println("  " + string);
		// }
	}

	public String algorithm(String sentence) {

		String[] words = sentence.split(" ");

		table = new Tree[words.length][words.length];

		for (int i = 0; i < words.length; i++)
			for (int j = 0; j < words.length; j++)
				table[i][j] = new Tree();

		for (int i = 0; i < words.length; i++) {

			List<Tree> word = new ArrayList<Tree>();
			word.add(new Tree(words[i]));
			table[i][i] = new Tree(getHead(words[i]), word);

			rules.add(table[i][i].getValue() + " -> " + words[i]);

			for (int j = i - 1; j >= 0; j--)
				for (int k = j + 1; k <= i; k++) {
					String head = getHead(table[j][k - 1].getValue() + " "
							+ table[k][i].getValue());

					if (table[j][i].getValue().equals("-")
							&& !(head.equals("-"))) {
						List<Tree> children = new ArrayList<Tree>();
						children.add(table[j][k - 1]);
						children.add(table[k][i]);
						table[j][i] = new Tree(head, children);

						rules.add(head + " -> " + table[j][k - 1].getValue()
								+ " " + table[k][i].getValue());
					}

					// System.out.println(i + " " + j + " " + k + " " +
					// table[j][i].getValue() + " " + table[j][k-1].getValue() +
					// " " + table[k][i].getValue());
				}
		}

		if (table[0][words.length - 1].getValue().equals("S")) {
			List<Tree> leaves = new ArrayList<Tree>(); 
			for(int i = 0; i<table.length; i++)
				leaves.add(table[i][i]);

			Tree tree = translate(leaves);
			
			return tree.findLeaves();
		}
		return "";
	}

	private String getHead(String rule) {

		for (String key : ItalianGrammar.keySet()) {
			List<String> list = ItalianGrammar.get(key);
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).equalsIgnoreCase(rule))
					return key;
		}
		return "-";
	}
	
//	0 = non trovato 	1 = trovato nello stesso ordine 	2 = trovato in ordine inverso
	private Pair<String, Integer> findInGrammar(String rule){
		
		for (String key : YodaGrammar.keySet()) {
			List<String> list = YodaGrammar.get(key);
			for (int i = 0; i < list.size(); i++)
			{
				String[] words = rule.split(" ");
				if (list.get(i).equalsIgnoreCase(rule))
					return new Pair<String, Integer>(key, 1);
				else if(list.get(i).equalsIgnoreCase(words[1] + " " + words[0]))
					return new Pair<String, Integer>(key, 2);
			}
		}

		return new Pair<String, Integer>("", 0);
	}
	
	public Tree translate(List<Tree> trees){
		
		if(trees.get(0).getValue().equalsIgnoreCase("s") && trees.size() <= 1)
			return trees.get(0);
		
		List<Tree> partial = new ArrayList<Tree>();
		for(int i = trees.size()-1 ; i > 0; i--)
		{
			Pair<String, Integer> found = findInGrammar(trees.get(i-1).getValue() + " " + trees.get(i).getValue());
			if(found.getKey().equalsIgnoreCase("s") && !partial.isEmpty())
			{
				Collections.reverse(trees);
				found = findInGrammar(trees.get(i-1).getValue() + " " + trees.get(i).getValue());
			}
				
			switch (found.getValue()) {
				case 1:
					List<Tree> children1 = new ArrayList<Tree>();
					children1.add(trees.get(i-1));
					children1.add(trees.get(i)); 
					Tree tree1 = new Tree(found.getKey(),children1);
					partial.add(tree1);
					i--;
					break;
				case 2:
					List<Tree> children2 = new ArrayList<Tree>();
					children2.add(trees.get(i)); 
					children2.add(trees.get(i-1));
					Tree tree2 = new Tree(found.getKey(),children2);
					partial.add(tree2);
					i--;
					break;

			default:
				partial.add(trees.get(i));
				break;
			}
			
			if(i==1)
				partial.add(trees.get(0));
			
		}
		Collections.reverse(partial);
		return translate(partial);
	}

	public void print() {

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.print(table[i][j].getValue() + "    ");
			}
			System.out.println();
		}
		System.out.println(rules);
		System.out.println("\n");

	}

	public static void main(String[] args) {

		Translator translator = new Translator();
		translator.parserGrammar("assets\\GrammaticaYoda.txt");
		translator.parserGrammar("assets\\GrammaticaItaliana.txt");

		// Scanner scanner = new Scanner(System.in);

		// String sentence = scanner.nextLine();
		System.out.println(translator.algorithm("tu hai amici lì"));
		System.out.println(translator.algorithm("noi siamo illuminati"));
		System.out.println(translator.algorithm("tu avrai novecento anni di età"));
	}

}
