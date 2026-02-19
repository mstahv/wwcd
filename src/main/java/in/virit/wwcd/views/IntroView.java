package in.virit.wwcd.views;

import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.DefaultButton;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class IntroView extends AbstractView {

    public IntroView(AdminSession adminSession, AppContext appContext) {
        add(md("""
                # What We(b) can do in 2026!?
                
                Web as an application platform is a one big hack - but we still love it. Although designed for mostly
                read-only content, the web platform has evolved to support complex applications. And the progress
                has not stopped. New APIs and functionalities are being added to browsers all the time, and the capabilities of
                web applications are constantly improving. In this demo/presentation we will look at some of the fresh
                capabilities of the web platform, and how they can be utilized in modern web applications.
                
                **App is in "presentation mode". Voting starts soon. All demos will (re-)open after the session.**
                
                You  can prepare for voting the agenda by opening this page via [w.virit.in](https://w.virit.in/)
                
                ![QR Code](/photos/qrcode.png)
                
                """));

        if(adminSession.isAdmin()) {
            add(new DefaultButton("Vote", () -> {
                appContext.vote();
            }){{focus();}});
        }
    }

}
