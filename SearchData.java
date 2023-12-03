import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Math.log;

public class SearchData extends FileControl {
    public List<String> getOutgoingLinks(String url) {
        Hashtable<String, Integer> linkLocations = parseHashTable(parsedPathString, "link_locations.txt");

        if (linkLocations == null) {
            return null;
        }

        String outgingString = readFile(crawlPathString + linkLocations.get(url),
                "/outgoing_links.txt");
        return new ArrayList<>(Arrays.asList(outgingString.split("\\R")));
    }

    public List<String> getIncomingLinks(String url) {
        Hashtable<String, Integer> linkLocations = parseHashTable(parsedPathString, "link_locations.txt");

        if (linkLocations.equals(null)) {
            return null;
        }

        String incomingString = readFile(crawlPathString + linkLocations.get(url),
                "/incoming_links.txt");
        return new ArrayList<>(Arrays.asList(incomingString.split("\\R")));
    }

    /*public double getPageRank(String url) {

    }*/

    public double getIDF(String word) {
        Hashtable<String, Integer> idfs = parseHashTable(parsedPathString, "idf.txt");
        if (idfs == null) {
            return 0;
        }
        return idfs.get(word);
    }

    public double getTF(String url, String word) {
        double timeWordAppears = 0;

        Hashtable<String, Integer> linkLocations = parseHashTable(parsedPathString, "link_locations.txt");

        if (linkLocations == null) {
            return 0;
        }

        String fileText = readFile(crawlPathString + linkLocations.get(url), "page_text.txt");
        String[] wordList = fileText.split("\\R");
        System.out.println(wordList);
        for (String singleWord : wordList) {
            if (singleWord.equals(word)) {
                timeWordAppears++;
            }
        }
        return timeWordAppears / wordList.length;
    }

    public double getTFIDF(String url, String word) {
        // Using log identity for log base 2
        return (log(1 + getTF(url, word)) / log(2)) * getIDF(word);
    }
}
