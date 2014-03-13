/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 12/6/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.rankers;

import yandex.containers.QueryAction;
import yandex.containers.SearchResult;
import yandex.summarizers.SimpleCounter;
import yandex.summarizers.TermSummaries;
import yandex.utils.TermsFile;

/*
each search term in a query is associated with the clicks for that query.
more frequently used terms are more important.
write a file where each term is associated with the urlids and domain ids?
--
SearchTerm,TimesTermIsUsed \t URLID,TimesURLIDIsUsed,... \t DomainID,TimesDomainIDIsUsed,...

To rank,
for each term in query, result.d3Rank = TimesUrlIdIsUsed + TimesDomainIdIsUsed
 */
public class D3Ranker {

    public static void rank(QueryAction query) {

        for (String term : query.listOfTerms) {
            TermSummaries termSummaries = TermsFile.terms.get(term);

            if (termSummaries != null) {
                for (SearchResult result : query.results) {
                    SimpleCounter counter = termSummaries.urlCount.get(result.urlId);
                    if (counter != null) {
                        result.d3Rank += counter.total;
                    }

                    counter = termSummaries.domainCount.get(result.domainId);
                    if (counter != null) {
                        result.d3Rank += counter.total;
                    }
                }
            }
        }
    }
}
