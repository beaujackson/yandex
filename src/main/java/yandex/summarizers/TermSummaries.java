/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 12/6/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.summarizers;

import java.util.HashMap;

public class TermSummaries {

    public String term;
    public Long total = 0L;

    //key is urlid, value is count of occurrences with this term
    public HashMap<String, SimpleCounter> urlCount = new HashMap<>();

    //key is domainid, value is count of occurrences with this term
    public HashMap<String, SimpleCounter> domainCount = new HashMap<>();

    public void incrementUrlCount(String urlId) {
        SimpleCounter counter = urlCount.get(urlId);

        if (counter == null) {
            counter = new SimpleCounter();
            urlCount.put(urlId, counter);
        }

        counter.total++;
    }

    public void incrementDomainCount(String domainId) {
        SimpleCounter counter = domainCount.get(domainId);

        if (counter == null) {
            counter = new SimpleCounter();
            domainCount.put(domainId, counter);
        }

        counter.total++;
    }

}
