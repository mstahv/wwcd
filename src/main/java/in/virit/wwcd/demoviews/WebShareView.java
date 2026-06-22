package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.webshare.ShareContent;
import com.vaadin.flow.component.webshare.WebShare;
import com.vaadin.flow.component.webshare.WebShareSupport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import in.virit.wwcd.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Web Share API", icon = VaadinIcon.SHARE)
@Route(layout = MainLayout.class)
public class WebShareView extends AbstractThing {

    public WebShareView() {
        add(md("""
                The [Web Share API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Share_API) lets a web app
                invoke the device's *native* share dialog — the same sheet you get from native apps, with all the
                installed targets (Messages, Mail, AirDrop, social apps...). It is mostly a mobile thing and requires
                a secure context (HTTPS) and a user gesture.

                New in Vaadin 25.2: a pure Java API for it. As with clipboard and fullscreen, sharing is bound to a
                button click so it runs inside the browser's user-gesture trust window.
                """));

        Span supportStatus = new Span();
        Signal<WebShareSupport> support = WebShare.supportSignal();
        Signal.effect(this, () -> supportStatus.setText("Browser support: " + support.get()));
        add(supportStatus);

        Button shareButton = new Button("Share this demo", VaadinIcon.SHARE.create());
        WebShare.onClick(shareButton).share(
                ShareContent.create()
                        .title("What Web Can Do")
                        .text("Check out what the web platform can do with 100% Java and Vaadin!")
                        .url("https://github.com/mstahv/wwcd"),
                () -> Notification.show("Shared!"),
                error -> Notification.show("Share cancelled or failed: " + error.message()));
        add(shareButton);
    }
}
