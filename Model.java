import java.util.List;

public class Model {

    // Instantiate classes
    public Crawler crawlClass;
    public SearchData searchDataClass;
    public Search searchClass;

    public Model() {
        crawlClass = new Crawler();
        searchDataClass = new SearchData();
        searchClass = new Search();
    }

    public void initialize() {
        crawlClass.initialize();
    }

    public void crawl(String seedURL) {
        crawlClass.crawl(seedURL);
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

    /*public List<SearchResult> search(String query, boolean boost, int X) {
        return searchClass.search(query, boost, X);
    }*/

}
