package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Screen Orientation API", icon = VaadinIcon.ROTATE_LEFT)
@Route(layout = MainLayout.class)
public class ScreenOrientationView extends AbstractThing {

    public ScreenOrientationView() {
        add(md("""
                Web apps can detect the orientation of the screen and lock it. This sometimes handy on various
                handheld devices.
                
                TODO: ResizeObserver + lock hack
                
                """));
    }
}