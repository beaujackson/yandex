/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.containers;

public class SearchResult {

    public String urlId;
    public String domainId;
    public Double d1Rank = 0.0;
    public Double d2Rank = 0.0;
    public Double d3Rank = 0.0;

    public SearchResult(String[] data) {
        urlId = data[0];
        domainId = data[1];
    }

    public void print() {
        System.out.println("      URLID: " + urlId + "; DomainID: " + domainId);
    }
}
