package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "WebAuthn / Passkeys", icon = VaadinIcon.PASSWORD)
@Route(layout = MainLayout.class)
public class WebAuthnView extends AbstractThing {

    public WebAuthnView() {
        add(md("""
                Biometric authentication, well, technically not really, but practically yes, via Passkeys and WebAuthn.

                Bold statement: **Only a fool would build username-password logins in 2026**.

                * Passkeys provide better UX
                * Passkeys are way more secure (especially among non-tech-savvy users)
                * It is harder for you (as web developer) to screw up the security
                * WebAuthn provides API for web apps to the passkeys (typically via your OS)
                * Your OS typically safeguards passkeys with fingerprint or facial recognition (among other
                  possibilities)

                [Try a separate example](https://webauthn.dokku1.parttio.org/login).

                [Three methods to go passwordless](https://vaadin.com/blog/three-methods-to-go-passwordless)
                """));
    }
}