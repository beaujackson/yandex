/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.analysis;

import java.util.HashMap;

public class ScoreCard {

    private static HashMap<String, ScoreCard> scoreCards = new HashMap<>();

    public static ScoreCard getInstance(String key) {
        if (!scoreCards.containsKey(key)) {
            scoreCards.put(key, new ScoreCard());
        }

        return scoreCards.get(key);
    }

    private int count = 0;
    private double runningTotal = 0;
    private double mean;

    private ScoreCard() {
    }

    public void setScore(Double score) {
        count++;

        if (score == null) {
            return;
        }

        runningTotal += score;
        mean = runningTotal / count;
    }

    public static void printScore() {

        for (String key : scoreCards.keySet()) {
            ScoreCard scoreCard = scoreCards.get(key);
            System.out.println(key + " Total analyzed: " + scoreCard.count);
            System.out.println(key + " Avg score: " + scoreCard.mean);
            System.out.println();
        }
    }
}
