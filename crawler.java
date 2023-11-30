import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class crawler {
    public String crawlPathString = "src/crawlData";
    public Path crawlPath = Paths.get(crawlPathString);

    public void initialize() {
        // Deletes all the folders and files
        if (Files.exists(crawlPath)) {
            File[] directories = new File(crawlPathString).listFiles(File::isDirectory);
            for (File folder : directories) {
                File[] subDirectories = new File(folder.getPath()).listFiles(File::isDirectory);
                for (File subFolder : subDirectories) {
                    File[] allFiles = new File(subFolder.getPath()).listFiles();
                    for (File individualFile : allFiles) {
                        individualFile.delete();
                    }
                    subFolder.delete();
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
            String weblink = links.get(folderNum);
            new File(crawlPathString + "/" + folderNum);
            boolean linkExists = false;
            try {
                String docString = WebRequester.readURL(weblink);
                // The "edit" variables are used in the following while loop to indicate whether the loop
                // should start/end adding to the title/text/link of the new key/paragraph/url
                boolean edit_text = false;
                String new_text = "";
                int index = 0;
                while (index < docString.length()) {
                    System.out.println(index);

                    // Determines if the current character is within any of the opening sections
                    if (!edit_text) {
                        if ("<title>".equals(docString.substring(index, index + 7))) {
                            edit_text = true;
                            index += 7;
                        } else if ("<p>".equals(docString.substring(index, index + 3))) {
                            edit_text = true;
                            index += 3;
                        } else if ("<a href=\"".equals(docString.substring(index, index + 9))) {
                            edit_text = true;
                            index += 9;
                        }
                    } else {

                        if ("</p>".equals(docString.substring(index, index + 4))) {

                        } else if ("\">".equals(docString.substring(index, index + 2))) {

                        } else if ("</title>".equals(docString.substring(index, index + 8))) {
                            // Adds the title and link of the website to a file
                            new File(crawlPathString + "/" + folderNum + "/title_and_link.txt").createNewFile();
//                        file.write(new_key + "\n" + weblink + "\n");
//                        file.close();
                            edit_text = false;
                            System.out.println("ye the index is: " + index);

                        }
                    }


                    index++;
                }
            } catch (MalformedURLException e) {
                // If the link is invalid then there is no point in proceeding
            } catch(IOException e) {
                // If the link is invalid then there is no point in continuing
            }

            folderNum++;
        }
    }
}
