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

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class SpectatorModeIT {

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
    void spectatorModeShowsDemosViewInsteadOfActualDemo() {
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

            // --- Enter presentation mode (spectator checkbox is checked by default) ---
            presenterMopo.click("vaadin-button:has(vaadin-icon[icon='vaadin:play'])");
            presenterPage.keyboard().type("password");
            presenterPage.keyboard().press("Enter");
            presenterMopo.waitForConnectionToSettle();

            // Both should be on LobbyView with intro content
            assertThat(presenterPage.getByAltText("What We(b) Can Do!")).isVisible();
            assertThat(presenterPage.locator("vaadin-button:has-text('Vote')")).isVisible();
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByAltText("What We(b) Can Do!")).isVisible();
            // Viewer must NOT see the admin-only Vote button
            assertThat(viewerPage.locator("vaadin-button:has-text('Vote')")).not().isVisible();

            // --- Vote → Voting phase ---
            presenterMopo.click("vaadin-button:has-text('Vote')");
            presenterMopo.waitForConnectionToSettle();

            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Let's plan the session")).isVisible();

            // Viewer casts some votes
            Locator voteButtons = viewerPage.locator("vaadin-grid vaadin-button");
            voteButtons.first().waitFor();
            for (int i = 0; i < 3; i++) {
                viewerMopo.click(voteButtons.nth(i));
            }

            // --- Close voting → AgendaView ---
            presenterMopo.click(presenterPage.getByText("Close voting"));
            presenterMopo.waitForConnectionToSettle();
            assertThat(presenterPage.locator("vaadin-grid")).isVisible();

            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.locator("vaadin-grid")).isVisible();

            // --- Presenter clicks a demo → viewer should see DemosView, NOT the actual demo ---
            Locator demoButtons = presenterPage.locator("vaadin-grid vaadin-button");
            demoButtons.first().waitFor();
            String firstDemoName = demoButtons.first().textContent().trim();
            presenterMopo.click(demoButtons.first());
            presenterMopo.waitForConnectionToSettle();

            // Viewer should see the spectator DemosView
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Sit back and enjoy the show")).isVisible();
            assertThat(viewerPage.getByText(firstDemoName)).isVisible();

            // --- Presenter navigates to Q&A ---
            presenterMopo.click(presenterPage.locator("vaadin-button:has-text('Q&A')"));
            presenterMopo.waitForConnectionToSettle();
            assertThat(presenterPage.getByText("Close the presentation")).isVisible();

            // Viewer should see Q&A content but NOT the admin-only Close button
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Questions, comments, ideas?")).isVisible();
            assertThat(viewerPage.locator("vaadin-button:has-text('Close the presentation')")).not().isVisible();

            // --- Close the presentation ---
            presenterMopo.click("vaadin-button:has-text('Close the presentation')");
            presenterMopo.waitForConnectionToSettle();

            // Both should return to MainView
            assertThat(presenterPage.getByText("Ready to be amazed?")).isVisible();
            waitForViewer(viewerPage, viewerMopo);
            assertThat(viewerPage.getByText("Ready to be amazed?")).isVisible();

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
