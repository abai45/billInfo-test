import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Выполняет insert, получает выписку, ищет операции по rowNumb и сравнивает их с эталонными.
 */
@Service
public class TestCaseExecutor {

    private final UniversalComparatorService comparator;
    private final BillInfoService billInfoService;
    private final ParamGeneratorService paramGenerator;

    public TestCaseExecutor(UniversalComparatorService comparator,
                            BillInfoService billInfoService,
                            ParamGeneratorService paramGenerator) {
        this.comparator = comparator;
        this.billInfoService = billInfoService;
        this.paramGenerator = paramGenerator;
    }

    public List<AutotestResult> execute(TestCase testCase) {
        Map<String, Object> params = paramGenerator.generateParams(testCase.getParameterKeys());
        String sql = paramGenerator.applyParams(testCase.getInsertSql(), params);
        paramGenerator.executeInsert(sql);

        String accNum = params.get("accNum").toString();
        String clientRboId = params.get("clientRboId").toString();
        String rowNumb = params.get("row_numb").toString();
        String now = params.get("now").toString();

        JsonNode fullBillInfo = billInfoService.fetchBillInfo(accNum, clientRboId, now, now);
        List<JsonNode> actualOps = billInfoService.extractOperationsByRowNumb(fullBillInfo, rowNumb);
        List<JsonNode> expectedOps = testCase.getEthalonJsonList();

        List<AutotestResult> results = new ArrayList<>();

        for (int i = 0; i < expectedOps.size(); i++) {
            JsonNode expected = expectedOps.get(i);
            JsonNode actual = (i < actualOps.size()) ? actualOps.get(i) : null;

            if (actual == null) {
                results.add(new AutotestResult(rowNumb, "ERROR", "Операция №" + (i + 1) + " не найдена в выписке"));
            } else {
                List<String> diffs = comparator.compare(expected, actual, testCase.getFieldsToCompare());
                String status = diffs.isEmpty() ? "PASS"
                        : allOnlyInExtraJson(diffs) ? "WARN" : "ERROR";

                results.add(new AutotestResult(rowNumb, status, String.join(" | ", diffs)));
            }
        }

        return results;
    }

    private boolean allOnlyInExtraJson(List<String> diffs) {
        return diffs.stream().allMatch(d ->
                d.contains("Unexpected field") || d.contains("Missing field"));
    }
}