package in.virit.wwcd.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class DemosView extends AbstractView {

    public DemosView(AppContext appContext) {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new Image("/photos/headline.svg", "What We(b) Can Do!") {{
            setMaxWidth("80%");
        }});

        AppContext.AgendaItem currentDemo = appContext.getCurrentDemo();
        if (currentDemo != null) {
            add(new H2(currentDemo.demo().getName()) {{
                getStyle().setMarginTop("1em");
            }});
        }

        add(new Icon(VaadinIcon.FILM) {{
            setSize("4em");
            getStyle().setOpacity("0.4");
            getStyle().setMarginTop("1em");
        }});

        add(md("""
                **Sit back and enjoy the show!**

                All demos and their source codes will open on this page after the presentation.
                """));
    }
}
