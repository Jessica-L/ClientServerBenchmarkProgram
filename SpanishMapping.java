/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class SpanishMapping implements Mapping<String> {
	public String apply(String str) {
		System.out.println(SpanishMapping.englishToSpanish().get(str.toLowerCase()));
		return SpanishMapping.englishToSpanish().get(str.toLowerCase());
	}

	private static HashMap<String,String> englishToSpanishMap = null;

	private static synchronized HashMap<String,String> englishToSpanish() {
		
		if (SpanishMapping.englishToSpanishMap == null) {
			SpanishMapping.englishToSpanishMap = new HashMap<String,String>();
			try {
				BufferedReader reader = new BufferedReader(new FileReader("EnglishToSpanish.trans"));
				String line = reader.readLine();
				while (line != null) {

					String[] wordMapping = line.split(":");
					SpanishMapping.englishToSpanishMap.put(wordMapping[0].toLowerCase(),wordMapping[1].toLowerCase());

					line = reader.readLine();
				}
			} catch (Exception e) {
				System.out.println("Cannot create dictionary.");
			}
			

		}
		return SpanishMapping.englishToSpanishMap;
	}
}