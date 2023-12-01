import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class FileControl {
    public String crawlPathString = "crawlData";
    public String readFile(int folderNum, String filename) throws IOException {
        StringBuilder data = new StringBuilder();
        try {
            DataInputStream fileReader = new DataInputStream(new FileInputStream("./" + crawlPathString + "/" + folderNum + filename));
            String line;
            while ((line = fileReader.readLine()) != null) {
                data.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            // Should never reach this case
            return "File Not Found";
        }
        return data.toString();
    }
    public void writeFile(String new_text, int folderNum, String filename) throws IOException {
        // First read the contents of the file
        String contents = readFile(folderNum, filename);
        // If the file was found
        if (!contents.equals("File Not Found")) {
            new_text = contents + new_text;
        }
        // Write everything to the new file
        try (DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream("./" + crawlPathString + "/"
                + folderNum + filename))) {

            // Write each character individually
            for (char letter : new_text.toCharArray()) {
                fileWriter.write(letter);
            }
        }
    }
}
