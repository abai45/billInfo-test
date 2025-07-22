import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Генерация параметров и выполнение insert.
 */
@Service
public class ParamGeneratorService {
    private final JdbcTemplate jdbc;

    public ParamGeneratorService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> generateParams(List<String> keys) {
        Map<String, Object> map = new HashMap<>();
        for (String key : keys) {
            switch (key) {
                case "accNum" -> map.put("accNum", "KZ79722C000049079280");
                case "clientRboId" -> map.put("clientRboId", "1400915174342028");
                case "row_numb" -> map.put("row_numb", String.valueOf(System.currentTimeMillis() % 10000000));
                case "now" -> {
                    String now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")) + "+05:00";
                    map.put("now", now);
                }
                default -> map.put(key, key + "_VALUE");
            }
        }
        return map;
    }

    public String applyParams(String sql, Map<String, Object> map) {
        for (Map.Entry<String, Object> e : map.entrySet()) {
            sql = sql.replace("{" + e.getKey() + "}", e.getValue().toString());
        }
        return sql;
    }

    public void executeInsert(String sql) {
        jdbc.execute(sql);
    }
}