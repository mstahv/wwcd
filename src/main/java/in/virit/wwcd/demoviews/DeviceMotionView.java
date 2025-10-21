package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.devicemotion.DeviceMotion;
import org.vaadin.firitin.devicemotion.DeviceMotionEvent;
import org.vaadin.firitin.rad.PrettyPrinter;

@Route(layout = MainLayout.class)
@MenuItem(title = "Device motion and position", icon = VaadinIcon.ROTATE_LEFT)
public class DeviceMotionView extends AbstractThing {

    public DeviceMotionView() {
        add(md("""
                Device motion and position APIs are available in modern browsers, but often relevant only in mobile
                devices/apps. The device motion API is a bit related to Geolocation APIs, but in a different
                "scale" and powered by different sensors (gyroscope, accelerometer, compass). In some cases they can be
                combined. Solutions can be creative, from helping navigation (auto-rotate map), to custom input solutions
                or games.

                Try separate [Puck "Game"](https://puck.dokku1.parttio.org/) or read boring numbers below! Note, you'll need a device with relevant sensors
                (most modern phones/tablets should have them).

                Below is a "number dump" on supported devices.
                """));

        Div details = new Div();
        add(details);

        DeviceMotion.listen(new DeviceMotion.MotionListener() {
            @Override
            public void deviceMotionUpdate(DeviceMotionEvent deviceMotionEvent) {
                details.removeAll();
                details.add(new H5("Acceleration"));
                details.add(PrettyPrinter.toVaadin(deviceMotionEvent.getAcceleration()));
                details.add(new H5("Rotation"));
                details.add(PrettyPrinter.toVaadin(deviceMotionEvent.getRotationRate()));
            }
        });

    }
}