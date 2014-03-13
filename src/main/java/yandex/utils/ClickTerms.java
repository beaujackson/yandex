/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.utils;

import yandex.App;
import yandex.containers.UrlTerms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ClickTerms {

    private static String COMMA = ",";
    private static String TAB = "\t";

    public static HashMap<String, UrlTerms> map = new HashMap<>();

    static {
        File clickTerms = new File(App.clickTerms);

        if (clickTerms.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(clickTerms));

                String line;
                while ((line = reader.readLine()) != null) {

                    //file format:
                    //URLID \t URLIDCount \t Term1,UsageCount1,NumAnalytics1 \t TermN,UsageCountN,NumAnalyticsN

                    String[] values = line.split(TAB);
                    UrlTerms urlTerms = new UrlTerms();
                    urlTerms.total = Long.parseLong(values[1]);

                    for (int i=2; i<values.length; i++) {
                        String[] termAnalysis = values[i].split(COMMA);

                        urlTerms.terms.put(termAnalysis[0], Integer.parseInt(termAnalysis[1]));
                        urlTerms.analysis.high = Double.parseDouble(termAnalysis[2]);
                        urlTerms.analysis.low = Double.parseDouble(termAnalysis[3]);
                        urlTerms.analysis.count = Integer.parseInt(termAnalysis[4]);
                        urlTerms.analysis.mean = Double.parseDouble(termAnalysis[5]);
                        urlTerms.analysis.runningTotal = Double.parseDouble(termAnalysis[6]);
                        urlTerms.analysis.quartile = Double.parseDouble(termAnalysis[7]);
                    }

                    map.put(values[0], urlTerms);
                }

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
