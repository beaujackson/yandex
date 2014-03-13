/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.utils;

public class Pad {

    public static String right(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

}
