import java.io.*;

public class FileControl {
    public String crawlPathString = "crawlData";
    public String readFile(String path, String filename) throws IOException {
        StringBuilder data = new StringBuilder();
        try {
            FileReader file = new FileReader("./" + crawlPathString + "/" + path + filename);
            BufferedReader fileReader = new BufferedReader(file);
            String line = fileReader.readLine();
            while (line != null) {
                data.append(line).append("\n");
                line = fileReader.readLine();
            }
            file.close();
        } catch (FileNotFoundException e) {
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
        try (DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream("./" + crawlPathString + "/"
                + path + filename))) {

            // Write each character individually
            for (char letter : new_text.toCharArray()) {
                fileWriter.write(letter);
            }
        }
    }
}
