import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Object parseJson(String json) {
        json = json.trim();
        if (json.startsWith("{")) {
            return getParsedValue(json);
        } else if (json.startsWith("[")) {
            return parseArray(json);
        } else {
            throw new IllegalArgumentException("Invalid JSON value: " + json);
        }
    }

    public String getParsedValue(String content) {
        HashMap<String, String> hashMap = new HashMap<>();
        content = content.substring(1, content.length() - 1);
        if(content.length() == 0) {
            return "{}";
        }
        if(content.charAt(content.length() - 1) == ',') {
            System.out.println("Json is not of a valid format, extra , at the end");
            System.exit(1);
        }
        String[] values = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for(String value : values) {
            String[] keyValue = value.split(":", 2);
            if (keyValue.length < 2) {
                System.out.println("Invalid key-value pair: " + value);
                System.exit(1);
            }
            String key = keyValue[0].trim();
            String val = keyValue[1].trim();
            validateKey(key);
            if (val.startsWith("{")) {
                if (val.charAt(val.length() - 1) != '}') {
                    throw new JsonParsingException("Invalid object format: Missing closing }");
                }
                hashMap.put(key, getParsedValue(val));
            } else {
                validateValue(val);
                hashMap.put(key, val);
            }
        }
        return hashMap.toString();
    }

    private List<Object> parseArray(String json) {
        List<Object> list = new ArrayList<>();
        json = json.substring(1, json.length() - 1).trim();

        if(json.isEmpty()) {
            return list;
        }

        String[] values = json.split(",(?![^\\[]*\\])");
        for (String value : values) {
            value = value.trim();
            if (value.startsWith("{")) {
                list.add(getParsedValue(value));
            } else if (value.startsWith("[")) {
                list.add(parseArray(value));
            } else {
                validateValue(value);
                list.add(value);
            }
        }
        return list;
    }

    private void validateKey(String key) {
        if(key.charAt(0) != '"' || key.charAt(key.length()-1) != '"') {
            throw new JsonParsingException("Invalid key: " + key);
        }
    }

    private void validateValue(String value) {
        List<String> valid = Arrays.asList("false", "true", "null");
        if(value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
            char val1 = value.charAt(0);
            char val2 = value.charAt(value.length()-1);
            return;
        }else if(value.charAt(0) == '"' && value.charAt(value.length()-1) != '"') {
            throw new JsonParsingException("Invalid value " + value);
        }else if(value.charAt(0) == '\'') {
            if(value.length() == 1) {
                if(value.charAt(value.length() - 1) == '\'') {
                    return;
                } else {
                    throw new JsonParsingException("Invalid value, value is char but not closing with a '");
                }
            } else {
                throw new JsonParsingException("Invalid value, String should be double quotes. Failed for: " + value);
            }
        } else {
            if(value.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?")) {
                return;
            }
            if(value.charAt(0) == '{') {
                if(value.charAt(value.length()-1) != '}') {
                    throw new JsonParsingException("Invalid value no closing } for object type");
                } else {
//                    String val = value.substring(1, value.length()-1).trim();
                    parseJson(value);
                    return;
                }
            }
            if(value.charAt(0) == '['){
                if(value.charAt(value.length()-1) != ']') {
                    throw new JsonParsingException("Invalid value no closing ] for array type");
                } else {
                    String[] values = value.substring(1, value.length()-1).split(",");
                    for(String val : values) {
                        if(val.length() == 0) {
                            continue;
                        } else if(val.charAt(0) == '{' || val.charAt(0) == '[') {
                            getParsedValue(val);
                        } else if(val.length() == 0) {
                            continue;
                        } else {
                            validateValue(val);
                        }
                    }
                    return;
                }
            }
            if(!valid.contains(value)) {
                throw new JsonParsingException("Invalid value from the array");
            }
        }
    }

    public static void printJson(Object json, String indent) {
        if (json instanceof Map<?, ?> map) {
            System.out.println(indent + "{");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.print(indent + "  \"" + entry.getKey() + "\": ");
                printJson(entry.getValue(), indent + "  ");
            }
            System.out.println(indent + "}");
        } else if (json instanceof List<?> list) {
            System.out.println(indent + "[");
            for (Object item : list) {
                printJson(item, indent + "  ");
            }
            System.out.println(indent + "]");
        } else {
            System.out.println(indent + json);
        }
    }
}
