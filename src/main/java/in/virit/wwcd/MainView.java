package in.virit.wwcd;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.RichText;

@Route(layout = MainLayout.class)
@MenuItem(icon = VaadinIcon.HOME, order = MenuItem.BEGINNING, title = "About")
public class MainView extends VerticalLayout {

    public MainView() {
        add(new RichText().withMarkDown("""
           # What We(b) Can Do!?
           
           Browsers and WWW in general keeps evolving. This example app contains a dozen (or so) of inspirational features that\s
           you probably didn't even realize (or remember) to be possible. Many of the examples are demoed within the
           example itself, with plain Java powerd by [Vaadin }>](https://vaadin.com/).
           
           🧸The app contains some "presentation features" for the demo, which might disable some features during
            presentations 🧸
           """));
    }
}
