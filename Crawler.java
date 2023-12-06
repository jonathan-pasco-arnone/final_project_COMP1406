import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Crawler extends FileControl implements Serializable {
    private void generateOutgoingLinkS(Hashtable<String, Integer> locationsOfLinks) throws IOException {
        File[] directories = new File(CRAWLPATHSTRING).listFiles(File::isDirectory);
        for (File folder : directories) {
            // Grabs the current folder's link
            String currentLink = readFile(CRAWLPATHSTRING + folder.getName(),
                    "/title_and_link.txt").split("\\R")[1];

            // For each of the outgoing links
            for (String outgoingLink : readFile(CRAWLPATHSTRING + folder.getName(),
                    "/outgoing_links.txt").split("\\R")) {
                // Add link to the current incoming links file
                writeFile(currentLink, CRAWLPATHSTRING + locationsOfLinks.get(outgoingLink).toString(), "/incoming_links.txt");
            }
        }
    }

    private double[] generatePageRanks(Hashtable<String, Integer> locationsOfLinks) {
        // The main matrix of the problem
        double[][] probabilityMatrix = new double[locationsOfLinks.size()][locationsOfLinks.size()];
        double[] basicVector = new double[locationsOfLinks.size()];
        double alpha = 0.1;

        basicVector[0] = 1;

        // Fill out the probability matrix
        for (String link : locationsOfLinks.keySet()) {
            String[] incomingLinks = readFile(CRAWLPATHSTRING + locationsOfLinks.get(link),
                    "/incoming_links.txt").split("\\R");
            double chancePerPage = (double) 1 / incomingLinks.length;

            // Add the default value
            int count = 0;
            while (count != probabilityMatrix[locationsOfLinks.get(link)].length) {
                probabilityMatrix[locationsOfLinks.get(link)][count] = alpha / locationsOfLinks.size();
                count++;
            }

            // Add the probability of each incoming page
            for (String singleIncomingLink : incomingLinks) {
                probabilityMatrix[locationsOfLinks.get(link)][locationsOfLinks.get(singleIncomingLink)] =
                        chancePerPage * (1 - alpha) + alpha / locationsOfLinks.size();
            }
        }

        // Remove vector b and initialize it inside the while loop
        double euclideanDistance = 1;
        while (euclideanDistance > 0.00001) {
            double[] vectorB = basicVector;

            // Multiply the matrix by the vector
            double[] newVector = new double[locationsOfLinks.size()];
            int columnIndexProbabilityMatrix = 0;
            // Cycles through each column of the probability matrix
            while (columnIndexProbabilityMatrix != probabilityMatrix[0].length) {
                int columnIndexBasicVector = 0;
                double newValue = 0;
                for (double[] rowProbabilityMatrix : probabilityMatrix) {
                    newValue += basicVector[columnIndexBasicVector]
                            * rowProbabilityMatrix[columnIndexProbabilityMatrix];
                    columnIndexBasicVector++;
                }
                newVector[columnIndexProbabilityMatrix] = newValue;
                columnIndexProbabilityMatrix++;
            }

            basicVector = newVector;

            // Calculate euclidean distance
            euclideanDistance = 0;
            int index = 0;
            while (index != vectorB.length) {
                euclideanDistance += Math.pow(vectorB[index] - basicVector[index], 2);
                index++;
            }
            euclideanDistance = Math.sqrt(euclideanDistance);
        }
        return basicVector;
    }

    public void initialize() {
        // Deletes all the folders and files
        if (Files.exists(Paths.get(CRAWLPATHSTRING))) {
            File[] directories = new File(CRAWLPATHSTRING).listFiles(File::isDirectory);
            for (File folder : directories) {
                File[] allFiles = new File(folder.getPath()).listFiles();
                for (File individualFile : allFiles) {
                    individualFile.delete();
                }
                folder.delete();
            }
        }
        if (Files.exists(Paths.get(PARSEDPATHSTRING))) {
            File[] files = new File(PARSEDPATHSTRING).listFiles();
            for (File sinlgeFile : files) {
                sinlgeFile.delete();
            }
        }
    }

    public void crawl(String seedURL) {
        new File(CRAWLPATHSTRING).mkdir();
        new File(PARSEDPATHSTRING).mkdir();

        // Do some crawling
        Hashtable<String, Integer> locationsOfLinks = new Hashtable<>();
        locationsOfLinks.put(seedURL, 0);

        ArrayList<String> links = new ArrayList<>();
        links.add(seedURL);

        Hashtable<String, Integer> wordPerDoc = new Hashtable<>();

        int folderNum = 0;
        while (folderNum < links.size()) {
            try {
                new File(CRAWLPATHSTRING + folderNum).mkdir();
                String weblink = links.get(folderNum);
                String docString = WebRequester.readURL(weblink);
                boolean editText = false;

                // Use string builder because it is faster when adding letter by letter
                StringBuilder newText = new StringBuilder();
                int index = 0;
                while (index < docString.length()) {

                    if (!editText) {
                        // Determines if the current character is within any of the opening sections by first
                        // checking if there are enough characters left (if there are less than 8 characters then
                        // none of the tags could be opened and closed with content in between)
                        if (index < (docString.length() - 8)) {
                            if ("<a href=\"".equals(docString.substring(index, index + 9))) {
                                editText = true;
                                index += 8;
                            }
                            if ("<title>".equals(docString.substring(index, index + 7))) {
                                editText = true;
                                index += 6;
                            }
                            if ("<p>".equals(docString.substring(index, index + 3))) {
                                editText = true;
                                index += 2;
                            }
                        } else {
                            break;
                        }
                    } else {

                        // Parses the text
                        if ("</p>".equals(docString.substring(index, index + 4))) {
                            writeFile(newText.toString(), CRAWLPATHSTRING + folderNum,
                                    "/page_text.txt");
                            // Adds the amount of times the word appears in the doc
                            // The "\\R" splits the string into an array of strings separated by new lines
                            for (String word : new TreeSet<>(Arrays.asList(docString.split("\\R")))) {
                                if (!wordPerDoc.containsKey(word)) {
                                    wordPerDoc.put(word, 1);
                                } else {
                                    wordPerDoc.put(word, wordPerDoc.get(word) + 1);
                                }
                            }
                            editText = false;
                            newText = new StringBuilder();

                            // Parses the links
                        } else if ("\">".equals(docString.substring(index, index + 2))) {
                            // Is the link local
                            if (!newText.substring(0, 7).equals("http://")) {
                                int counter = weblink.length() - 1;
                                while (counter >= 0) {
                                    if (weblink.substring(counter, counter + 1).equals("/")) {
                                        break;
                                    }
                                    counter--;
                                }
                                // Adds the global link to the local one
                                newText = new StringBuilder(weblink.substring(0, counter)
                                        + newText.substring(1, newText.length()));
                            }

                            // If the link has not already been crawled
                            if (!links.contains(newText.toString())) {
                                locationsOfLinks.put(newText.toString(), links.size());
                                links.add(newText.toString());
                            }

                            writeFile(newText.toString(), CRAWLPATHSTRING + folderNum,
                                    "/outgoing_links.txt");
                            editText = false;
                            newText = new StringBuilder();

                            // Parses the title
                        } else if ("</title>".equals(docString.substring(index, index + 8))) {
                            // Adds the title and link of the website to a file
                            newText.append("\n").append(weblink);
                            writeFile(newText.toString(), CRAWLPATHSTRING + folderNum,
                                    "/title_and_link.txt");
                            editText = false;
                            newText = new StringBuilder();

                        } else {
                            newText.append(docString.charAt(index));
                        }
                    }

                    index++;
                }
                folderNum++;
            } catch (IOException e) {
                // IOException will happen if the link is invalid
                // In which case, there is no point in continuing with this link and should move on to the next one
            }
        }

        try {

            generateOutgoingLinkS(locationsOfLinks);

            // Serializing
            serialize(PARSEDPATHSTRING, "link_locations.txt", locationsOfLinks);
            serialize(PARSEDPATHSTRING, "idf.txt", wordPerDoc);
            serialize(PARSEDPATHSTRING, "page_ranks.txt", generatePageRanks(locationsOfLinks));

        } catch (IOException e) {
            // If the crawl was done correctly, then this will never be called
            // This is more to appease the linter
        }
    }
}
