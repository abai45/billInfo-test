// укороченная версия, полная будет добавлена во второй части
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UniversalComparatorService {
    public List<String> compare(JsonNode expectedJson, JsonNode actualJson, List<String> fieldsToCompare) {
        List<String> errors = new ArrayList<>();
        Set<String> compared = new HashSet<>(fieldsToCompare);

        for (String fieldPath : fieldsToCompare) {
            JsonNode expectedValue = getValueByPath(expectedJson, fieldPath);
            JsonNode actualValue = getValueByPath(actualJson, fieldPath);
            if (!Objects.equals(safeToString(expectedValue), safeToString(actualValue))) {
                errors.add("Mismatch at " + fieldPath + ": expected ["
                        + safeToString(expectedValue) + "] but got ["
                        + safeToString(actualValue) + "]");
            }
        }
        return errors;
    }

    private JsonNode getValueByPath(JsonNode json, String path) {
        String[] parts = path.split("\.");
        JsonNode current = json;
        for (String part : parts) {
            if (current == null) return null;
            current = current.get(part);
        }
        return current;
    }

    private String safeToString(JsonNode node) {
        return node == null || node.isNull() ? "null" : node.asText();
    }
}
