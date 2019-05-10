package cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Parser {
	
	public static Map<String, List<String>> parserGrammar(String path) {
		Map<String, List<String>> grammar = new TreeMap<String, List<String>>();
		try {
			Scanner scanner = new Scanner(new File(path));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace("\r", "");

				if (line.contains(" -> ")) {
					String[] split = line.toLowerCase().split(" -> ");
					if(!grammar.containsKey(split[1]))
						grammar.put(split[1], new ArrayList<String>());

					grammar.get(split[1]).add(split[0]);
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
	
}
