import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Crawler extends FileControl {
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
                new File(crawlPathString + "/" + folderNum).mkdirs();
                String weblink = links.get(folderNum);
                boolean linkExists = false;
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
                                    index += 2;
                                }
                            } else {
                                break;
                            }
                        } else {

                            // Parses the text
                            if ("</p>".equals(docString.substring(index, index + 4))) {
                                writeFile(new_text.toString(), String.valueOf(folderNum), "/page_text.txt");
                                // Adds the amount of times the word appears in the doc
                                // The "\\R" splits the string into an array of strings separated by new lines
                                for (String word : docString.split("\\R")) {
                                    if (wordPerDoc.get(word) == null) {
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

                                writeFile(new_text.toString(), String.valueOf(folderNum), "/outgoing_links.txt");
                                edit_text = false;
                                new_text = new StringBuilder();

                            // Parses the title
                            } else if ("</title>".equals(docString.substring(index, index + 8))) {
                                // Adds the title and link of the website to a file
                                new_text.append("\n").append(weblink);
                                writeFile(new_text.toString(), String.valueOf(folderNum), "/title_and_link.txt");
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
                String currentLink = readFile(folder.getName(), "/title_and_link.txt").split("\\R")[1];

                // For each of the outgoing links
                for (String outgoingLink : readFile(folder.getName(),
                        "/outgoing_links.txt").split("\\R")) {
                    // Add link to the current incoming links file
                    writeFile(currentLink, linkLocations.get(outgoingLink).toString(), "/incoming_links.txt");
                }
            }

        } catch (IOException e) {
            // IOException will happen if the link is invalid then there is no point in
            // continuing

            // StringIndexOutOfBoundsException will happen at the end of every file reading because the program
            // will attempt to read past the last characters
        }
    }
}
