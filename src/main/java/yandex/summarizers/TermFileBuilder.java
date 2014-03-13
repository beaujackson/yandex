/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 12/6/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.summarizers;

import yandex.App;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class TermFileBuilder {
    private static String COMMA = ",";
    private static String TAB = "\t";

    public static TermFileBuilder instance = new TermFileBuilder();

    private HashMap<String, TermSummaries> terms = new HashMap<>();

    private TermFileBuilder() {
    }

    public TermSummaries getTermSummaries(String term) {
        TermSummaries termSummaries = terms.get(term);

        if (termSummaries == null) {
            termSummaries = new TermSummaries();
            termSummaries.term = term;
            terms.put(term, termSummaries);
        }

        return termSummaries;
    }

    public void buildTermFile() {
        File termsFile = new File(App.termsFile);

        if (termsFile.exists()) {
            termsFile.delete();
        }

        try {
            termsFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(termsFile, true));

            //SearchTerm,TimesTermIsUsed \t URLID,TimesURLIDIsUsed,... \t DomainID,TimesDomainIDIsUsed,...
            for (String key : terms.keySet()) {
                TermSummaries termSummaries = terms.get(key);

                if (termSummaries.urlCount.isEmpty() || termSummaries.domainCount.isEmpty()) {
                    continue;
                }

                writer.write(key);
                writer.write(COMMA + termSummaries.total);
                writer.write(TAB);

                boolean first = true;
                for (String urlId : termSummaries.urlCount.keySet()) {

                    if (!first) {
                        writer.write(COMMA);
                    }

                    first = false;
                    writer.write(urlId + "," + termSummaries.urlCount.get(urlId).total);
                }

                writer.write(TAB);

                first = true;
                for (String domainId : termSummaries.domainCount.keySet()) {

                    if (!first) {
                        writer.write(COMMA);
                    }

                    first = false;
                    writer.write(domainId + "," + termSummaries.domainCount.get(domainId).total);
                }

                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
