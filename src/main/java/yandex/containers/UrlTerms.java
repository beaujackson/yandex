/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/18/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.containers;

import yandex.analysis.NumericAnalysis;

import java.util.HashMap;

public class UrlTerms {

    public long total = 0;
    public HashMap<String, Integer> terms = new HashMap<>();
    public NumericAnalysis analysis = new NumericAnalysis();
}
