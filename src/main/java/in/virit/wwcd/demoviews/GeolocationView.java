package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import in.virit.wwcd.session.UISession;
import org.vaadin.addons.maplibre.MapLibre;
import org.vaadin.addons.maplibre.components.TrackerMarker;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VParagaph;
import com.vaadin.flow.component.geolocation.Geolocation;
import com.vaadin.flow.component.geolocation.GeolocationCoordinates;
import com.vaadin.flow.component.geolocation.GeolocationWatcher;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import org.vaadin.firitin.rad.PrettyPrinter;
import org.vaadin.firitin.util.style.AuraProps;

import java.util.concurrent.atomic.AtomicBoolean;

@Route(layout = in.virit.wwcd.MainLayout.class)
@MenuItem(title = "Geolocation", icon = VaadinIcon.GLOBE)
public class GeolocationView extends AbstractThing {

    private final MapLibre map;
    private GeolocationWatcher watcher;
    private TrackerMarker trackerMarker;
    GeoDataDisplay rawDataDisplay = new GeoDataDisplay();

    public GeolocationView() {
        add(md("""
                One of the oldest "hardware integrations" to web, and probably nothing new, but my old-time favourite.
                At least it is good to remind that all this can be accomplished using Vaadin with *100% pure Java*.
                Also, there is more than just lat-longs available, especially if on GPS (~ mobile devices)!
                Solutions utilizing geolocation data can sometimes also benefit from data coming via [device motion API](devicemotion).
                """));
        add(rawDataDisplay);


        map = new MapLibre();
        map.setMinHeight("400px");
        addAndExpand(map);
        trackerMarker = new TrackerMarker(map);
        trackerMarker.setColor(NamedColor.DARKOLIVEGREEN);
        addNavbarHelper(new VButton(VaadinIcon.BULLSEYE, this::enableGeolocation));

    }

    private void enableGeolocation() {
        if(watcher != null) {
            watcher.stop();
            watcher = null;
            Notification.show("Geolocation tracking disabled");
            return;
        }

        AtomicBoolean firstPosition = new AtomicBoolean(true);

        // watchPosition is bound to this component and stops automatically on detach
        this.watcher = Geolocation.watchPosition(this);
        watcher.addPositionListener(position -> {
            var geolocationData = position.coords();
            trackerMarker.addPoint(geolocationData.longitude(), geolocationData.latitude());
            if(firstPosition.getAndSet(false)) {
                map.flyTo(trackerMarker.getMarker().getGeometry(), 15);
            }
            rawDataDisplay.setData(geolocationData);
        }, error -> {
            Notification.show("Geolocation error: " + error.debugInfo());
        });
        Notification.show("Geolocation tracking requested!");
    }

    private class GeoDataDisplay extends HorizontalFloatLayout {

        public void setData(GeolocationCoordinates data) {
            removeAll();
            addData("Longitude", data.longitude());
            addData("Latitude", data.latitude());
            addData("Altitude", data.altitude());
            addData("Heading", data.heading());
            addData("Speed", data.speed());
            addData("Accuracy (meters)", data.accuracy());
            addData("Altitude accuracy", data.altitudeAccuracy());
        }

        private void addData(String header, Double value) {
            add(new VerticalLayout(new H5(header), new Pre(String.valueOf(value))){{
                setMargin(false);
                setPadding(false);
                getStyle().setFontSize(AuraProps.FONT_SIZE_XS.var());
                setWidth(null);
            }});
        }
    }
}