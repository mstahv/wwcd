package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.MapLibre;
import org.vaadin.addons.maplibre.dto.FlyToOptions;
import org.vaadin.addons.maplibre.dto.LngLat;
import org.vaadin.addons.maplibre.dto.Projection;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.VButton;

@MenuItem(title = "WebGL Hardware accelerated 3D graphics", icon = VaadinIcon.CUBE)
@Route(layout = MainLayout.class)
public class HardwareAccelerated3DView extends AbstractThing {

    public HardwareAccelerated3DView() {
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        add(md("""
                WebGL is a well established standard to utilize your graphics card for advanced 3D rendering.
                It is widely used in games and advanced visualizations, but also in e.g. advanced web based maps.
                3D graphics rendering is hardware accelerated on most devices, including mobile phones.
                
                 * Example: MapLibreGL - an open source 3D capable slippy map widget
                
                """));

        var map = new FrankfurtMapLibreGLExample();

        findAncestor(MainLayout.class).addNavbarHelper(
                new VButton(VaadinIcon.GLOBE, e -> {
                    map.setProjection(Projection.GLOBE); // world is round, on smaller zoom levels :-)
                }));
        findAncestor(MainLayout.class).addNavbarHelper(
                new VButton("Fly to Frankfurt", e -> {
                    map.flyTo(new FlyToOptions() {{
                        setCenter(new LngLat(8.6778088, 50.1153228));
                        setZoom(16.0);
                        setPitch(60.0);
                        setBearing(180.0);
                        setDuration(5000);
                    }});
                }));
        addAndExpand(map);

    }

    private static class FrankfurtMapLibreGLExample extends MapLibre {

        public FrankfurtMapLibreGLExample() {
            setSizeFull();
            addMarker(8.6778088, 50.1153228)
                    .withPopup("Frankfurt am Main<br>Germany");
            setZoomLevel(2);
        }
    }
}