import java.io.File;
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
        while (folderNum <= links.size()) {
            String weblink = links.get(0);
            new File(crawlPathString + "/" + folderNum);
            //doc_string =


            folderNum++;
        }
    }
}
