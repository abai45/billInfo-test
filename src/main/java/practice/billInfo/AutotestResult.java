/**
 * Результат сравнения одной операции.
 */
public class AutotestResult {
    private final String nid;
    private final String status;
    private final String message;

    public AutotestResult(String nid, String status, String message) {
        this.nid = nid;
        this.status = status;
        this.message = message;
    }

    public String getNid() { return nid; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
