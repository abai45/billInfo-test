import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Запускает автотесты по группе.
 */
@Service
public class AutotestRunner {

    private final TestCaseRepository repo;
    private final TestCaseExecutor executor;
    private final ResultLoggerService logger;

    public AutotestRunner(TestCaseRepository repo,
                          TestCaseExecutor executor,
                          ResultLoggerService logger) {
        this.repo = repo;
        this.executor = executor;
        this.logger = logger;
    }

    public void runGroup(String groupId) {
        List<TestCase> tests = repo.loadByGroup(groupId);
        for (TestCase test : tests) {
            List<AutotestResult> results = executor.execute(test);
            logger.logResults(test.getTestName(), results);
        }
    }
}