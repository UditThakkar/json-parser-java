import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "D:\\Codes\\Java\\Build Your Own Series\\tests\\step3\\invalid.json";


        JsonParser parser = new JsonParser(filePath);

        String content = parser.getContent();

//        boolean value = parser.validateJson(content);
//
//        if(value) {
//            System.exit(0);
//        } else {
//            System.exit(1);
//        }

        parser.getParsedValue(content);
    }
}