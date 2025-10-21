package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.webnotification.WebNotification;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@MenuItem(title = "Notifications", icon = VaadinIcon.BELL)
@Route(layout = MainLayout.class)
public class NotificationsView extends AbstractThing {

    private final WebNotification webNotification;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public NotificationsView() {
        add(md("""
                There are three main categories for showing notifications to your users.

                * Show them within your web app with pure web tech. Extremely flexible, but useless if your web app is
                  not open.
                * Use operating system provided service via Web Notifications API
                * Ship them even if window and browser is closed with Web Push Notifications API.

                [Article comparing these approaches.](https://vaadin.com/blog/which-notifications-are-best-for-your-java-app-web-vaadin-or-push)
                """));

        add(new Button("Notification.show(\"Hello world\") with delay (5 secs)", e -> {
            executorService.schedule(() -> {
                ui().access(() -> Notification.show("Hello world"));
            }, 5, java.util.concurrent.TimeUnit.SECONDS);
        }));

        webNotification = WebNotification.get(); // Get the WebNotification instance, which bound to
        // the current UI. You can also use `WebNotification.get(yourUIInstance)` in more complex scenarios.

        // Check the permission status of Web Notifications, if not granted, request it,
        // else show the demo buttons to trigger notifications.
        webNotification.checkPermission().thenAccept(permission -> {
            if (permission == WebNotification.Permission.GRANTED) {
                Notification.show("Web Notification permission is already granted.");
                addWebNotificationDemoButton();
                return;
            }

            if (permission == WebNotification.Permission.DENIED) {
                // Using Vaadin Notification to show Web Notifications permission denied :-)
                Notification.show("Web Notification permission is denied. You can change it in browser settings. Web" +
                        "app alone can't anymore request it.");
                add("Permission denied by the user. You can change it in browser settings. Web app alone can't request it anymore.");
            } else {
                Notification.show("Web Notification permission is default. You can request it to enable the demo.");
            }
            add(new VButton("requestPermission", e -> {
                webNotification.requestPermission(() -> {
                    Notification.show("Permissions granted. You can now try the demo buttons.");
                    addWebNotificationDemoButton();
                    e.getSource().removeFromParent();
                }, () -> {
                    Notification.show("User denied permission.");
                });
            }));
        });

    }

    private void addWebNotificationDemoButton() {
        add(new VButton("Web Notification with delay (5 secs)", e1 -> {
            executorService.schedule(() -> {
                /*
                 * Note that this is happening in non UI thread, so modifying the UI directly
                 * is not allowed. Thus, we use `showNotificationAsync` method instead, which
                 * uses the `UI.access()` method to safely modify the UI. In case you are a big
                 * fan of spoiling your UI code with UI handling and UI.access() calls, you can
                 * also use the `webNotification.showNotification` method with the usual dance.
                 */
                webNotification.showNotificationAsync("Delayed notification from Vaadin!");
            }, 5, java.util.concurrent.TimeUnit.SECONDS);
        }));

        image("/liukuri.png", "See how [Liukuri](https://liukuri.fi/) uses web **push** notifications to notify users of high " +
                "electricity prices - even while they are enjoying the Finnish winter.", "4em");
    }
}