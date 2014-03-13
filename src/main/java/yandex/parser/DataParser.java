/*
* $Header$
* $Revision 1 $
* $Author: wjackson $
* &copy; $Date: 11/15/13$ Expedia Inc. PROPRIETARY AND CONFIDENTIAL
*/

package yandex.parser;

import yandex.App;
import yandex.containers.SearchSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataParser {

    private static String TAB = "\t";

    private String dataFilePath;
    private Long sessionCount;

    public DataParser(String dataFilePath, Long count) {
        this.dataFilePath = dataFilePath;
        this.sessionCount = count;
    }

    public void sampleRaw() {
        try {
            BufferedReader reader = getReader();

            String line = null;
            long readCount = 0;
            String lastSessionId = "";

            while (((line = reader.readLine()) != null) && readCount < sessionCount) {
                String[] data = line.split(TAB);

                if (!lastSessionId.equals(data[0])) {
                    System.out.println("-------------------------------------------------");
                    lastSessionId = data[0];
                    readCount++;
                }

                System.out.println(line.replace(TAB, " | "));
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sample() {
        try {
            BufferedReader reader = getReader();

            long readCount = 0;
            String line = reader.readLine();
            String[] data = line.split(TAB);
            String lastSessionId = data[0];

            while (readCount < sessionCount && line != null) {

                SearchSession session = new SearchSession();

                while (lastSessionId.equals(data[0])) {

                    parseLine(session, data);

                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    data = line.split(TAB);
                }

                session.print();

                lastSessionId = data[0];
                readCount++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void train() {
        try {
            BufferedReader reader = getReader();

            long readCount = 0;
            String line = reader.readLine();
            String[] data = line.split(TAB);
            String lastSessionId = data[0];

            while (readCount < sessionCount && line != null) {

                SearchSession session = new SearchSession();

                while (lastSessionId.equals(data[0])) {

                    parseLine(session, data);

                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    data = line.split(TAB);
                }

                session.train();

                lastSessionId = data[0];
                readCount++;

                if (readCount % 10000 == 0) {
                    System.out.print(".");
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test() {

        File results = new File(App.resultFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(results, true));

            writer.write("SessionID,URLID");
            writer.newLine();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = getReader();

            long readCount = 0;
            String line = reader.readLine();
            String[] data = line.split(TAB);
            String lastSessionId = data[0];

            while (readCount < sessionCount && line != null) {

                SearchSession session = new SearchSession();

                while (lastSessionId.equals(data[0])) {

                    parseLine(session, data);

                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    data = line.split(TAB);
                }

                session.test();

                lastSessionId = data[0];
                readCount++;

                if (readCount % 10000 == 0) {
                    System.out.print(".");
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseLine(SearchSession session, String[] data) {

        if ("M".equals(data[1])) {
            session.parseMetadata(data);
        } else if ("Q".equals(data[2]) || "T".equals(data[2])) {
            session.addQuery(data);
        } else if ("C".equals(data[2])) {
            session.addClick(data);
        }
    }

    private BufferedReader getReader() throws IOException {
        File file = new File(dataFilePath);
        return new BufferedReader(new FileReader(file));
    }
}
