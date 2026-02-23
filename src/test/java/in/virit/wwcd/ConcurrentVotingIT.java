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
import java.util.Random;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class ConcurrentVotingIT {

    @LocalServerPort
    private int port;

    private static final Random random = new Random();

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setTimeout(30000));
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
        BrowserContext presenterContext = browser.newContext();
        Page presenterPage = presenterContext.newPage();
        Mopo presenterMopo = new Mopo(presenterPage);

        try {
            // Step 1: Presenter opens the application
            presenterPage.navigate(baseUrl());
            presenterPage.waitForLoadState(LoadState.NETWORKIDLE);

            // Step 2: Click the present button (with play icon)
            presenterMopo.click("vaadin-button:has(vaadin-icon[icon='vaadin:play'])");

            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-after-play-click.png")));

            // Step 3: Type password into the dialog and submit
            presenterPage.keyboard().type("password");
            presenterPage.keyboard().press("Enter");
            presenterMopo.waitForConnectionToSettle();

            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-after-password.png")));

            // Step 4: Click Begin button
            presenterPage.locator("//vaadin-button[contains(text(),'Begin')]").click();
            presenterPage.waitForTimeout(1000);

            // Step 5: Click Vote button
            presenterPage.getByText("Vote").click();
            presenterPage.waitForTimeout(1000);

            // Step 6: One viewer votes
            BrowserContext viewerContext = browser.newContext();
            Page viewerPage = viewerContext.newPage();
            try {
                simulateViewer(viewerContext, viewerPage, 0);
            } finally {
                viewerContext.close();
            }

            // Step 7: Presenter clicks "Close Voting" button
            presenterMopo.waitForConnectionToSettle();
            presenterPage.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("./target/screenshot-presenter-before-close.png")));
            presenterPage.getByText("Close voting").click();
            presenterPage.waitForTimeout(2000);

            // Step 8: Verify the view changes to Agenda view
            var agendaHeading = presenterPage.locator("h2:has-text('Agenda')");
            assertTrue(agendaHeading.isVisible(), "Agenda view should be displayed after closing voting");

        } finally {
            presenterContext.close();
        }
    }

    private void simulateViewer(BrowserContext viewerContext, Page viewerPage, int viewerId) {
        Mopo mopo = new Mopo(viewerPage);

        viewerPage.navigate(baseUrl());
        viewerPage.waitForLoadState();
        viewerPage.waitForTimeout(3000);
        mopo.waitForConnectionToSettle();

        viewerPage.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("./target/screenshot-viewer" + viewerId + ".png")));

        assertThat(viewerPage.getByText("Voting")).hasCount(2);

        int buttonCount = Tagline.values().length;
        if (buttonCount == 0) {
            return;
        }

        // Cast 10 votes
        for (int vote = 0; vote < 10 && vote < buttonCount; vote++) {
            viewerPage.waitForTimeout(random.nextInt(300) + 100);

            Locator buttons = viewerPage.locator("vaadin-grid vaadin-button");
            int count = buttons.count();
            if (count == 0) {
                viewerPage.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get("./target/screenshot-viewer" + viewerId + "-no-buttons.png")));
                return;
            }
            int idx = random.nextInt(count);
            mopo.click(buttons.nth(idx));
        }

        Mopo.waitForConnectionToSettle(viewerPage);
        viewerPage.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("./target/screenshot-viewer" + viewerId + "-end.png")));
    }
}
