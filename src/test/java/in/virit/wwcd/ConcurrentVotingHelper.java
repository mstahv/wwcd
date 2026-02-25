package in.virit.wwcd;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Playwright-based concurrent voting test — no Spring dependencies.
 * <p>
 * Can be run standalone against an external server:
 * <pre>
 * mvn verify -DskipITs=false -Dit.test=ConcurrentVotingHelper -Dtest.url=http://myserver:8080 -Dviewer.count=50
 * </pre>
 * Also used as a delegate from {@link ConcurrentVotingIT} for embedded Spring Boot testing.
 */
@Tag("e2e")
public class ConcurrentVotingHelper {

    static final int VIEWER_COUNT = Integer.getInteger("viewer.count", 30);
    static final String PASSWORD = System.getProperty("test.password", "password");

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setTimeout(60000));
    }

    @AfterAll
    static void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Standalone test — only runs when {@code -Dtest.url} is set.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.url", matches = ".+")
    void testConcurrentVotingStandalone() throws Exception {
        runConcurrentVoting(System.getProperty("test.url"));
    }

    /**
     * Entry point for programmatic invocation from SpringBootTest.
     */
    static void runAgainst(String baseUrl) throws Exception {
        setUp();
        try {
            new ConcurrentVotingHelper().runConcurrentVoting(baseUrl);
        } finally {
            tearDown();
        }
    }

    void runConcurrentVoting(String baseUrl) throws Exception {
        System.out.println("Starting concurrent voting test with " + VIEWER_COUNT + " viewers against " + baseUrl);

        BrowserContext presenterContext = browser.newContext();
        Page presenterPage = presenterContext.newPage();
        Mopo presenterMopo = new Mopo(presenterPage);

        List<BrowserContext> viewerContexts = new ArrayList<>();

        try {
            // Step 1: Presenter enters presentation mode
            presenterPage.navigate(baseUrl);
            presenterPage.waitForLoadState(LoadState.NETWORKIDLE);

            presenterMopo.click("vaadin-button:has(vaadin-icon[icon='vaadin:play'])");
            presenterPage.keyboard().type(PASSWORD);
            presenterPage.keyboard().press("Enter");
            presenterMopo.waitForConnectionToSettle();

            // Step 2: Click Vote button to start voting phase
            presenterPage.locator("vaadin-button:has-text('Vote')").click();
            presenterPage.waitForTimeout(1000);

            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-voting-started.png")));

            // Step 3: Open all viewer pages as fast as possible.
            // Only wait for the server's initial response (COMMIT), not full page load.
            record Viewer(BrowserContext context, Page page, Mopo mopo) {}
            List<Viewer> viewers = new ArrayList<>();

            long connectStart = System.currentTimeMillis();
            for (int i = 0; i < VIEWER_COUNT; i++) {
                BrowserContext ctx = browser.newContext();
                viewerContexts.add(ctx);
                Page page = ctx.newPage();
                page.navigate(baseUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
                viewers.add(new Viewer(ctx, page, new Mopo(page)));
            }
            long connectDuration = System.currentTimeMillis() - connectStart;
            System.out.println(VIEWER_COUNT + " viewers connected in " + connectDuration + "ms");

            // Batch wait: just verify first and last viewer have voting buttons
            viewers.getFirst().page().locator("vaadin-grid vaadin-button").first().waitFor();
            viewers.getLast().page().locator("vaadin-grid vaadin-button").first().waitFor();
            System.out.println("All viewers settled on voting view");

            // Step 4: All viewers cast votes in round-robin fashion.
            // No artificial delays — the click round-trip provides natural spacing.
            int totalVotesCast = 0;
            Random random = new Random(42);
            int votesPerViewer = 10;
            int buttonCount = Tagline.values().length;

            long votingStart = System.currentTimeMillis();
            for (int round = 0; round < votesPerViewer && round < buttonCount; round++) {
                for (Viewer viewer : viewers) {
                    Locator buttons = viewer.page().locator("vaadin-grid vaadin-button");
                    int count = buttons.count();
                    if (count > 0) {
                        int idx = random.nextInt(count);
                        buttons.nth(idx).click();
                        totalVotesCast++;
                    }
                }
                System.out.println("Round " + (round + 1) + "/" + votesPerViewer + " complete, " + totalVotesCast + " votes cast so far");
            }
            long votingDuration = System.currentTimeMillis() - votingStart;

            // Brief settle time for last votes to reach server
            Thread.sleep(2000);

            System.out.println("Voting completed in " + votingDuration + "ms, " + totalVotesCast + " total votes from " + VIEWER_COUNT + " viewers");

            // Step 5: Presenter closes voting
            presenterMopo.waitForConnectionToSettle();
            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-presenter-before-close.png")));
            presenterPage.getByText("Close voting").click();
            presenterPage.waitForTimeout(2000);

            // Step 6: Verify the Agenda view is displayed
            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-presenter-agenda.png")));
            var agendaHeading = presenterPage.locator("h2:has-text('Agenda')");
            assertTrue(agendaHeading.isVisible(), "Agenda view should be displayed after closing voting");

            // Step 7: Presenter navigates to Q&A
            presenterMopo.click(presenterPage.locator("vaadin-button:has-text('Q&A')"));
            presenterMopo.waitForConnectionToSettle();
            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-presenter-qa.png")));
            System.out.println("Presenter moved to Q&A view");

            // Step 8: Presenter closes the presentation
            presenterMopo.click("vaadin-button:has-text('Close the presentation')");
            presenterMopo.waitForConnectionToSettle();
            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-presenter-closed.png")));
            System.out.println("Presentation closed");

        } finally {
            for (BrowserContext ctx : viewerContexts) {
                ctx.close();
            }
            presenterContext.close();
        }
    }
}
