package in.virit.wwcd.session;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@VaadinSessionScope
public class ShortcutTestSession {

    public enum TestResult {
        PASSED, FAILED
    }

    private final Map<String, TestResult> results = new HashMap<>();

    public void markPassed(String keyDisplay) {
        results.put(keyDisplay, TestResult.PASSED);
    }

    public void markFailed(String keyDisplay) {
        results.put(keyDisplay, TestResult.FAILED);
    }

    public void clear(String keyDisplay) {
        results.remove(keyDisplay);
    }

    public TestResult getResult(String keyDisplay) {
        return results.get(keyDisplay);
    }

    public Map<String, TestResult> getResults() {
        return results;
    }
}
