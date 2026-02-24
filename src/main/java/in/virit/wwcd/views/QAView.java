package in.virit.wwcd.views;

import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.DefaultButton;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true, title = "Q&A and takeaways")
public class QAView extends AbstractView {

    public QAView(AdminSession adminSession, AppContext appContext) {
        add(md("""

                Questions, comments, ideas?

                Bookmark this app at [w.virit.in](https://w.virit.in/), all demos will be available after the presentation.

                """));

        if(adminSession.isAdmin()) {
            add(new DefaultButton("Close the presentation", () -> appContext.closePresentation()));
        }
    }

}
