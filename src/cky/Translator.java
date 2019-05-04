package cky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Translator {

	private List<List<List<Tree>>> table;
	private Map<String, String> italianGrammar = new TreeMap<String, String>();
	private Map<String, String> yodaGrammar = new TreeMap<String, String>();

	public Translator() {
		yodaGrammar.putAll(Parser.parserGrammar("assets\\GrammaticaYoda.txt"));
		italianGrammar.putAll(Parser.parserGrammar("assets\\GrammaticaItaliana.txt"));
	}

	public String algorithm(String sentence) {
		table = new ArrayList<List<List<Tree>>>();
		String[] words = sentence.split(" ");
		applyCKY(words);

		for (int n = 0; n < table.get(0).get(words.length - 1).size(); n++)
			if (table.get(0).get(words.length - 1).get(n).getValue().equalsIgnoreCase("S")) {
				List<Tree> leaves = new ArrayList<Tree>();

				for (int i = 0; i < table.size(); i++)
					leaves.add(table.get(i).get(i).get(n));

				Tree tree = translate(leaves);

				return tree.findLeaves();
			}
		return "";
	}

	public Tree translate(List<Tree> trees) {

//		System.out.println(trees);
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
	}

	public void applyCKY(String[] words) {

		for (int i = 0; i < words.length; i++) {
			table.add(new ArrayList<List<Tree>>());
			for (int j = 0; j < words.length; j++)
				table.get(i).add(new ArrayList<Tree>());
		}

		// i = righe, j = colonne
		for (int i = 0; i < words.length; i++) {

			List<Tree> word = new ArrayList<Tree>();
			word.add(new Tree(words[i]));

			table.get(i).get(i).add(new Tree(italianGrammar.get(words[i]), word));

			for (int j = i - 1; j >= 0; j--)
				for (int k = j + 1; k <= i; k++) {

					for (int n = 0; n < table.get(j).get(k - 1).size(); n++)
						for (int m = 0; m < table.get(k).get(i).size(); m++) {
							String head = italianGrammar.get(table.get(j).get(k - 1).get(n).getValue() + " "
									+ table.get(k).get(i).get(m).getValue());

							if (head != null) {
								List<Tree> children = new ArrayList<Tree>();
								children.add(table.get(j).get(k - 1).get(n));
								children.add(table.get(k).get(i).get(m));
								table.get(j).get(i).add(new Tree(head, children));
							}
						}
				}
		}

	}

	public void printTable() {

		for (int i = 0; i < table.size(); i++) {
			for (int j = 0; j < table.get(i).size(); j++) {
				System.out.print(table.get(i).get(j).get(0).getValue() + "    ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {

		Translator translator = new Translator();

		System.out.println(translator.algorithm("tu hai amici lì"));
		System.out.println(translator.algorithm("noi siamo illuminati"));
		System.out.println(translator.algorithm("tu avrai novecento anni di età"));
	}

}
