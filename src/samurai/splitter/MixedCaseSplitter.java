
/****
 * 
 * Samurai token splitter, developed Enslen et al, MSR 2009
 * 
 */

package samurai.splitter;

import java.util.ArrayList;
import java.util.HashMap;

public class MixedCaseSplitter {

	ArrayList<String> ccTokens;
	SplitTokenScore scoreCalc;
	SameCaseSplitter sameCaseSplitter;

	public MixedCaseSplitter(ArrayList<String> ccTokens,
			SplitTokenScore scoreCalc) {
		this.ccTokens = ccTokens;
		this.scoreCalc = scoreCalc;
		this.sameCaseSplitter = new SameCaseSplitter(scoreCalc);
	}

	protected String[] decomposeCamelCase(String ccToken) {
		String cam1Regex = "([a-z])([A-Z])+";
		String replacement1 = "$1\t$2";
		return ccToken.replaceAll(cam1Regex, replacement1).split("\\s+");
	}

	protected String splitToken(String ccToken) {
		String[] smallTokens = decomposeCamelCase(ccToken);

		String splitall = new String();

		for (String smallToken : smallTokens) {
			int upperIndex = -1;

			double camelScore = 0;
			double altScore = 0;
			String splitted = new String();

			for (int i = 0; i < smallToken.length() - 1; i++) {
				if (Character.isUpperCase(smallToken.charAt(i))
						&& Character.isLowerCase(smallToken.charAt(i + 1))) {
					upperIndex = i;
					break;
				}
			}
			if (upperIndex >= 0) {
				int n = smallToken.length() - 1;

				if (upperIndex > 0) {
					camelScore = scoreCalc.getScore(smallToken.substring(
							upperIndex, n + 1));
				} else {
					camelScore = scoreCalc.getScore(smallToken.substring(0,
							n + 1));
				}

				altScore = scoreCalc.getScore(smallToken.substring(
						upperIndex + 1, n));

				// select split based on score
				if (camelScore > Math.sqrt(altScore)) {
					if (upperIndex >= 0) {
						splitted = smallToken.substring(0, upperIndex) + " "
								+ smallToken.substring(upperIndex, n + 1);
					}
				} else {
					splitted = smallToken.substring(0, upperIndex + 1) + " "
							+ smallToken.substring(upperIndex + 1, n + 1);
				}
			} else {
				splitted = smallToken;
			}
			splitall += " " + splitted;
		}

		String finalSplitted = new String();
		String[] words = splitall.trim().split("\\s+");
		for (String word : words) {
			String splitted = this.sameCaseSplitter.splitToken(word,
					scoreCalc.getScore(word));
			if (splitted.length() > word.length()) {
				finalSplitted += splitted + " ";
			} else {
				finalSplitted += word + " ";
			}
		}

		return finalSplitted;
	}

	public HashMap<String, String> getSplittedTokens() {
		HashMap<String, String> splitMap = new HashMap<>();
		for (String ccToken : this.ccTokens) {
			String splitted = splitToken(ccToken);
			splitMap.put(ccToken, splitted);
		}
		return splitMap;
	}
}
