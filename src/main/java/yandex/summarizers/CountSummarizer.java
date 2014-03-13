/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.summarizers;

import yandex.analysis.NumericAnalysis;
import yandex.utils.Pad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CountSummarizer {

    private static Integer LEN = 15;
    private static HashMap<String, CountSummarizer> summarizers = new HashMap<>();

    public long total = 0;
    public NumericAnalysis analysis = new NumericAnalysis();

    public static CountSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new CountSummarizer());
        }

        return summarizers.get(key);
    }

    public static void printSummary() {

        System.out.println(Pad.right("|Key", LEN) + Pad.right("|Count", LEN) + "|");

        List<String> keys = new ArrayList<>(summarizers.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (String key : keys) {
            getInstance(key).printSummary(key);
        }
    }

    public static void printSummary(long threshold) {

        System.out.println(Pad.right("|Key", LEN) + Pad.right("|Count", LEN) + "|");

        List<String> keys = new ArrayList<>(summarizers.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (String key : keys) {
            if (getInstance(key).total >= threshold) {
                getInstance(key).printSummary(key);
            }
        }
    }

    public CountSummarizer() {
    }

    private void printSummary(String key) {
        System.out.println(Pad.right("|" + key, LEN) + Pad.right("|" + total, LEN) + "|");
    }
}
