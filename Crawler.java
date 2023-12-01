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
        new File(crawlPathString).mkdirs();
        // Do some crawling
        Dictionary<String, Integer> linkLocations = new Hashtable<>();
        linkLocations.put(seedURL, 0);
        ArrayList<String> links = new ArrayList<>();
        links.add(seedURL);

        Dictionary<String, Integer> wordPerDoc = new Hashtable<>();

        int folderNum = 0;
        while (folderNum < links.size()) {
            new File(crawlPathString + "/" + folderNum).mkdirs();
            String weblink = links.get(folderNum);
            boolean linkExists = false;
            try {
                String docString = WebRequester.readURL(weblink);
                boolean edit_text = false;
                StringBuilder new_text = new StringBuilder();
                int index = 0;
                while (index < docString.length()) {

                    if (!edit_text) {
                        // Determines if the current character is within any of the opening sections
                        if ("<p>".equals(docString.substring(index, index + 3))) {
                            edit_text = true;
                            index += 2;
                        } else if ("<title>".equals(docString.substring(index, index + 7))) {
                            edit_text = true;
                            index += 6;
                        } else if ("<a href=\"".equals(docString.substring(index, index + 9))) {
                            edit_text = true;
                            index += 8;
                        }
                    } else {

                        // Parses the text
                        if ("</p>".equals(docString.substring(index, index + 4))) {
                            writeFile(new_text.toString(), String.valueOf(folderNum), "/page_text.txt");
                            edit_text = false;
                            new_text = new StringBuilder();

                        // Parses the links
                        } else if ("\">".equals(docString.substring(index, index + 2))) {
                            // Is the link local
                            if (!new_text.substring(0, 7).equals("http://")) {
                                int counter = weblink.length() - 1;
                                while (counter >= 0) {
                                    if (!weblink.substring(counter, counter + 1).equals("/")) {
                                        break;
                                    }
                                    counter--;
                                }
                                new_text = new StringBuilder(weblink.substring(0,counter) + new_text);
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
            } catch (IOException | StringIndexOutOfBoundsException e) {
                // IOException will happen if the link is invalid then there is no point in
                // continuing

                // StringIndexOutOfBoundsException will happen at the end of every file reading because the program
                // will attempt to read past the last characters
            }

            folderNum++;
        }
    }
}
