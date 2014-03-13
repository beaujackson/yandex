/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.containers;

/*
Click action (TypeOfRecord = C)

Record format:
SessionID TimePassed TypeOfRecord SERPID URLID

SessionID is the unique identifier of a search session.

TimePassed is the time passed since the start of the session with the SessionID in units of time.
We do not disclose how many milliseconds are in one unit of time.

TypeOfRecord is the type of the log record.
It’s either a query (Q, T), a click (C), or the session metadata (M).
T letter is used only for test queries.

SERPID is the unique identifier of a (S)earch (E)ngine (R)esult (P)age at the session level.

URLID is the unique identifier of an URL.

Example:
744899 1403 C 0 632428

1403 units of time after beginning of the session the user clicked on the result with URLID 632428
*/
public class ClickAction {

    public String sessionId;
    public Double timePassed;
    public String typeOfRecord;
    public String serpId;
    public String urlId;

    public Double relevance = 0.0;

    public ClickAction(String[] data) {
        sessionId = data[0];
        timePassed = Double.parseDouble(data[1]);
        typeOfRecord = data[2];
        serpId = data[3];
        urlId = data[4];
    }

    public void print() {
        System.out.println("      URLID: " + urlId + "; Time: " + timePassed);
    }
}
