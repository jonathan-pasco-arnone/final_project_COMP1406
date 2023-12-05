import java.util.*;

import static java.lang.Math.cos;
import static java.lang.Math.log;

public class SearchEngine extends FileControl {

    public final Hashtable<String, Integer> linkLocations;

    public SearchEngine(Hashtable<String, Integer> locationsOfLinks) {
        linkLocations = locationsOfLinks;
    }

    public List<SearchResult> search(String query, boolean boost, int X) {

        SearchData searchDataClass = new SearchData(linkLocations);
        ArrayList<SearchResult> topX = new ArrayList<>();

        // Create a list WITH duplicates
        String[] allwords = query.split("\\s+");
        // Creates a list with NO duplicates of every word in the phrase
        TreeSet<String> nonDuplicateWords = new TreeSet<>(Arrays.asList(query.split("\\s+")));
        Hashtable<String, Integer> wordQuantities = new Hashtable<>();

        for (String word : allwords) {
            if (wordQuantities.containsKey(word)) {
                wordQuantities.put(word, wordQuantities.get(word) + 1);
            } else {
                wordQuantities.put(word, 1);
            }
        }

        if (linkLocations == null) {
            return null;
        }

        for (String link : linkLocations.keySet()) {
            String[] fileText = readFile(CRAWLPATHSTRING + linkLocations.get(link),
                    "/title_and_link.txt").split("\\R");

            String fileTitle = fileText[0];
            String fileLink = fileText[1];

            // Cosine similarity
            double cosineSimilarity = 0;
            double numerator = 0;
            double denominatorQ = 0;
            double denominatorD = 0;


            for (String nonDuplicateWord : nonDuplicateWords) {

                // Get the amount of times the word appears in the query
                double wordQuantity = wordQuantities.get(nonDuplicateWord);
                double tfQ = wordQuantity / allwords.length;
                double qValue = log(1 + tfQ) / log(2) * searchDataClass.getIDF(nonDuplicateWord);
                double dValue = searchDataClass.getTFIDF(fileLink, nonDuplicateWord);
                numerator += qValue * dValue;
                denominatorQ += qValue * qValue;
                denominatorD += dValue * dValue;

            }

            if (denominatorD != 0) {
                cosineSimilarity = numerator / (Math.sqrt(denominatorQ) * (Math.sqrt(denominatorD)));
                if (boost) {
                    cosineSimilarity *= searchDataClass.getPageRank(fileLink);
                }
            }
            /*
            * I would have used the collections interface and implemented a custom compareTo() method in my
            * SingleSearchResult class, however since the ProjectTester interface requires a return value of
            * List<SearchResult>, I am unable to use additional functions in my SingleSearchResult class
            * */

            // Place the new file's cosine similarity into the list in the correct spot
            int placementLocation = 0;
            int counter = 0;
            for (SearchResult result : topX) {
                // This is my equivalent for rounding both to 3 decimal places (the + 0.0005 is to insure everything is
                // rounded fairly)
                int resultScore = (int) ((result.getScore() + 0.0005)* 1000);
                int cosineSimilarityScore = (int) ((cosineSimilarity + 0.0005) * 1000);

                // If the score up to 3 decimal places is bigger, then set as placement location
                // OR if the score is equal up to 3 decimal places, then compare the titles
                if (resultScore < cosineSimilarityScore || (resultScore == cosineSimilarityScore &&
                        (result.getTitle().compareTo(fileTitle) >= 0))) {
                    placementLocation = counter;
                    break;
                }
                if (counter == topX.size() - 1) {
                    placementLocation = topX.size();
                }
                counter++;
            }


            topX.add(placementLocation, new SingleSearchResult(fileTitle, cosineSimilarity));
            if (topX.size() > X) {
                topX.remove(X);
            }

        }
        return topX;
    }
}
