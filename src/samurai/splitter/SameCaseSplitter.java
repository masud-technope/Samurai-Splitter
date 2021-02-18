
/****
 * 
 * Samurai token splitter, developed Enslen et al, MSR 2009
 * 
 */

package samurai.splitter;

import java.util.ArrayList;
import config.StaticData;
import utility.ContentLoader;

public class SameCaseSplitter {

	static ArrayList<String> prefixes = new ArrayList<>();
	static ArrayList<String> suffixes = new ArrayList<>();
	SplitTokenScore scoreCalc;

	public SameCaseSplitter(SplitTokenScore scoreCalc) {
		this.loadPrefixSuffixData();
		this.scoreCalc = scoreCalc;
	}

	protected void loadPrefixSuffixData() {
		// loading the prefix and suffix
		if (prefixes.isEmpty()) {
			prefixes = ContentLoader.getAllLinesOptList(StaticData.SAMURAI_DIR
					+ "/prefixes.txt");
		}
		if (suffixes.isEmpty()) {
			suffixes = ContentLoader.getAllLinesOptList(StaticData.SAMURAI_DIR
					+ "/suffixes.txt");
		}
	}

	protected String splitToken(String token, double score) {
		// split the same case token
		String splitted = token;
		int n = token.length();
		double maxScore = -1;
		int i = 0;
		while (i < n - 1) {
			String left = token.substring(0, i + 1);
			String right = token.substring(i + 1, n);
			double scoreL = scoreCalc.getScore(left);
			double scoreR = scoreCalc.getScore(right);
			boolean prefix = prefixes.contains(left.toLowerCase())
					|| suffixes.contains(right.toLowerCase());
			boolean toSplitL = Math.sqrt(scoreL) > Math.max(
					scoreCalc.getScore(token), score);
			boolean toSplitR = Math.sqrt(scoreR) > Math.max(
					scoreCalc.getScore(token), score);
			if (!prefix && toSplitL && toSplitR) {
				if (scoreL + scoreR > maxScore) {
					maxScore = scoreL + scoreR;
					splitted = left + " " + right;
				}
			} else if (!prefix && toSplitL) {
				String temp = splitToken(right, score);
				if (temp.length() > right.length()) {
					splitted = left + " " + temp;
				} else {
					splitted = left + " " + right;
				}
			}
			i++;
		}
		return splitted;
	}
}
