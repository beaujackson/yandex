/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/22/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.rankers;

import yandex.containers.QueryAction;
import yandex.containers.SearchResult;
import yandex.containers.UrlTerms;
import yandex.utils.ClickTerms;

public class D2Ranker {

    public static void rank(QueryAction query) {

        for (SearchResult searchResult : query.results) {

            UrlTerms urlTerms = ClickTerms.map.get(searchResult.urlId);
            if (urlTerms != null) {
                for (String term : query.listOfTerms) {
                    if (urlTerms.terms.containsKey(term)) {

                        Integer count = urlTerms.terms.get(term);

                        //Double rank = count * urlTerms.analysis.mean;

                        searchResult.d2Rank += count;
                    }
                }
            }
        }
    }

}
