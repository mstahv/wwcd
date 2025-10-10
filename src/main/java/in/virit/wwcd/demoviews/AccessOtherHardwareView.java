package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Access hardware", icon = VaadinIcon.PLUG)
@Route(layout = MainLayout.class)
public class AccessOtherHardwareView extends AbstractThing {

    public AccessOtherHardwareView() {
        add(md("""
                As opposed to general belief you can actually do quite a lot with web and devices. And Chromium based
                browsers even allows you to directly access Bluetooth, Serial and USB ports 🤯
                
                 * High level access via Print, Geolocation etc.
                 * HID with hacks, e.g. barcode scanners often act as keyboards, or custom drivers
                 * Network connected devices aka IoT
                 * Server connected devices (e.g. print to an office)
                 * Example: [measure heart rate with BLE belt](https://hr.dokku1.parttio.org) (Chrome only!)
                 * Example: [Read orienteering competition cards with serial port](https://ereader.dokku1.parttio.org) (Chrome only!)
                
                """));
        image("/photos/wmoc-emits.jpg", "A Vaadin based web app was used to organize Emit e-Cards in World Masters Orienteering Championships 2024 in Finland.");
    }

}