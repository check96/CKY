package cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Translator {

	private String[][] table;
	private Map<String, List<String>> grammar = new TreeMap<String, List<String>>();
	private Set<String> rules = new TreeSet<String>();
	
	public void parserGrammarWithSem() {
		try {
			Scanner scanner = new Scanner(new File("assets\\grammars.txt"));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace("\r", "");

				if (line.contains("end."))
					break;

				if (line.contains(" -> ")) {
					String[] split = line.split("] ->");
					String head = split[0].split("\\[")[0];

					if (!grammar.containsKey(head))
						grammar.put(head, new ArrayList<String>());

					String[] list = split[1].split(" ");
					String rule = "";

					for (String string : list) {
						String body = string.split("\\[")[0];
						if (!body.equals(""))
							rule += body + " ";
					}
					grammar.get(head).add(rule.substring(0, rule.length() - 1));
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

//		for (String key : grammar.keySet()) {
//			System.out.println(key);
//			for (String string : grammar.get(key))
//				System.out.println("  " + string);
//		}

	}
	public void parserGrammar() {
		try {
			Scanner scanner = new Scanner(new File("assets\\GrammaticaItaliana.txt"));
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
//						String body = string.split("\\[")[0];
						if (!body.equals(""))
							rule += body + " ";
					}
					grammar.get(head).add(rule.substring(0, rule.length() - 1));
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (String key : grammar.keySet()) {
			System.out.println(key);
			for (String string : grammar.get(key))
				System.out.println("  " + string);
		}

	}


	public boolean algorithm(String sentence) {

		String[] words = sentence.split(" ");

		table = new String[words.length][words.length];

		for (int i = 0; i < words.length; i++)
			for (int j = 0; j < words.length; j++)
				table[i][j] = "-";

		for (int i = 0; i < words.length; i++) {
			table[i][i] = getHead(words[i]);
			rules.add(table[i][i] + " -> " + words[i]);
			
			for (int j = i - 1; j >= 0; j--)
				for (int k = j; k <= i-1; k++)
				{
					String head = getHead(table[j][k] + " " + table[k+1][i]);
					if(table[j][i].equals("-") && !(head.equals("-")))
					{
						table[j][i] = head;
						
						rules.add(head + " -> " + table[j][k] + " " + table[k+1][i]); 
					}
					
//					System.out.println(i + " " + j + " " + k + " " + table[j][i] + " " + table[j][k] + " " + table[k+1][i]);
				}																					
		}
		
		if (table[0][words.length-1].equals("S"))
			return true;

		return false;
	}

	private String getHead(String rule) {
		for (String key : grammar.keySet()) {
			List<String> list = grammar.get(key);
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).equals(rule))
					return key;
		}
		return "-";
	}

	public void print() {

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++){
				System.out.print(table[i][j] + "    ");
			}
			System.out.println();
		}
		System.out.println(rules);
		System.out.println("\n");
	
	}

	public static void main(String[] args) {

		Translator translator = new Translator();
		translator.parserGrammar();

		// Scanner scanner = new Scanner(System.in);

		// String sentence = scanner.nextLine();
		System.out.println(translator.algorithm("Tu hai amici lì"));
			translator.print();
//		else
//			System.out.println("false");
	}

}
