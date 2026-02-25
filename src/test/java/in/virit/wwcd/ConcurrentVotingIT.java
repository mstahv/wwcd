package in.virit.wwcd;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class ConcurrentVotingIT {

    @LocalServerPort
    private int port;

    private static final int VIEWER_COUNT = Integer.getInteger("viewer.count", 30);

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

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testConcurrentVoting() throws Exception {
        System.out.println("Starting concurrent voting test with " + VIEWER_COUNT + " viewers");

        BrowserContext presenterContext = browser.newContext();
        Page presenterPage = presenterContext.newPage();
        Mopo presenterMopo = new Mopo(presenterPage);

        // Collect viewer contexts for cleanup
        List<BrowserContext> viewerContexts = new ArrayList<>();

        try {
            // Step 1: Presenter enters presentation mode
            presenterPage.navigate(baseUrl());
            presenterPage.waitForLoadState(LoadState.NETWORKIDLE);

            presenterMopo.click("vaadin-button:has(vaadin-icon[icon='vaadin:play'])");
            presenterPage.keyboard().type("password");
            presenterPage.keyboard().press("Enter");
            presenterMopo.waitForConnectionToSettle();

            // Step 2: Click Vote button to start voting phase
            presenterPage.locator("vaadin-button:has-text('Vote')").click();
            presenterPage.waitForTimeout(1000);

            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-voting-started.png")));

            // Step 3: Open all viewer pages (sequentially — Playwright is single-threaded)
            // The server still handles each session on separate Tomcat threads,
            // so server-side concurrency is fully exercised.
            record Viewer(BrowserContext context, Page page, Mopo mopo) {}
            List<Viewer> viewers = new ArrayList<>();

            for (int i = 0; i < VIEWER_COUNT; i++) {
                BrowserContext ctx = browser.newContext();
                viewerContexts.add(ctx);
                Page page = ctx.newPage();
                Mopo mopo = new Mopo(page);
                page.navigate(baseUrl());
                page.waitForLoadState();
                viewers.add(new Viewer(ctx, page, mopo));
                System.out.println("Viewer " + i + " connected");
            }

            // Single batch wait for all viewers to settle on the voting view
            Thread.sleep(3000);
            // Verify first and last viewer have voting buttons available
            viewers.getFirst().page().locator("vaadin-grid vaadin-button").first().waitFor();
            viewers.getLast().page().locator("vaadin-grid vaadin-button").first().waitFor();
            System.out.println("All viewers settled on voting view");

            // Step 4: All viewers cast votes in round-robin fashion.
            // Each viewer gets 10 votes. We cycle through viewers casting
            // one vote each per round, creating interleaved load.
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

        } finally {
            for (BrowserContext ctx : viewerContexts) {
                ctx.close();
            }
            presenterContext.close();
        }
    }
}
