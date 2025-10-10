package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import in.virit.wwcd.session.AppContext;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Read and write to clipboard", icon = VaadinIcon.COFFEE)
@Route(layout = MainLayout.class) // TODO fixme, why it doesn't work from parent class anymore!?
public class ClipboarAPIView extends AbstractThing {

    public ClipboarAPIView() {
        add(md("""
                Modern browsers share a common API to read and write contents to the clipboard.
                """));
    }
}