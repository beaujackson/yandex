/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 12/6/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.utils;

import yandex.App;
import yandex.summarizers.SimpleCounter;
import yandex.summarizers.TermSummaries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class TermsFile {

    private static String COMMA = ",";
    private static String TAB = "\t";

    public static HashMap<String, TermSummaries> terms = new HashMap<>();

    static {
        File termsFile = new File(App.termsFile);

        if (termsFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(termsFile));

                String line;
                while ((line = reader.readLine()) != null) {

                    //file format:
                    //SearchTerm,TimesTermIsUsed \t URLID,TimesURLIDIsUsed,... \t DomainID,TimesDomainIDIsUsed,...

                    String[] values = line.split(TAB);

                    String[] data = values[0].split(COMMA);
                    TermSummaries termSummaries = new TermSummaries();
                    termSummaries.term = data[0];
                    termSummaries.total = Long.parseLong(data[1]);

                    String[] urls = values[1].split(COMMA);
                    for (int i=0; i<urls.length; i++) {
                        termSummaries.urlCount.put(urls[i], new SimpleCounter(Integer.parseInt(urls[++i])));
                    }

                    String[] domains = values[2].split(COMMA);
                    for (int i=0; i<domains.length; i++) {
                        termSummaries.domainCount.put(domains[i], new SimpleCounter(Integer.parseInt(domains[++i])));
                    }

                    terms.put(termSummaries.term, termSummaries);
                }

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
