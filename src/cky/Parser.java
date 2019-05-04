package cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Parser {
	
	public static Map<String, String> parserYodaGrammar() {
		Map<String, String> grammar = new TreeMap<String, String>();
		try {
			Scanner scanner = new Scanner(new File("assets\\GrammaticaYoda.txt"));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace("\r", "");

				if (line.contains(" -> ")) {
					String[] split = line.split(" -> ");
					grammar.put(split[1], split[0]);
				}
			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

//		for (String key : grammar.keySet())
//			 System.out.println(key + ":  " + grammar.get(key));
		
		return grammar;
	}
	
	public static Map<String, List<Tree>> parserItalianGrammar() {
		Map<String, List<Tree>> grammar = new TreeMap<String, List<Tree>>();
		try {
			Scanner scanner = new Scanner(new File("assets\\GrammaticaItaliana.txt"));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace("\r", "");

				if (line.contains(" -> ")) {
					String[] split = line.split(" -> ");
					if(!grammar.containsKey(split[1]))
						grammar.put(split[1], new ArrayList<Tree>());
					
					Tree tree = new Tree(split[0]);
					grammar.get(split[1]).add(tree);
				}
			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

//		 for (String key : grammar.keySet())
//			 System.out.println(key + ":  " + grammar.get(key));
		
		return grammar;
	}
	
}
