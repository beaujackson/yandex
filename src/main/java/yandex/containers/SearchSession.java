/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.containers;

import yandex.App;
import yandex.profilers.UserProfile;
import yandex.summarizers.CountSummarizer;
import yandex.utils.CompareDouble;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/*
SearchSession is defined by the meta data record

Session metadata (TypeOfRecord = M):

Record format:
SessionID TypeOfRecord Day USERID

SessionID is the unique identifier of a search session.

TypeOfRecord is the type of the log record.
It’s either a query (Q, T), a click (C), or the session metadata (M).
T letter is used only for test queries.

Day is the number of the day in the data (the entire log spans over 30 days).

UserID is the unique identifier of a user.

Example:
744899 M 23 123123123

The above record describes the session (SessionID = 744899) of the user with USERID 123123123,
performed on the 23rd day of the dataset.


A search session has one or more queries identified by serpid.
Each query has a list of terms.
Each query has a list of results made up of urlid and domainid.
Each query has zero or more clicks tied to the query by serpid.
Each click has a urlid indicating the result it is tied to.


Sort urlids by relevance for each query.

hypothesis: domainid may impact relevance.
experiment: how does frequency of search term impact rank?
 */
public class SearchSession {

    public String sessionId;
    public String typeOfRecord;
    public Integer day;
    public String userId;

    public HashMap<String, QueryAction> queries = new HashMap<>();
    public HashMap<String, Integer> searchTermFrequency = new HashMap<>();

    public void parseMetadata(String[] data) {
        sessionId = data[0];
        typeOfRecord = data[1];
        day = Integer.parseInt(data[2]);
        userId = data[3];

        CountSummarizer.getInstance("User-" + userId).total++;
        UserProfile.getProfile(userId).sessionCount++;
        UserProfile.getProfile(userId).incrementDaySearch(day);
    }

    public void addQuery(String[] data) {
        UserProfile.getProfile(userId).searchCount++;
        QueryAction query = new QueryAction(userId, data);
        queries.put(query.serpId, query);

        for (String term : query.listOfTerms) {
            Integer val = searchTermFrequency.get(term);

            if (val == null) {
                searchTermFrequency.put(term, 1);
            } else {
                searchTermFrequency.put(term, val + 1);
            }
        }
    }

    public void addClick(String[] data) {
        ClickAction click = new ClickAction(data);

        QueryAction queryAction = queries.get(click.serpId);

        Double dwellTime = click.timePassed - queryAction.timePassed;
        if (dwellTime < 50) {
            click.relevance = 0.0;
        } else if (dwellTime >= 50 && dwellTime < 400) {
            click.relevance = 1.0;
        } else if (dwellTime >= 400) {
            click.relevance = 2.0;
        }

        queryAction.clicks.add(click);
    }

    public void train() {

        for (QueryAction query : queries.values()) {

            if (!query.clicks.isEmpty()) {
                query.clicks.get(query.clicks.size() - 1).relevance = 2.0;
            }
            query.train();
            query.rank();
            query.score(query.clicks);
        }
    }

    public void test() {

        File results = new File(App.resultFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(results, true));

            for (QueryAction query : queries.values()) {

                if (query.typeOfRecord.equals("T")) {

                    query.rank();

                    Collections.sort(query.results, new Comparator<SearchResult>() {
                        @Override
                        public int compare(SearchResult o1, SearchResult o2) {
                            return CompareDouble.reverseCompare(o1.d1Rank, o2.d1Rank);
                        }
                    });

                    for (SearchResult searchResult : query.results) {
                        writer.write(sessionId + "," + searchResult.urlId);
                        writer.newLine();
                    }
                }
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void print() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.print("Session: " + sessionId);
        System.out.print("; Day: " + day);
        System.out.print("; User: " + userId);
        System.out.println();

        for (QueryAction query : queries.values()) {
            query.print();

            if (!query.clicks.isEmpty()) {
                System.out.println("    Clicks:");
                for (ClickAction click : query.clicks) {
                    click.print();
                }
            }
        }
    }
}
