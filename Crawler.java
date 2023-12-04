import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Crawler extends FileControl implements Serializable {
    public void initialize() {
        // Deletes all the folders and files
        if (Files.exists(Paths.get(crawlPathString))) {
            File[] directories = new File(crawlPathString).listFiles(File::isDirectory);
            for (File folder : directories) {
                File[] allFiles = new File(folder.getPath()).listFiles();
                for (File individualFile : allFiles) {
                    individualFile.delete();
                }
                folder.delete();
            }
        }
        if (Files.exists(Paths.get(parsedPathString))) {
            File[] files = new File(parsedPathString).listFiles();
            for (File sinlgeFile : files) {
                sinlgeFile.delete();
            }
        }
    }

    public void crawl(String seedURL) {
        try {
            new File(crawlPathString).mkdirs();
            // Do some crawling
            Hashtable<String, Integer> linkLocations = new Hashtable<>();
            linkLocations.put(seedURL, 0);

            ArrayList<String> links = new ArrayList<>();
            links.add(seedURL);

            Hashtable<String, Integer> wordPerDoc = new Hashtable<>();

            int folderNum = 0;
            while (folderNum < links.size()) {
                new File(crawlPathString + folderNum).mkdirs();
                String weblink = links.get(folderNum);
                    String docString = WebRequester.readURL(weblink);
                    boolean edit_text = false;

                    // Use string builder because it is faster when adding letter by letter
                    StringBuilder new_text = new StringBuilder();
                    int index = 0;
                    while (index < docString.length()) {

                        if (!edit_text) {
                            // Determines if the current character is within any of the opening sections by first
                            // checking if there are enough characters left (if there are less than 8 characters then
                            // none of the tags could be opened and closed with content in between)
                            if (index < (docString.length() - 8)) {
                                if ("<a href=\"".equals(docString.substring(index, index + 9))) {
                                    edit_text = true;
                                    index += 8;
                                }
                                if ("<title>".equals(docString.substring(index, index + 7))) {
                                    edit_text = true;
                                    index += 6;
                                }
                                if ("<p>".equals(docString.substring(index, index + 3))) {
                                    edit_text = true;
                                    // Would be +2 but since the <p> tag always has a new line after it, an additional
                                    // +1 is added
                                    index += 3;
                                }
                            } else {
                                break;
                            }
                        } else {

                            // Parses the text
                            if ("</p>".equals(docString.substring(index, index + 4))) {
                                writeFile(new_text.toString(), crawlPathString + String.valueOf(folderNum), "/page_text.txt");
                                // Adds the amount of times the word appears in the doc
                                // The "\\R" splits the string into an array of strings separated by new lines
                                // Using a tree set ensures no duplicates
                                for (String word : new TreeSet<>(Arrays.asList(docString.split("\\R")))) {
                                    if (!wordPerDoc.containsKey(word)) {
                                        wordPerDoc.put(word, 1);
                                    } else {
                                        wordPerDoc.put(word, wordPerDoc.get(word) + 1);
                                    }
                                }
                                edit_text = false;
                                new_text = new StringBuilder();

                            // Parses the links
                            } else if ("\">".equals(docString.substring(index, index + 2))) {
                                // Is the link local
                                if (!new_text.substring(0, 7).equals("http://")) {
                                    int counter = weblink.length() - 1;
                                    while (counter >= 0) {
                                        if (weblink.substring(counter, counter + 1).equals("/")) {
                                            break;
                                        }
                                        counter--;
                                    }
                                    // Adds the global link to the local one
                                    new_text = new StringBuilder(weblink.substring(0,counter)
                                            + new_text.substring(1, new_text.length()));
                                }

                                // If the link has not already been crawled
                                if (!links.contains(new_text.toString())) {
                                    linkLocations.put(new_text.toString(), links.size());
                                    links.add(new_text.toString());
                                }

                                writeFile(new_text.toString(), crawlPathString + String.valueOf(folderNum), "/outgoing_links.txt");
                                edit_text = false;
                                new_text = new StringBuilder();

                            // Parses the title
                            } else if ("</title>".equals(docString.substring(index, index + 8))) {
                                // Adds the title and link of the website to a file
                                new_text.append("\n").append(weblink);
                                writeFile(new_text.toString(), crawlPathString + String.valueOf(folderNum), "/title_and_link.txt");
                                edit_text = false;
                                new_text = new StringBuilder();

                            } else {
                                new_text.append(docString.charAt(index));
                            }
                        }

                        index++;
                    }
                folderNum++;
            }

            File[] directories = new File(crawlPathString).listFiles(File::isDirectory);
            for (File folder : directories) {
                // Grabs the current folder's link
                String currentLink = readFile(crawlPathString + folder.getName(), "/title_and_link.txt").split("\\R")[1];

                // For each of the outgoing links
                for (String outgoingLink : readFile(crawlPathString + folder.getName(),
                        "/outgoing_links.txt").split("\\R")) {
                    // Add link to the current incoming links file
                    writeFile(currentLink, crawlPathString + linkLocations.get(outgoingLink).toString(), "/incoming_links.txt");
                }
            }

            new File(parsedPathString).mkdir();

            // Serializing
            serialize(parsedPathString, "link_locations.txt", linkLocations);
            serialize(parsedPathString, "idf.txt", wordPerDoc);

            /*
            *
            * Page rank calculation
            *
            * */

            // The main matrix of the problem
            double[][] probabilityMatrix = new double[linkLocations.size()][linkLocations.size()];
            double[] basicVector = new double[linkLocations.size()];
            double alpha = 0.1;

            basicVector[0] = 1;

            // Fill out the probability matrix
            for (String link : linkLocations.keySet()) {
                SearchData searchDataInstance = new SearchData();
                List<String> incomingLinks =  searchDataInstance.getIncomingLinks(link);
                double chancePerPage = (double) 1 / incomingLinks.size();

                // Add the default value
                int count = 0;
                while (count != probabilityMatrix[linkLocations.get(link)].length) {
                    probabilityMatrix[linkLocations.get(link)][count] = alpha / linkLocations.size();
                    count++;
                }

                // Add the probability of each incoming page
                for (String singleIncomingLink : incomingLinks) {
                    probabilityMatrix[linkLocations.get(link)][linkLocations.get(singleIncomingLink)] = chancePerPage
                            * (1 - alpha) + alpha / linkLocations.size();
                }
            }

            /*
            *
            * Remove vector b and initialize it inside the while loop
            *
            *
            * */
            double[] vectorB;
            double euclideanDistance = 1;
            while (euclideanDistance > 0.0001) {
                vectorB = basicVector;

                // Multiply the matrix by the vector
                double[] newVector = new double[linkLocations.size()];
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

            serialize(parsedPathString, "page_ranks.txt", basicVector);

        } catch (IOException e) {
            // IOException will happen if the link is invalid
            // In which case, there is no point in continuing
        }
    }
}
