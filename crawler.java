import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        new File("src/crawlData").mkdirs();
        // Do some crawling
    }
}
