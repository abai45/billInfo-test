import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий, загружающий тест-кейсы из таблицы autotest_cases.
 */
@Repository
public class TestCaseRepository {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public TestCaseRepository(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public List<TestCase> loadByGroup(String groupId) {
        return jdbc.query("SELECT * FROM autotest_cases WHERE group_id = ?",
            new Object[]{groupId},
            (rs, rowNum) -> {
                TestCase test = new TestCase();
                test.setGroupId(rs.getString("group_id"));
                test.setTestName(rs.getString("test_name"));
                test.setInsertSql(rs.getString("insert_sql"));
                test.setParameterKeys(mapper.readValue(rs.getString("parameters"), List.class));
                test.setFieldsToCompare(mapper.readValue(rs.getString("for_compare"), List.class));
                test.setEthalonJsonList(mapper.readValue(rs.getString("ethalon_json"), List.class));
                return test;
            });
    }
}
