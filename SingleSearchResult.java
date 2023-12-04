public class SingleSearchResult implements SearchResult {
    private String title;
    private double score;
    public String getTitle() { return title; }

    public double getScore() { return score; }

    public SingleSearchResult(String initialTitle, double initialScore) {
        title = initialTitle;
        score = initialScore;
    }

    public String toString() {
        return getTitle() + " | " + getScore();
    }
}
