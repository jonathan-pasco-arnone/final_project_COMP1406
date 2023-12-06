// The Model portion of the MVC
import java.util.Hashtable;
import java.util.List;

public class Model extends FileControl implements ProjectTester {

    // Instantiate classes
    private Crawler crawlClass;
    private SearchData searchDataClass;
    private SearchEngine searchClass;

    public Model() {
        crawlClass = new Crawler();

        // Getting this variable and passing it into each instance instead of having each class deserialize it on their
        // own (If no crawl has been done then the variable will be null. This is ok because without a crawl, none of
        // these functions will work anyway)
        Hashtable<String, Integer> linkLocations = (Hashtable<String, Integer>)
                deserialize(PARSEDPATHSTRING, "link_locations.txt");

        searchDataClass = new SearchData(linkLocations);
        searchClass = new SearchEngine(linkLocations);
    }

    public void initialize() {
        crawlClass.initialize();
    }

    public void crawl(String seedURL) {
        crawlClass.crawl(seedURL);

        // Must reinitialize these instances because every crawl could change the parsed files that are read as soon as
        // the instance is initialized
        Hashtable<String, Integer> linkLocations = (Hashtable<String, Integer>)
                deserialize(PARSEDPATHSTRING, "link_locations.txt");

        searchDataClass = new SearchData(linkLocations);
        searchClass = new SearchEngine(linkLocations);
    }

    public List<String> getOutgoingLinks(String url) {
        return searchDataClass.getOutgoingLinks(url);
    }

    public List<String> getIncomingLinks(String url) {
        return searchDataClass.getIncomingLinks(url);

    }

    public double getPageRank(String url) {
        return searchDataClass.getPageRank(url);
    }

    public double getIDF(String word) {
        return searchDataClass.getIDF(word);
    }

    public double getTF(String url, String word) {
        return searchDataClass.getTF(url, word);
    }

    public double getTFIDF(String url, String word) {
        return searchDataClass.getTFIDF(url, word);
    }

    public List<SearchResult> search(String query, boolean boost, int X) {
        return searchClass.search(query, boost, X);
    }

}
