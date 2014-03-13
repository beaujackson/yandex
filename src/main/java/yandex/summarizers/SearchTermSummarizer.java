/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/18/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.summarizers;

import yandex.utils.Pad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SearchTermSummarizer {
    private static Integer LEN = 15;
    private static HashMap<String, SearchTermSummarizer> summarizers = new HashMap<>();

    public long total = 0;
    private HashMap<String, CountSummarizer> urlCount = new HashMap<>();


    public static SearchTermSummarizer getInstance(String key) {
        if (!summarizers.containsKey(key)) {
            summarizers.put(key, new SearchTermSummarizer());
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

    public SearchTermSummarizer() {
    }

    public void incrementUrlCount(String urlid) {
        String key = "URL-" + urlid;
        CountSummarizer countSummarizer = CountSummarizer.getInstance(key);

        if (!urlCount.containsKey(key)) {
            urlCount.put(key, countSummarizer);
        }

        urlCount.get(key).total++;
    }

    private void printSummary(String key) {
        System.out.println(Pad.right("|" + key, LEN) + Pad.right("|" + total, LEN) + "|");

        for (String countKey : urlCount.keySet()) {
            CountSummarizer countSummarizer = urlCount.get(countKey);
            System.out.println(Pad.right("|" + countKey, LEN) + Pad.right("|" + countSummarizer.total, LEN) + "|");
        }

        System.out.println("-------------------------------");
    }
}
