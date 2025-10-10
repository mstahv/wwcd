package in.virit.wwcd.other;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.vaadin.addons.maplibre.BaseMapConfigurer;
import org.vaadin.addons.maplibre.MapLibre;

@ApplicationScoped
@Named("baseMapConfigurer")
public class MapConfig implements BaseMapConfigurer {
    @Override
    public void configure(MapLibre mapLibre) {
        mapLibre.initStyle("https://api.maptiler.com/maps/streets/style.json?key=XcKoEYvUam5KUROGRwGO");
        mapLibre.setCenter(8.6778088, 50.1153228);
        mapLibre.setZoomLevel(4);
    }
}
