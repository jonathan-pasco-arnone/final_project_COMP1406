public class SingleSearchResult implements SearchResult {
    private final String title;
    private final double score;
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
