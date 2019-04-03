package cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Translator {

	private Tree[][] table;
	private Map<String, String> ItalianGrammar = new TreeMap<String, String>();
	private Map<String, String> YodaGrammar = new TreeMap<String, String>();

	public void parserGrammar(String path) {
		try {
			Map<String, String> grammar = new TreeMap<String, String>();
			Scanner scanner = new Scanner(new File(path));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace("\r", "");

				if (line.contains(" -> ")) {
					String[] split = line.split(" -> ");
					grammar.put(split[1], split[0]);
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
		// System.out.println(key);
		// for (String string : grammar.get(key))
		// System.out.println("  " + string);
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
			table[i][i] = new Tree(ItalianGrammar.get(words[i]),word);
			
			for (int j = i - 1; j >= 0; j--)
				for (int k = j + 1; k <= i; k++) {
					String head = ItalianGrammar.get(table[j][k - 1].getValue()	+ " " + table[k][i].getValue());
					if (table[j][i].getValue().equals("-") && head != null) {
						List<Tree> children = new ArrayList<Tree>();
						children.add(table[j][k - 1]);
						children.add(table[k][i]);
						table[j][i] = new Tree(head, children);
					}
				}
		}
		if (table[0][words.length - 1].getValue().equalsIgnoreCase("S")) {
			List<Tree> leaves = new ArrayList<Tree>();

			for (int i = 0; i < table.length; i++)
				leaves.add(table[i][i]);
			Tree tree = translate(leaves);
			return tree.findLeaves();
		}
		return "";
	}

	public Tree translate(List<Tree> trees) {

		if (trees.get(0).getValue().equalsIgnoreCase("s"))
			return trees.get(0);

		List<Tree> partial = new ArrayList<Tree>();
		for (int i = trees.size() - 1; i > 0; i--) {
			try {
				String head = YodaGrammar.get(trees.get(i-1).getValue() + " "	+ trees.get(i).getValue());
				if (head.equalsIgnoreCase("s") && !partial.isEmpty()) {
					partial.add(trees.get(i - 1));
					partial.add(trees.get(i));
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
					String head = YodaGrammar.get(trees.get(i).getValue() + " "	+ trees.get(i-1).getValue());
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
				}
				catch(Exception e1){
					partial.add(trees.get(i));
				}
			}
			if (i == 1)
				partial.add(trees.get(0));

		}
		Collections.reverse(partial);
		return translate(partial);
	}

	public void printTable() {

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.print(table[i][j].getValue() + "    ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {

		Translator translator = new Translator();
		translator.parserGrammar("assets\\GrammaticaYoda.txt");
		translator.parserGrammar("assets\\GrammaticaItaliana.txt");

		// Scanner scanner = new Scanner(System.in);

		// String sentence = scanner.nextLine();
		
//		System.out.println(translator.algorithm("la mia età è illuminata"));
		System.out.println(translator.algorithm("tu hai amici lì"));
		System.out.println(translator.algorithm("noi siamo illuminati"));
		System.out.println(translator.algorithm("tu avrai novecento anni di età"));
	}

}
