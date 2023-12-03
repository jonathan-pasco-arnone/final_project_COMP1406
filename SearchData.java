import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class SearchData extends FileControl {
    public List<String> getOutgoingLinks(String url) {
        Hashtable<String, Integer> linkLocations = parseHashTable(parsedPathString, "link_locations.txt");

        String outgingString = readFile(crawlPathString + linkLocations.get(url),
                "/outgoing_links.txt");
        return new ArrayList<>(Arrays.asList(outgingString.split("\\R")));
    }

    public List<String> getIncomingLinks(String url) {
        Hashtable<String, Integer> linkLocations = parseHashTable(parsedPathString, "link_locations.txt");

        String incomingString = readFile(crawlPathString + linkLocations.get(url),
                "/incoming_links.txt");
        return new ArrayList<>(Arrays.asList(incomingString.split("\\R")));
    }

    /*public double getPageRank(String url) {

    }*/

    public double getIDF(String word) {
        Hashtable<String, Integer> idfs = parseHashTable(parsedPathString, "idf.txt");
        return idfs.get(word);
    }

    /*public double getTF(String url, String word) {

    }

    public double getTFIDF(String url, String word) {

    }*/
}
