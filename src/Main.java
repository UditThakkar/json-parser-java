import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String folderPath = "test";  // Folder containing multiple test files
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path: " + folderPath);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json")); // Get only JSON files

        if (files == null || files.length == 0) {
            System.out.println("No JSON files found in the directory.");
            return;
        }

        for (File file : files) {
            System.out.println("Processing file: " + file.getName());

            try {
                JsonParser parser = new JsonParser(file.getAbsolutePath());

                String content = parser.getContent();
                Object parsedContent = parser.parseJson(content);

                JsonParser.printJson(parsedContent, "");
            } catch (JsonParsingException e) {
                System.out.println("Error in file " + file.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Failed to read file " + file.getName() + ": " + e.getMessage());
            }

            System.out.println("--------------------------------");
        }
    }
}
