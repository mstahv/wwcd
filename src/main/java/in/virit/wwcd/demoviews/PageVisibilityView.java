package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.util.PageVisibility;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@MenuItem(title = "Page Visibility API", icon = VaadinIcon.EYE)
@Route(layout = MainLayout.class)
public class PageVisibilityView extends AbstractThing {

    private final ScheduledFuture<?> future;
    private final Registration registration;
    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private PageVisibility.Visibility visibility;
    private boolean notificationsEnabled;

    public PageVisibilityView() {
        add(md("""
                For rich UIs it can be surprisingly important to know if the user is actually looking at the page or if the
                page is in a background tab. For example, certain notifications can be totally lost if the user is not looking at the page
                and should be displayed differently when the user returns to the page. Also, there can be performance benefits,
                for example pausing certain updates when the user is not actively looking at the page.

                All modern browsers
                support [Page Visibility API](https://developer.mozilla.org/en-US/docs/Web/API/Page_Visibility_API), but I have found out that sometimes one is better off by also tracking
                if the page has focus in it (an open browser window reports visible, but can be behind another one).
                PageVisibility helper class in Viritin add-on combines these into a single API.

                Below, the log is updated every second, but only if the page is visible. Also, when the page visibility
                changes. If notifications are enabled, a notification is shown when the visibility changes.
                """));

        add(new Checkbox("Show notifications when page visibility changes", false) {
            {
                addValueChangeListener(e -> {
                    notificationsEnabled = e.getValue();
                });
            }
        });

        Log log = new Log();

        // In some cases detecting the state once is enough
        PageVisibility.get().isVisible().thenAccept(v -> {
            visibility = v;
        });

        // Maintain the visibility state and reacting to changes via listener
        registration = PageVisibility.get().addVisibilityChangeListener(v -> {
            visibility = v;
            String text = "Page visibility changed: " + v;
            // always log it
            log.log(text);

            // Optionally show a notification about it
            if (notificationsEnabled) {

                if (v == PageVisibility.Visibility.VISIBLE) {
                    // By default, defaults are fine, the notification is shown at bottom left and will disappear soonish
                    Notification.show(text);
                } else {
                    // Configure a persistent notification based on the visibility state
                    new Notification() {{
                        setText(text);
                        // If the page is not focused or is totally hidden, our notification would be lost
                        // Configure it so that it stays on the screen until the user closes it
                        setDuration(-1);
                        add(new H5("Page was not focused or hidden"));
                        add(new Paragraph("This notification is configured to stay on the screen until you close it. " +
                                "Otherwise you would not see it if you stay in other app for more than couple of secs."));
                        add(new Button("Got it!", e -> close()));
                        setPosition(Notification.Position.MIDDLE);
                        // Optionally you could make it dissapear automatically after a delay after
                        // the page becomes visible again...
                    }}.open();
                }
            }
        });

        UI ui = UI.getCurrent();
        future = executorService.scheduleWithFixedDelay(() -> {
            ui.access(() -> {
                if (visibility == PageVisibility.Visibility.VISIBLE) {
                    // Only do this if the page is visible and focused
                    log.log("periodic background task, UI visibility: " + visibility);
                } else {
                    // You could do something else still, e.g. based on last request timestamp
                    Instant lastRequest = Instant.ofEpochMilli(ui.getSession().getLastRequestTimestamp());
                    System.out.println("periodic task, visibility: " + visibility + ", last request: " + lastRequest);
                }
            });

        }, 1000, 1000, java.util.concurrent.TimeUnit.MILLISECONDS);

        add(log);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        registration.remove();
        future.cancel(true);
    }

    static class Log extends Composite<VerticalLayout> {
        public Log() {
            getStyle().setPadding("1em");
            getStyle().setBorder("1px solid #ccc");
            getContent().setSpacing(false);
        }

        public void log(String message) {
            getContent().add(new Pre(LocalDateTime.now() + ": " + message) {{
                getStyle().setMargin("0");
            }});
            // Keep the log size reasonable
            if (getContent().getComponentCount() > 10) {
                getContent().getChildren().findFirst().ifPresent(Component::removeFromParent);
            }
        }
    }
}