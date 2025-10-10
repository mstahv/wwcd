package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Installed", icon = VaadinIcon.PASSWORD)
@Route(layout = MainLayout.class)
public class InstallingView extends AbstractThing {

    public InstallingView() {
        add(md("""
                "Progressive Web Apps" (PWA) is a term for web applications that can be installed to device "home screen" and
                run as a standalone app, outside the browser. A think to not about modern PWAs, is that they are NOT
                tied to mobile devices only. For example latest Windows and MacOS versions support "installing" PWA:s 
                as well.
                
                Vaadin developers are priviliged to have a built-in PWA support for their apps via a single
                `@PWA` annotation. See more from [Vaadin PWA documentation](https://vaadin.com/docs/latest/flow/pwa/overview).
                
                TODO video about installing and running as standalone app on a mobile device.
                """));
    }
}