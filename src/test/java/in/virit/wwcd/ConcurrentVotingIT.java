package in.virit.wwcd;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Runs the concurrent voting test against an embedded Spring Boot server.
 * Delegates all Playwright logic to {@link ConcurrentVotingHelper}.
 * <p>
 * To run against an external server instead (no Spring Boot), run
 * {@link ConcurrentVotingHelper} directly with {@code -Dtest.url=http://host:port}.
 */
@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class ConcurrentVotingIT {

    @LocalServerPort
    private int port;

    @Test
    void testConcurrentVoting() throws Exception {
        ConcurrentVotingHelper.runAgainst("http://localhost:" + port);
    }
}
