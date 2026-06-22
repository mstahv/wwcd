package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.fullscreen.Fullscreen;
import com.vaadin.flow.component.fullscreen.FullscreenState;
import com.vaadin.flow.signals.Signal;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.util.VStyleUtil;

@MenuItem(title = "Fullscreen API", icon = VaadinIcon.EXPAND_FULL)
@Route(layout = MainLayout.class)
public class FullscreenView extends AbstractThing {

    public FullscreenView() {
        add(md("""
                The [Fullscreen API](https://developer.mozilla.org/en-US/docs/Web/API/Fullscreen_API) provides an easy way to present web content using the user's entire screen.
                This can be useful for web applications such as games or video players, where maximizing the available
                screen space can enhance the user experience. Some mobile browsers have limited the API functionality,
                but on desktop it works well across browsers. On mobile apps, if fullscreen is desired, consider
                using the PWA mode, which launches the app in a standalone window without the browser "chrome".

                 * The JS API is "trivial", but "interesting things" can happen with SPAs like Vaadin. For Vaadin,
                   you can try for example the [Viritin add-on](https://vaadin.com/directory/component/flow-viritin),
                   which provides a simple Java API for the Fullscreen browser API and a couple of workarounds for the "surprises".
                 * Test this app's fullscreen mode by clicking the button in the menu.
                 * Or click/tap the photo below (makes the photo fullscreen). Note, iPhones don't support fullscreen!
                """));

        add(new FullscreenImage("/photos/view.jpg"));

    }


    public class FullscreenImage extends Image {

        private boolean firstSignal = true;

        public FullscreenImage(String imageUrl) {
            super(imageUrl, "A nice view");
            addClassName("fullscreen-image");
            VStyleUtil.injectAsFirst("""
                .fullscreen-image {
                    max-width: 300px;
                }
                
                :fullscreen .fullscreen-image {
                    max-width: 100vw;
                    width: 100vw;
                    height: 100vh;
                    object-fit: contain;
                    border: none;
                    border-radius: 0;
                    box-shadow: none;
                }
            
            """);
            // Pre-arm the request on the click trigger so it runs inside the click's own
            // user-gesture window; doing it in an addClickListener would be too late and rejected.
            Fullscreen.onClick(this).enter(this);

            // Click again while fullscreen to exit (the old toggle behaviour). exit() needs no
            // gesture, so a server-side listener works. The pre-armed enter still fires on this
            // click too, but re-requesting on the already-fullscreen element is a harmless no-op.
            addClickListener(e -> {
                if (Fullscreen.stateSignal().peek() == FullscreenState.FULLSCREEN) {
                    Fullscreen.exit();
                }
            });

            // React to state changes on the server. We just show notifications, but this is
            // where an app could pause a video, lock orientation, etc. The effect also fires on
            // Esc-exit, and once on registration — firstSignal tells that initial run apart from
            // a real exit.
            Signal<FullscreenState> state = Fullscreen.stateSignal();
            Signal.effect(this, () -> {
                switch (state.get()) {
                    case FULLSCREEN -> Notification.show("Entered fullscreen — click the photo or press Esc to exit");
                    case NOT_FULLSCREEN -> Notification.show(
                            firstSignal ? "Fullscreen mode is not active" : "Exited fullscreen.");
                    case UNSUPPORTED -> Notification.show("Unsupported fullscreen state.");
                    case UNKNOWN -> Notification.show("Unknown fullscreen state.");
                }
                firstSignal = false;
            });
        }

    }
}