import java.util.*;

import static java.lang.Math.log;

public class Search extends FileControl {

    public List<SearchResult> search(String query, boolean boost, int X) {

        SearchData searchDataClass = new SearchData();
        ArrayList<SearchResult> topX = new ArrayList<>();

        // Create a list WITH duplicates
        String[] allwords = query.split("\\s+");
        // Creates a list with NO duplicates of every word in the phrase
        TreeSet<String> nonDuplicateWords = new TreeSet<>(Arrays.asList(query.split("\\s+")));

        Hashtable<String, Integer> linkLocations = (Hashtable<String, Integer>)
                deserialize(parsedPathString, "link_locations.txt");

        if (linkLocations == null) {
            return null;
        }

        for (String link : linkLocations.keySet()) {
            String[] fileText = readFile(crawlPathString + linkLocations.get(link),
                    "/title_and_link.txt").split("\\R");

            // This will only ever happen if there has been no crawl
            if (fileText == null) {
                return null;
            }

            String fileTitle = fileText[0];
            String fileLink = fileText[1];

            // Cosine similarity
            double cosineSimilarity = 0;
            double numerator = 0;
            double denominatorQ = 0;
            double denominatorD = 0;
            for (String nonDuplicateWord : nonDuplicateWords) {
                // Get the amount of times the word appears in the query
                double wordQuantity = 0;
                for (String word : allwords) {
                    if (nonDuplicateWord.equals(word)) {
                        wordQuantity++;
                    }
                }
                double tfQ = wordQuantity / allwords.length;
                double qValue = log(1 + tfQ) / log(2) * searchDataClass.getIDF(nonDuplicateWord);
                double dValue = searchDataClass.getTFIDF(fileLink, nonDuplicateWord);
                numerator += qValue * dValue;
                denominatorQ += qValue * qValue;
                denominatorD += dValue * dValue;
            }
            if (denominatorD != 0) {
                if (boost) {
                    cosineSimilarity = searchDataClass.getPageRank(fileLink) * numerator
                            / (Math.sqrt(denominatorQ) * (Math.sqrt(denominatorD)));
                } else {
                    cosineSimilarity = numerator / (Math.sqrt(denominatorQ) * (Math.sqrt(denominatorD)));
                }
            }

            // Place the new file's cosine similarity into the list in the correct spot
            int placementLocation = 0;
            int counter = 0;
            for (SearchResult result : topX) {
                if (result.getScore() < cosineSimilarity) {
                    placementLocation = counter;
                    break;
                }
                if (counter == topX.size() - 1) {
                    placementLocation = topX.size();
                }
                counter++;
            }

            // Building all the parts of the dictionary
            topX.add(placementLocation, new SingleSearchResult(fileTitle, cosineSimilarity));
            // If the cosine similarity is in the top X values
            if (topX.size() > X) {
                topX.remove(X);
            }
        }
        return topX;
    }
}
