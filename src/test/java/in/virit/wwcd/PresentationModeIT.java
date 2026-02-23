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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PresentationModeIT {

    @LocalServerPort
    private int port;

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
    @Order(2)
    void viewerIsMovedToLobbyWhenPresentationStarts() {
        BrowserContext presenterContext = browser.newContext();
        Page presenterPage = presenterContext.newPage();
        Mopo presenterMopo = new Mopo(presenterPage);

        BrowserContext viewerContext = browser.newContext();
        Page viewerPage = viewerContext.newPage();
        Mopo viewerMopo = new Mopo(viewerPage);

        try {
            // Both users open the app and land on the About view
            presenterPage.navigate(baseUrl());
            presenterPage.waitForLoadState(LoadState.NETWORKIDLE);
            viewerPage.navigate(baseUrl());
            viewerPage.waitForLoadState(LoadState.NETWORKIDLE);
            viewerMopo.waitForConnectionToSettle();

            assertThat(viewerPage.getByText("Ready to be amazed?")).isVisible();

            // Presenter clicks the play button and enters password
            presenterMopo.click("vaadin-button:has(vaadin-icon[icon='vaadin:play'])");
            presenterPage.keyboard().type("password");
            presenterPage.keyboard().press("Enter");
            presenterMopo.waitForConnectionToSettle();

            // Presenter should be on LobbyView with Begin button
            assertThat(presenterPage.locator("vaadin-button:has-text('Begin')")).isVisible();

            // Viewer should be automatically moved to LobbyView via server push/poll
            viewerPage.waitForTimeout(2000);
            viewerMopo.waitForConnectionToSettle();
            assertThat(viewerPage.getByText("Wait for the presenter to begin")).isVisible();

        } finally {
            viewerContext.close();
            presenterContext.close();
        }
    }

    @Test
    @Order(1)
    void fullPresentationWalkthrough() {
        BrowserContext presenterContext = browser.newContext();
        Page presenterPage = presenterContext.newPage();
        Mopo presenterMopo = new Mopo(presenterPage);

        BrowserContext viewerContext = browser.newContext();
        Page viewerPage = viewerContext.newPage();
        Mopo viewerMopo = new Mopo(viewerPage);

        try {
            // Both users open the app
            presenterPage.navigate(baseUrl());
            presenterPage.waitForLoadState(LoadState.NETWORKIDLE);
            viewerPage.navigate(baseUrl());
            viewerPage.waitForLoadState(LoadState.NETWORKIDLE);
            viewerMopo.waitForConnectionToSettle();

            // --- Enter presentation mode ---
            presenterMopo.click("vaadin-button:has(vaadin-icon[icon='vaadin:play'])");
            presenterPage.keyboard().type("password");
            presenterPage.keyboard().press("Enter");
            presenterMopo.waitForConnectionToSettle();

            // Both should be on LobbyView
            assertThat(presenterPage.locator("vaadin-button:has-text('Begin')")).isVisible();
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Wait for the presenter to begin")).isVisible();

            // --- Begin → IntroView ---
            presenterMopo.click("vaadin-button:has-text('Begin')");
            assertThat(presenterPage.getByText("What We(b) can do in 2026")).isVisible();

            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("What We(b) can do in 2026")).isVisible();

            // --- Vote → Voting phase ---
            presenterMopo.click("vaadin-button:has-text('Vote')");
            presenterMopo.waitForConnectionToSettle();

            // Viewer should land on VotingView
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Let's plan the session")).isVisible();

            // Viewer casts a few votes
            Locator voteButtons = viewerPage.locator("vaadin-grid vaadin-button");
            voteButtons.first().waitFor();
            for (int i = 0; i < 3; i++) {
                viewerMopo.click(voteButtons.nth(i));
            }

            // Presenter should be on VotingLeaderboardView with Close voting button
            assertThat(presenterPage.getByText("Close voting")).isVisible();

            // --- Close voting → AgendaView ---
            presenterMopo.click(presenterPage.getByText("Close voting"));
            presenterMopo.waitForConnectionToSettle();
            assertThat(presenterPage.locator("vaadin-grid")).isVisible();

            waitForViewer(viewerPage, viewerMopo);
            // Viewer should also see the agenda grid
            assertThat(viewerPage.locator("vaadin-grid")).isVisible();

            // --- Show a demo from the agenda ---
            // Presenter clicks the first demo name in the agenda grid
            Locator demoButtons = presenterPage.locator("vaadin-grid vaadin-button");
            demoButtons.first().waitFor();
            String firstDemoName = demoButtons.first().textContent().trim();
            presenterMopo.click(demoButtons.first());
            presenterMopo.waitForConnectionToSettle();

            // Viewer should be moved to the same demo view
            waitForViewer(viewerPage, viewerMopo);
            // The demo view shows a GitHub source link in the navbar (added by AbstractThing)
            assertThat(viewerPage.locator("a[href*='github.com']")).isVisible();

            // --- Navigate back to agenda (forward button goes to agenda from a demo) ---
            Locator forwardButton = presenterPage.locator("vaadin-button:has(vaadin-icon[icon='vaadin:fast-forward'])");
            forwardButton.click();
            presenterMopo.waitForConnectionToSettle();
            // Presenter should be back on agenda
            assertThat(presenterPage.locator("vaadin-grid")).isVisible();

            // Show a second demo via pickNext (forward button on agenda)
            forwardButton.click();
            presenterMopo.waitForConnectionToSettle();

            // Viewer should be moved to the second demo
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.locator("a[href*='github.com']")).isVisible();

            // --- Q&A phase ---
            // Click 5. Q&A in the presentation stages drawer
            presenterMopo.click(presenterPage.locator("vaadin-button:has-text('Q&A')"));
            presenterMopo.waitForConnectionToSettle();
            assertThat(presenterPage.getByText("Questions, comments, ideas?")).isVisible();

            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Questions, comments, ideas?")).isVisible();

            // --- Close the presentation ---
            presenterMopo.click("vaadin-button:has-text('Close the presentation')");
            presenterMopo.waitForConnectionToSettle();

            // Presenter should be back on MainView (About)
            assertThat(presenterPage.getByText("Ready to be amazed?")).isVisible();

            // Viewer should also be back on MainView
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Ready to be amazed?")).isVisible();

            // --- Verify demos are now accessible ---
            // Navigate viewer directly to a demo view; should not be redirected
            viewerPage.navigate(baseUrl() + "/clipboardapi");
            viewerMopo.waitForConnectionToSettle();

            // The demo view shows a GitHub source link (from AbstractThing)
            assertThat(viewerPage.locator("a[href*='github.com']")).isVisible();

        } finally {
            viewerContext.close();
            presenterContext.close();
        }
    }

    private void waitForViewer(Page viewerPage, Mopo viewerMopo) {
        viewerPage.waitForTimeout(2000);
        viewerMopo.waitForConnectionToSettle();
    }
}
