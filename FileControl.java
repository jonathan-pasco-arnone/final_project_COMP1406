import java.io.*;

public abstract class FileControl {
    public static final String PARSEDPATHSTRING = "parsedData/";
    public static final String CRAWLPATHSTRING = "crawlData/";
    public void serialize(String path, String filename, Object object) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path + filename);
        ObjectOutputStream outputtingLinkLocations = new ObjectOutputStream(fileOutputStream);
        outputtingLinkLocations.writeObject(object);
        outputtingLinkLocations.close();
    }
    public Object deserialize(String path, String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path + filename))) {
            return inputStream.readObject();
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
                // This is to make sure no empty lines get put in
                if (!line.isEmpty()) {
                    data.append(line).append("\n");
                }
                line = fileReader.readLine();
            }
            file.close();
        } catch (IOException e) {
            // Should never reach this case if inputted file is name and folder is correct
            return null;
        }
        return data.toString();
    }
    public void writeFile(String newText, String path, String filename) throws IOException {
        // First read the contents of the file
        String contents = readFile(path, filename);
        // If the file was found
        if (contents != null) {
            newText = contents + newText;
        }
        // Write everything to the new file
        try (DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream("./" + path + filename))) {

            // Write each character individually
            for (char letter : newText.toCharArray()) {
                fileWriter.write(letter);
            }
        }
    }
}
