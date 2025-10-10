package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Wake lock", icon = VaadinIcon.MOON)
@Route(layout = MainLayout.class)
public class PreventAutomaticScreenLockView extends AbstractThing {

    public PreventAutomaticScreenLockView() {
        add(md("""
                TODO
                """));
    }
}