/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/18/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.summarizers;

import yandex.App;
import yandex.utils.Pad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ClickSummarizer {
    private static Integer LEN = 15;
    private static String COMMA = ",";
    private static String TAB = "\t";
    private static HashMap<String, ClickSummarizer> summarizers = new HashMap<>();

    public long total = 0;
    private HashMap<String, CountSummarizer> termCount = new HashMap<>();

    public ClickSummarizer() {
    }

    public static ClickSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new ClickSummarizer());
        }

        return summarizers.get(key);
    }

    public static void printSummary(long threshold) {

        System.out.println(Pad.right("|Key", LEN) + Pad.right("|Count", LEN) + "|");
        System.out.println("-------------------------------");

        for (String key : summarizers.keySet()) {
            ClickSummarizer summarizer = summarizers.get(key);
            if (summarizer.total > threshold) {
                getInstance(key).printSummary(key);
            }
        }
    }

    /*
    clickTerms file format:
    URLID \t URLIDCount \t Term1,UsageCount1,NumAnalytics1 \t TermN,UsageCountN,NumAnalyticsN
     */
    public static void buildClickTermsFile() {

        File clickTerms = new File(App.clickTerms);

        if (clickTerms.exists()) {
            clickTerms.delete();
        }

        try {
            clickTerms.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(clickTerms, true));

            for (String key : summarizers.keySet()) {
                ClickSummarizer summarizer = summarizers.get(key); //key is urlid

                writer.write(key);
                writer.write(TAB + summarizer.total); //total is total number of times this urlid was part of a clickaction

                //term is a search term this urlid had associated with it
                for (String term : summarizer.termCount.keySet()) {

                    //countSummarizer is summary information about how often this term was associated with a click action
                    CountSummarizer countSummarizer = summarizer.termCount.get(term);
                    writer.write(TAB + term);
                    writer.write(COMMA + countSummarizer.total);
                    writer.write(COMMA + countSummarizer.analysis.getCSV());
                }

                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incrementTermCount(String term, Double score) {

        CountSummarizer countSummarizer = null;

        if (!termCount.containsKey(term)) {
            countSummarizer = new CountSummarizer();
            termCount.put(term, countSummarizer);
        }

        countSummarizer = termCount.get(term);
        countSummarizer.total++;
        countSummarizer.analysis.setVal(score);
    }

    private void printSummary(String key) {
        System.out.println(Pad.right("|" + key, LEN) + Pad.right("|" + total, LEN) + "|");

        for (String termKey : termCount.keySet()) {
            CountSummarizer countSummarizer = termCount.get(termKey);
            System.out.println(Pad.right("|" + termKey, LEN) + Pad.right("|" + countSummarizer.total, LEN) + "|");
        }

        System.out.println("-------------------------------");
    }
}
