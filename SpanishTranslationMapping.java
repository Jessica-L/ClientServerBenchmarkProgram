import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class SpanishTranslationMapping implements Mapping<String> {
	public String apply(String str) {
		String[] strArray = str.split(" ");
		String[] spanishStrArray = new String[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			String span = SpanishTranslationMapping.englishToSpanish().get(strArray[i].toLowerCase());
			if (span == null) span = strArray[i];
			spanishStrArray[i] = span;
		}
		String spanishStr = "";
		for (String s : spanishStrArray) {
			if (spanishStr.length() > 0) spanishStr += " " + s;
			else spanishStr += s;
		}
		return spanishStr;
	}

	private static HashMap<String,String> englishToSpanishMap = null;

	private static synchronized HashMap<String,String> englishToSpanish() {
		
		if (SpanishTranslationMapping.englishToSpanishMap == null) {
			SpanishTranslationMapping.englishToSpanishMap = new HashMap<String,String>();
			try {
				BufferedReader reader = new BufferedReader(new FileReader("EnglishToSpanish.trans"));
				String line = reader.readLine();
				while (line != null) {

					String[] wordMapping = line.split(":");
					SpanishTranslationMapping.englishToSpanishMap.put(wordMapping[0].toLowerCase(),wordMapping[1].toLowerCase());

					line = reader.readLine();
				}
			} catch (Exception e) {
				System.out.println("Cannot create dictionary.");
			}
			

		}
		return SpanishTranslationMapping.englishToSpanishMap;
	}
}