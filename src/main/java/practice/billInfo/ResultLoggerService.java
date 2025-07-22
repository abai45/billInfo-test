import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Логирует результаты тестов в базу.
 */
@Service
public class ResultLoggerService {
    private final JdbcTemplate jdbc;

    public ResultLoggerService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void logResults(String testName, List<AutotestResult> results) {
        for (AutotestResult result : results) {
            jdbc.update("INSERT INTO autotest_logs (test_name, row_numb, status, message, log_time) VALUES (?, ?, ?, ?, ?)",
                    testName,
                    result.getNid(),
                    result.getStatus(),
                    result.getMessage(),
                    LocalDateTime.now());
        }
    }
}