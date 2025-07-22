import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Отправляет JSON-запрос на http://scs-app2-lt3:8081/getBillInfo и возвращает операции по rowNumb.
 */
@Service
public class BillInfoService {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode fetchBillInfo(String accNum, String clientRboId, String beginDate, String endDate) {
        try {
            BillInfoRequest request = new BillInfoRequest(accNum, clientRboId, beginDate, endDate);
            String responseBody = createRequestPostman("http://scs-app2-lt3:8081/getBillInfo", request).body().string();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("data").path("OPER_ARR").get("OPER");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка получения выписки: " + e.getMessage(), e);
        }
    }

    public List<JsonNode> extractOperationsByRowNumb(JsonNode operations, String rowNumb) {
        List<JsonNode> result = new ArrayList<>();
        if (operations != null && operations.isArray()) {
            for (JsonNode op : operations) {
                if (op.has("rowNumb") && rowNumb.equals(op.get("rowNumb").asText())) {
                    result.add(op);
                }
            }
        }
        return result;
    }

    // Stub method
    private okhttp3.Response createRequestPostman(String url, Object body) throws IOException {
        throw new UnsupportedOperationException("HTTP method not implemented");
    }

    public static class BillInfoRequest {
        public String accNum;
        public String dogId;
        public String beginDate;
        public String endDate;
        public boolean bonus = false;
        public String callSystem = "IO";
        public String lang = "ru-RU";
        public String printForm = "BILL_INFO";
        public boolean debug = true;

        public BillInfoRequest(String accNum, String dogId, String beginDate, String endDate) {
            this.accNum = accNum;
            this.dogId = dogId;
            this.beginDate = beginDate;
            this.endDate = endDate;
        }
    }
}