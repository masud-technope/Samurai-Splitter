
/****
 * 
 * Samurai token splitter, developed Enslen et al, MSR 2009
 * 
 */

package samurai.splitter;

import java.util.HashMap;

public class SplitTokenScore {

	HashMap<String, Integer> wordMap;
	HashMap<String, Integer> reservedMap;
	int pSumFreq = 0;

	public SplitTokenScore(HashMap<String, Integer> reservedMap,
			HashMap<String, Integer> wordMap) {
		this.reservedMap = reservedMap;
		this.wordMap = wordMap;
		this.pSumFreq = getReservedSumFreq();
	}

	protected int getReservedSumFreq() {
		int sum = 0;
		for (String key : reservedMap.keySet()) {
			sum += reservedMap.get(key);
		}
		return sum;
	}

	protected double getScore(String key) {
		double score = 0;
		if (reservedMap.containsKey(key)) {
			score = reservedMap.get(key);
		}
		if (wordMap.containsKey(key)) {
			score += (double) wordMap.get(key) / Math.log(pSumFreq);
		}
		return score;
	}
}
