import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Утилита для сравнения JSON-объектов по заданным полям, включая вложенные.
 * Поддерживает автоматический парсинг текстовых полей, содержащих JSON.
 */
@Service
public class UniversalComparatorService {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> compare(JsonNode expectedJson, JsonNode actualJson, List<String> fieldsToCompare) {
        List<String> errors = new ArrayList<>();
        Set<String> compared = new HashSet<>(fieldsToCompare);

        for (String fieldPath : fieldsToCompare) {
            JsonNode expectedValue = getValueByPath(expectedJson, fieldPath);
            JsonNode actualValue = getValueByPath(actualJson, fieldPath);

            if (!compareJsonValues(expectedValue, actualValue)) {
                errors.add("Mismatch at " + fieldPath + ": expected ["
                        + safeToString(expectedValue) + "] but got ["
                        + safeToString(actualValue) + "]");
            }
        }

        // Unexpected fields in actual
        for (String fieldPath : getAllJsonPaths(actualJson)) {
            if (!compared.contains(fieldPath) && getValueByPath(expectedJson, fieldPath) == null) {
                JsonNode actualExtra = getValueByPath(actualJson, fieldPath);
                if (actualExtra != null && !actualExtra.isNull() && !actualExtra.asText().trim().isEmpty()) {
                    errors.add("Unexpected field in actual JSON: " + fieldPath);
                }
            }
        }

        // Missing fields in actual
        for (String fieldPath : getAllJsonPaths(expectedJson)) {
            if (!compared.contains(fieldPath) && getValueByPath(actualJson, fieldPath) == null) {
                JsonNode expectedExtra = getValueByPath(expectedJson, fieldPath);
                if (expectedExtra != null && !expectedExtra.isNull() && !expectedExtra.asText().trim().isEmpty()) {
                    errors.add("Missing field in actual JSON (was in ethalon only): " + fieldPath);
                }
            }
        }

        return errors;
    }

    private boolean compareJsonValues(JsonNode a, JsonNode b) {
        return Objects.equals(safeToString(a), safeToString(b));
    }

    private String safeToString(JsonNode node) {
        return node == null || node.isNull() ? "null" : node.asText();
    }

    /**
     * Получает значение по пути вида "a.b.c", с поддержкой автоматического парсинга строк в JSON.
     */
    private JsonNode getValueByPath(JsonNode json, String path) {
        String[] parts = path.split("\\.");
        JsonNode current = json;

        for (String part : parts) {
            if (current == null || current.isNull()) return null;

            // Если строка — пробуем распарсить как JSON
            if (current.isTextual()) {
                try {
                    current = mapper.readTree(current.asText());
                } catch (Exception e) {
                    return null; // строка не является JSON
                }
            }

            current = current.get(part);
        }

        return current;
    }

    /**
     * Возвращает все пути к полям в JSON (включая вложенные).
     */
    private Set<String> getAllJsonPaths(JsonNode node) {
        Set<String> paths = new HashSet<>();
        collectPaths(node, "", paths);
        return paths;
    }

    private void collectPaths(JsonNode node, String prefix, Set<String> paths) {
        if (node == null) return;

        if (node.isObject()) {
            node.fieldNames().forEachRemaining(field -> {
                String newPrefix = prefix.isEmpty() ? field : prefix + "." + field;
                collectPaths(node.get(field), newPrefix, paths);
            });
        } else {
            paths.add(prefix);
        }
    }
}
