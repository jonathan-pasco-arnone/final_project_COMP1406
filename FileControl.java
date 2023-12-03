import java.io.*;
import java.util.Hashtable;

public class FileControl {
    public String parsedPathString = "parsedData/";
    public String crawlPathString = "crawlData/";
    public Hashtable<String, Integer> parseHashTable(String path, String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path
                + filename))) {

            return (Hashtable<String, Integer>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
    public String readFile(String path, String filename) {
        StringBuilder data = new StringBuilder();
        try {
            FileReader file = new FileReader("./" + path + filename);
            BufferedReader fileReader = new BufferedReader(file);
            String line = fileReader.readLine();
            while (line != null) {
                data.append(line).append("\n");
                line = fileReader.readLine();
            }
            file.close();
        } catch (IOException e) {
            // Should never reach this case if inputted file is name and folder is correct
            return "File Not Found";
        }
        return data.toString();
    }
    public void writeFile(String new_text, String path, String filename) throws IOException {
        // First read the contents of the file
        String contents = readFile(path, filename);
        // If the file was found
        if (!contents.equals("File Not Found")) {
            new_text = contents + new_text;
        }
        // Write everything to the new file
        try (DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream("./" + path + filename))) {

            // Write each character individually
            for (char letter : new_text.toCharArray()) {
                fileWriter.write(letter);
            }
        }
    }
}
