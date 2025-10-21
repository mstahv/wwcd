package in.virit.wwcd.views;

import com.vaadin.flow.component.html.Image;
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
        add(new Image("/photos/headline.svg", "What We(b) Can Do!"){{
            setMaxWidth("100%");
        }});
        add(md("""                
                **App is in "presentation mode". All demos will (re-)open after the session. Wait for the presenter to begin...**
                
                """));

        if(adminSession.isAdmin()) {
            add(new DefaultButton("Begin", e -> {
                appContext.begin();
                navigate(IntroView.class);
            }){{focus();}});
        }
    }

}
