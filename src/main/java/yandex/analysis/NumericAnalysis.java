/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.analysis;

import yandex.utils.CompareDouble;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NumericAnalysis {
    private static String COMMA = ",";
    public double high = 0;
    public double low = Double.MAX_VALUE;
    public int count = 0;
    public double mean;
    public double runningTotal = 0;
    public double quartile;
    public double q1 = 0;
    public double q2 = 0;
    public double q3 = 0;
    public double q4 = 0;

    public void setVal(Double value) {
        count++;

        if (value == null) {
            return;
        }

        runningTotal += value;

        if(value > high) {
            high = value;
        }

        if (value < low) {
            low = value;
        }

        mean = runningTotal / count;

        quartile = (high - low) / 4;

        q1 = low + quartile;
        q2 = low + (quartile * 2);
        q3 = low + (quartile * 3);
        q4 = low + (quartile * 4);
    }

    public void distribution(List<Double> values) {
        Collections.sort(values, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return CompareDouble.compare(o1, o2);
            }
        });

        long q1Count = 0;
        long q2Count = 0;
        long q3Count = 0;
        long q4Count = 0;

        for (Double value : values) {
            if (value <= q1) {
                q1Count++;
            } else if (value <= q2) {
                q2Count++;
            } else if (value <= q3) {
                q3Count++;
            } else {
                q4Count++;
            }
        }

        System.out.println("low - high: " + low + " - " + high);
        System.out.println("quartile: " + quartile);
        System.out.println("q1: " + q1Count);
        System.out.println("q2: " + q2Count);
        System.out.println("q3: " + q3Count);
        System.out.println("q4: " + q4Count);
    }

    public String getCSV() {
        StringBuilder builder = new StringBuilder();
        builder.append(high + COMMA);
        builder.append(low + COMMA);
        builder.append(count + COMMA);
        builder.append(mean + COMMA);
        builder.append(runningTotal + COMMA);
        builder.append(quartile);

        return builder.toString();
    }
}
