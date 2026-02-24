package in.virit.wwcd.views;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class LobbyView extends AbstractView {

    public LobbyView(AdminSession adminSession, AppContext appContext) {
        add(new Image("/photos/headline.svg", "What We(b) Can Do!"){{
            setMaxWidth("100%");
        }});
        if(adminSession.isAdmin()) {
            add(new HorizontalFloatLayout() {{
                add(md("""
                        Web as an application platform is a one big hack - but we still love it. Although designed for mostly
                        read-only content, the web platform has evolved to support complex applications. And the progress
                        has not stopped. New APIs and functionalities are being added to browsers all the time, and the capabilities of
                        web applications are constantly improving. In this demo/presentation we will look at some of the fresh
                        capabilities of the web platform, and how they can be utilized in modern web applications.
                        
                        Join to vote the agenda in:
                        
                        ## [w.virit.in](https://w.virit.in/)
                        
                        """));
                add(new Image("/photos/qrcode.png", "QR Code") {{
                    setMaxWidth("200px");
                }});
            }});
        } else {
            add("Voting starts sooon...");
        }

        if(adminSession.isAdmin()) {
            add(new DefaultButton("Vote", () -> {
                appContext.vote();
            }){{focus();}});
        }
    }

}
