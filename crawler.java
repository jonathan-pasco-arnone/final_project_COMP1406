import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

public class crawler {
    public String crawlPathString = "crawlData";
    public Path crawlPath = Paths.get(crawlPathString);

    private void writeFile(String new_text, int folderNum, String filename) throws IOException {
        // Checks if the file already exists. If so, then copy the current contents and add it to the new_text
        if (Files.exists(Path.of("./" + crawlPathString + "/" + folderNum + filename))) {
            File oldFile = new File("./" + crawlPathString + "/" + folderNum + filename);

            // Scanner objects are capable of reading files
            Scanner fileReader = new Scanner(oldFile);
            StringBuilder data = new StringBuilder();
            while (fileReader.hasNextLine()) {
                data.append(fileReader.nextLine()).append("\n");
            }
            fileReader.close();
            new_text = data + new_text;
            oldFile.delete();
        }
        // The try-with-resources below attempts to open the file and will close it when done (or it will throw an
        // exception if one occurs)
        try (FileWriter file = new FileWriter("./" + crawlPathString + "/" + folderNum + filename)) {
            file.write(new_text);
        }
    }
    public void initialize() {
        // Deletes all the folders and files
        if (Files.exists(crawlPath)) {
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
//                    System.out.println(index + "The character: " + docString.substring(index, index + 1));
//                    System.out.println("Current text: " + new_text);

                    // Determines if the current character is within any of the opening sections
                    if (!edit_text) {
                        if ("<title>".equals(docString.substring(index, index + 7))) {
                            edit_text = true;
                            index += 6;
                        } else if ("<p>".equals(docString.substring(index, index + 3))) {
                            edit_text = true;
                            index += 2;
                        } else if ("<a href=\"".equals(docString.substring(index, index + 9))) {
                            edit_text = true;
                            index += 8;
                        }
                    } else {

                        if ("</p>".equals(docString.substring(index, index + 4))) {
                            writeFile(new_text.toString(), folderNum, "/page_text.txt");
                            edit_text = false;
                            new_text = new StringBuilder();
                        } else if ("\">".equals(docString.substring(index, index + 2))) {

                            writeFile(new_text.toString(), folderNum, "/outgoing_links.txt");
                            edit_text = false;
                            new_text = new StringBuilder();
                        } else if ("</title>".equals(docString.substring(index, index + 8))) {
                            // Adds the title and link of the website to a file
                            new_text.append("\n").append(weblink);
                            writeFile(new_text.toString(), folderNum, "/title_and_link.txt");
                            edit_text = false;
                            new_text = new StringBuilder();

                        } else {
                            new_text.append(docString.substring(index, index + 1));
                        }
                    }


                    index++;
                }
            } catch (MalformedURLException e) {
                // If the link is invalid then there is no point in proceeding
            } catch(IOException e) {
                // If the link is invalid then there is no point in continuing
            } catch (StringIndexOutOfBoundsException e) {
                // Will happen at the end of every file reading because the program will attempt to read past the last
                // Characters
            }

            folderNum++;
        }
    }
}
