import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Класс модели одного тест-кейса, загружаемого из БД.
 */
public class TestCase {
    private String groupId;
    private String testName;
    private String insertSql;
    private List<String> parameterKeys;
    private List<JsonNode> ethalonJsonList;
    private List<String> fieldsToCompare;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public String getInsertSql() { return insertSql; }
    public void setInsertSql(String insertSql) { this.insertSql = insertSql; }
    public List<String> getParameterKeys() { return parameterKeys; }
    public void setParameterKeys(List<String> parameterKeys) { this.parameterKeys = parameterKeys; }
    public List<JsonNode> getEthalonJsonList() { return ethalonJsonList; }
    public void setEthalonJsonList(List<JsonNode> ethalonJsonList) { this.ethalonJsonList = ethalonJsonList; }
    public List<String> getFieldsToCompare() { return fieldsToCompare; }
    public void setFieldsToCompare(List<String> fieldsToCompare) { this.fieldsToCompare = fieldsToCompare; }
}
