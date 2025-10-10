package in.virit.wwcd.views;

import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class LobbyView extends AbstractView {

    public LobbyView(AdminSession adminSession, AppContext appContext) {
        add(md("""
                # What We(b) can do in 2025!?
                
                **App is in "presentation mode". All demos will (re-)open after the session. Wait for the presenter to begin...**
                
                You can prepare for voting the agenda at [wwcd.virit.in](https://wwcd.virit.in/)
                
                """));

        if(adminSession.isAdmin()) {
            add(new DefaultButton("Begin", e -> {
                appContext.begin();
                navigate(IntroView.class);
            }){{focus();}});
        }
    }

}
