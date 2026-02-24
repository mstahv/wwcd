package in.virit.wwcd;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.util.style.VaadinCssProps;

@Route(layout = MainLayout.class)
@MenuItem(icon = VaadinIcon.HOME, order = MenuItem.BEGINNING, title = "About")
public class MainView extends VerticalLayout {

    public MainView() {
        add(new Image("/photos/headline.svg", "What We(b) Can Do!"){{
            setMaxWidth("100%");
            getStyle().setBorderRadius(VaadinCssProps.RADIUS_L.var());
        }});
        add(new RichText().withMarkDown("""
           The web platform keeps evolving at an incredible pace, and modern browsers are far more capable than most developers realize.
           This showcase application demonstrates over a dozen powerful features that might surprise you—capabilities you didn't know
           existed or forgot were even possible.

           **Each example runs live in your browser**, built entirely with Java and [Vaadin](https://vaadin.com/). No JavaScript required.
           From hardware access to advanced graphics, from offline capabilities to biometric authentication—discover what's possible
           when you combine the power of modern web APIs with the elegance of Java development.

           **Ready to be amazed?** Explore the menu to see what we(b) can do!

           ---

           *🧸 Note: This app includes presentation features that may disable certain functionality during demos.*
           """));
    }
}
