package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MenuItem;

@Route(layout = MainLayout.class)
@MenuItem(title = "Device motion and position", icon = VaadinIcon.COFFEE)
public class DeviceMotionView extends AbstractThing {

    public DeviceMotionView(MultiplayerPuckGame multiplayerPuckGame) {
        add(md("""
                Device motion and position API's are available in modern browsers, but often relevant only in mobile
                devices/apps. The device motion API is bit related to Geolocation APIs, but in a different
                "scale" and powered by different sensors (gyroscope, accelometer, compass). In some cases they can be
                combined. Solutions can be creative, from helping navigation (auto-rotate map), to custom input solutions
                or games.
                
                """));
        add(multiplayerPuckGame);
    }
}