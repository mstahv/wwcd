package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.color.Color;
import in.virit.color.NamedColor;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.util.ResizeObserver;
import org.vaadin.firitin.util.style.LumoProps;

@MenuItem(title = "Screen Orientation API", icon = VaadinIcon.ROTATE_LEFT)
@Route(layout = MainLayout.class)
public class ScreenOrientationView extends AbstractThing {

    public ScreenOrientationView() {
        setSizeFull();
        add(md("""
                Web apps can detect the orientation of the screen and to some extent lock it. This is sometimes handy on
                various handheld devices or with weird window sizes on desktop browsers, when optimizing the UX.

                * Detection can happen via [ScreenOrientation API](https://developer.mozilla.org/en-US/docs/Web/API/ScreenOrientation), [Screen API](https://developer.mozilla.org/en-US/docs/Web/API/Screen) or using [ResizeObserver](https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver).
                * Locking orientation (to dev specific position) works via [ScreenOrientation API](https://developer.mozilla.org/en-US/docs/Web/API/ScreenOrientation/lock) - on Android devices.
                * Workarounds for locking on Apple devices: CSS rotate against media queries (mediocre results), hint the user
                  about optimal orientation: *This video is horizontal, please rotate your screen...*

                This demo reports the size and small visualization of the view using ResizeObserver. I find this the most flexible approach as it can observe any visible element
                size (not just screen or window size). Used via helper in Viritin add-on.
                """));

                Div sizeReport = new VDiv(){{
                    getStyle().setBackgroundColor(NamedColor.DARKSLATEGREY);
                    getStyle().setColor(NamedColor.WHITE);
                    getStyle().setFontWeight(Style.FontWeight.BOLD);
                    getStyle().setFontSize(LumoProps.FONT_SIZE_L.var());
                    getStyle().setPadding("1em");
                    getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX);
                    setWidth("0");
                    setHeight("0");
                }};;
                add(sizeReport);


                ResizeObserver.get().observe(ScreenOrientationView.this, size -> {
                    String orientation;
                    if(size.width() == size.height()) {
                        orientation = "square";
                    } else if(size.width() > size.height()) {
                        orientation = "horizontal";
                    } else {
                        orientation = "vertical";
                    }

                    sizeReport.setText("%s W: %s H: %S".formatted(orientation, size.width(), size.height()));

                    sizeReport.setWidth( (size.width())/3 + "px");
                    sizeReport.setHeight( (size.height())/3 + "px");

                });

    }
}