package cky;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Parser {
	
	public static Map<String, String> parserGrammar(String path) {
		Map<String, String> grammar = new TreeMap<String, String>();
		try {
			Scanner scanner = new Scanner(new File(path));
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
	
}
