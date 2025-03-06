import java.io.*;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private String filePath;


    public JsonParser(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    public boolean validateJson(String content) throws FileNotFoundException {
        if(content.isEmpty()) return false;

        if(content.length() == 2) {
            if(content.charAt(0) == '{' && content.charAt(1) == '}') {
                return true;
            }
        }
        return false;
    }

    public void getParsedValue(String content) {
        HashMap<String, String> hashMap = new HashMap<>();
        content = content.substring(1, content.length() - 1);
        if(content.charAt(content.length() - 1) == ',') {
            System.out.println("Json is not of a valid format");
            System.exit(1);
        }
        String[] values = content.split(",");
        for(String value : values) {
            String[] keyValue = value.split(":", 2);
            String key = keyValue[0].trim();
            String val = keyValue[1].trim();
            validateKey(key);
            validateValue(value);
            hashMap.put(key, val);
        }
        System.out.println(hashMap.toString());
    }
    
    private void validateKey(String key) {
        if(key.charAt(0) != '"' || key.charAt(key.length()-1) != '"') {
            System.out.println("Invalid key");
            System.exit(1);
        }
    }
    private void validateValue(String value) {
        List<String> valid = Arrays.asList("false", "true", "null");
        if(value.charAt(0) == '"' && value.charAt(value.length()-1) != '"') {
            System.out.println("Invalid value");
            System.exit(1);
        } else {
            if(value.matches("-?\\d+")) {
                return;
            }
            if(!valid.contains(value)) {
                System.out.println("Invalid value frm the array");
                System.exit(1);
            }
        }
    }
}
