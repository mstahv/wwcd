package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Installed", icon = VaadinIcon.PASSWORD)
@Route(layout = MainLayout.class)
public class InstallingView extends AbstractThing {

    public InstallingView() {
        add(md("""
                "Progressive Web Apps" (PWA) is a term for web applications that can be installed to device "home screen" and
                run as a standalone app, outside the browser. A thing to note about modern PWAs is that they are NOT
                tied to mobile devices only. For example, the latest Windows and MacOS versions support "installing" PWAs
                as well.

                Vaadin developers are privileged to have built-in PWA support for their apps via a single
                `@PWA` annotation. See more from [Vaadin PWA documentation](https://vaadin.com/docs/latest/flow/pwa/overview).
                """));

        add(new HomeScreenVideo());

    }

    @Tag("video")
    static class HomeScreenVideo extends Component {
        HomeScreenVideo() {
            getElement().setAttribute("controls", "");
            getElement().setAttribute("loop", "");
            getStyle().setMaxHeight("50vh");
            Element source = new Element("source");
            source.setAttribute("src", "/photos/adding-to-homescreen.mov");
            source.setAttribute("type", "video/mp4");
            getElement().appendChild(source);
        }
    }
}