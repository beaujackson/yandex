/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.containers;

import yandex.analysis.ScoreCard;
import yandex.profilers.UserProfile;
import yandex.rankers.D1Ranker;
import yandex.rankers.D2Ranker;
import yandex.rankers.D3Ranker;
import yandex.summarizers.SearchTermSummarizer;
import yandex.summarizers.TermFileBuilder;
import yandex.summarizers.TermSummaries;
import yandex.utils.CompareDouble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/*
Query action (TypeOfRecord = Q or T):

Record format:
SessionID TimePassed TypeOfRecord SERPID QueryID ListOfTerms ListOfURLsAndDomains

SessionID is the unique identifier of a search session.

TimePassed is the time passed since the start of the session with the SessionID in units of time.
We do not disclose how many milliseconds are in one unit of time.

TypeOfRecord is the type of the log record.
It’s either a query (Q, T), a click (C), or the session metadata (M).
T letter is used only for test queries.

SERPID is the unique identifier of a (S)earch (E)ngine (R)esult (P)age at the session level.

QueryID is the unique identifier of a query.

ListOfTerms is a comma-separated list of terms of the query, represented by their TermIDs.

   - TermId is the unique identifier of a query term.

ListOfURLsAndDomains is the list of comma-separated pairs of URLID and the corresponding DomainId.
(e.g. en.wikipedia.org is the domain of http://en.wikipedia.org/wiki/Web_search,
or scifun.chem.wisc.edu is the domain of http://scifun.chem.wisc.edu/HomeExpts/HOMEEXPTS.HTML).
It is tab-separated and ordered from left to right as they were shown to the user from the top to the bottom.

   - URLID is the unique identifier of an URL.

Example:
744899 0 Q 0 192902 4857, 3847, 2939 632428,2384 309585,28374 319567,38724 6547,28744 20264,2332 3094446,34535 90,21 841,231 8344,2342 119571,45767

The above record indicates the user for session 744899 submitted the query with QUERYID 192902.
0 time passed, and the serpid is 0.
The query contained terms with TermIDs 4857,3847,2939.
The URL with URLID 632428 placed on the domain DomainID 2384 is the top result on the corresponding SERP (serpid 0).

A search session has one or more queries identified by serpid.
Each query has a list of terms.
Each query has a list of results made up of urlid and domainid.
Each query has zero or more clicks tied to the query by serpid.
Each click has a urlid indicating the result it is tied to.

*/
public class QueryAction {

    private static String COMMA = ",";

    public String sessionId;
    public Integer timePassed;
    public String typeOfRecord;
    public String serpId;
    public String queryId;
    public String[] listOfTerms;
    public List<SearchResult> results = new ArrayList<>();
    public List<ClickAction> clicks = new ArrayList<>();
    public HashMap<String, SearchResult> resultMap = new HashMap<>();

    public QueryAction(String userId, String[] data) {
        sessionId = data[0];
        timePassed = Integer.parseInt(data[1]);
        typeOfRecord = data[2];
        serpId = data[3];
        queryId = data[4];
        listOfTerms = data[5].split(COMMA);

        for (String term : listOfTerms) {
            UserProfile.getProfile(userId).incrementTermUse(term);
        }

        for (int i=6; i<data.length; i++) {
            SearchResult result = new SearchResult(data[i].split(COMMA));
            results.add(result);
            resultMap.put(result.urlId, result);
        }
    }

    public void train() {

        for (String term : listOfTerms) {
            SearchTermSummarizer summarizer = SearchTermSummarizer.getInstance(term);
            summarizer.total++;

            TermSummaries termSummaries = TermFileBuilder.instance.getTermSummaries(term);
            termSummaries.total++;

            for (ClickAction click : clicks) {
                summarizer.incrementUrlCount(click.urlId);
                termSummaries.incrementUrlCount(click.urlId);

                SearchResult result = resultMap.get(click.urlId);
                termSummaries.incrementDomainCount(result.domainId);
            }
        }
    }

    public void rank() {
        D1Ranker.rank(this);
        D2Ranker.rank(this);
        D3Ranker.rank(this);
    }

    public void score(List<ClickAction> clicks) {

        if (clicks != null) {
            Collections.sort(clicks, new Comparator<ClickAction>() {
                @Override
                public int compare(ClickAction o1, ClickAction o2) {
                    return CompareDouble.reverseCompare(o1.relevance, o2.relevance);
                }
            });
        }
        setScore("Raw", clicks);

        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return CompareDouble.reverseCompare(o1.d1Rank, o2.d1Rank);
            }
        });
        setScore("D1", clicks);

        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return CompareDouble.reverseCompare(o1.d2Rank, o2.d2Rank);
            }
        });
        setScore("D2", clicks);

        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return CompareDouble.reverseCompare(o1.d3Rank, o2.d3Rank);
            }
        });
        setScore("D3", clicks);

    }

    private void setScore(String key, List<ClickAction> clicks) {
        double score = 0;

        for (SearchResult searchResult : results) {

            boolean scoreFound = false;

            for (ClickAction click : clicks) {
                if (searchResult.urlId.equals(click.urlId)) {
                    score += click.relevance;
                    scoreFound = true;
                }
            }

            if (!scoreFound) {
                break;
            }
        }

        ScoreCard.getInstance(key).setScore(score);
    }

    public void print() {
        System.out.print("Query: " + serpId);
        System.out.print("; ID: " + queryId);
        System.out.print("; Type: " + typeOfRecord);
        System.out.print("; Time: " + timePassed);
        System.out.println();

        System.out.println("    List Of Terms:");
        for (String term : listOfTerms) {
            System.out.println("      " + term);
        }

        System.out.println("    URLs / Domains");
        for (SearchResult searchResult : results) {
            searchResult.print();
        }
    }
}
