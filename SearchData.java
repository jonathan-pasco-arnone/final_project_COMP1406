import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Math.log;

public class SearchData extends FileControl {
    private final Hashtable<String, Integer> linkLocations;
    private final Hashtable<String, Integer> idfs;

    // This constructor will be called after a crawl is called
    public SearchData(Hashtable<String, Integer> locationOfLinks) {
        linkLocations = locationOfLinks;
        idfs = (Hashtable<String, Integer>) deserialize(PARSEDPATHSTRING, "idf.txt");
    }

    public List<String> getOutgoingLinks(String url) {
        if (linkLocations == null) {
            return null;
        }

        String outgingString = readFile(CRAWLPATHSTRING + linkLocations.get(url),
                "/outgoing_links.txt");

        if (outgingString == null) {
            return null;
        }

        return new ArrayList<>(Arrays.asList(outgingString.split("\\R")));
    }

    public List<String> getIncomingLinks(String url) {
        if (linkLocations == null) {
            return null;
        }

        String incomingString = readFile(CRAWLPATHSTRING + linkLocations.get(url),
                "/incoming_links.txt");

        if (incomingString == null) {
            return null;
        }

        return new ArrayList<>(Arrays.asList(incomingString.split("\\R")));
    }

    public double getPageRank(String url) {
        double[] pageRanks = (double[]) deserialize(PARSEDPATHSTRING, "page_ranks.txt");

        if (linkLocations == null || pageRanks == null || linkLocations.get(url) == null) {
            return -1;
        }

        return pageRanks[linkLocations.get(url)];

    }

    public double getIDF(String word) {
        if (linkLocations == null || idfs == null || idfs.get(word) == null) {
            return 0;
        }
        return log((double) linkLocations.size() / (1 + idfs.get(word))) / log(2);
    }

    public double getTF(String url, String word) {
        double timeWordAppears = 0;

        if (linkLocations == null || linkLocations.get(url) == null) {
            return 0;
        }

        String fileText = readFile(CRAWLPATHSTRING + linkLocations.get(url), "/page_text.txt");
        String[] wordList = fileText.split("\\R");
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
