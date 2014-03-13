package yandex;

import yandex.analysis.ScoreCard;
import yandex.parser.DataParser;
import yandex.summarizers.ClickSummarizer;
import yandex.summarizers.TermFileBuilder;

import java.io.File;
import java.util.HashMap;

/**
 * App.
 */
public class App 
{
    private static String testFile = "/Users/wjackson/projects/yandex/data/test";
    private static String trainingFile = "/Users/wjackson/projects/yandex/data/train";
    public static String resultFile = "/Users/wjackson/projects/yandex/data/sorted";
    public static String clickTerms = "/Users/wjackson/projects/yandex/data/clickTerms";
    public static String termsFile = "/Users/wjackson/projects/yandex/data/termsFile";

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> parsedArgs = new HashMap<String, String>();

        for (int i=0; i<args.length; i++) {
            String arg = args[i];

            switch (arg) {

                case "-sampleraw":
                    parsedArgs.put("sampleraw", "true");
                    break;

                case "-sample":
                    parsedArgs.put("sample", "true");
                    break;

                case "-build":
                    parsedArgs.put("build", "true");
                    break;

                case "-train":
                    parsedArgs.put("dataFile", trainingFile);
                    parsedArgs.put("train", "true");
                    if (i + 1 < args.length) {
                        parsedArgs.put("count", args[++i]);
                    }
                    break;

                case "-traintest":
                    parsedArgs.put("dataFile", testFile);
                    parsedArgs.put("train", "true");
                    if (i + 1 < args.length) {
                        parsedArgs.put("count", args[++i]);
                    }
                    break;

                case "-test":
                    parsedArgs.put("dataFile", testFile);
                    parsedArgs.put("test", "true");
                    if (i + 1 < args.length) {
                        parsedArgs.put("count", args[++i]);
                    }
                    break;

                default:
                    break;
            }
        }

        return parsedArgs;
    }

    public static void main( String[] args )
    {
        try {
            HashMap<String, String> parsedArgs = parseArgs(args);

            Long count = parsedArgs.containsKey("count") ? Long.parseLong(parsedArgs.get("count")) : Long.MAX_VALUE;

            DataParser parser = new DataParser(parsedArgs.get("dataFile"), count);

            if (parsedArgs.containsKey("sampleraw")) {
                parser.sampleRaw();
                return;
            }

            if (parsedArgs.containsKey("sample")) {
                parser.sample();
                return;
            }

            if (parsedArgs.containsKey("train")) {
                System.out.print("Training.");

                parser.train();

                if (parsedArgs.containsKey("build")) {
                    //ClickSummarizer.buildClickTermsFile();
                    TermFileBuilder.instance.buildTermFile();
                }

                System.out.println();

                System.out.println("- Score -------------------------------------------------------------------------");
                ScoreCard.printScore();
                System.out.println();

//                System.out.println("- Click Summaries ---------------------------------------------------------------");
//                ClickSummarizer.printSummary(5);

//                System.out.println("- Search Term Summaries ---------------------------------------------------------");
//                SearchTermSummarizer.printSummary();

//                System.out.println("- Count Summaries ---------------------------------------------------------------");
//                CountSummarizer.printSummary(100);

//                System.out.println("- User Profiles -----------------------------------------------------------------");
//                UserProfile.printProfiles(80);

                return;
            }

            if (parsedArgs.containsKey("traintest")) {
                System.out.print("Training test file.");

                parser.train();

                if (parsedArgs.containsKey("build")) {
                    ClickSummarizer.buildClickTermsFile();
                }

                System.out.println();

                System.out.println("- Score -------------------------------------------------------------------------");
                ScoreCard.printScore();
                System.out.println();

//                System.out.println("- Click Summaries ---------------------------------------------------------------");
//                ClickSummarizer.printSummary(5);

//                System.out.println("- Search Term Summaries ---------------------------------------------------------");
//                SearchTermSummarizer.printSummary();

//                System.out.println("- Count Summaries ---------------------------------------------------------------");
//                CountSummarizer.printSummary(100);

//                System.out.println("- User Profiles -----------------------------------------------------------------");
//                UserProfile.printProfiles(80);

                return;
            }

            if (parsedArgs.containsKey("test")) {
                System.out.print("Testing.");

                File results = new File(resultFile);
                if (results.exists()) {
                    results.delete();
                }
                results.createNewFile();

                parser.test();
                return;
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
