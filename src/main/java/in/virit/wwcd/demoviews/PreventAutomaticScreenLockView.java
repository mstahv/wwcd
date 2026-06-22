package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.wakelock.WakeLock;
import com.vaadin.flow.signals.Signal;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

import java.time.Instant;

@MenuItem(title = "Wake lock", icon = VaadinIcon.MOON)
@Route(layout = MainLayout.class)
public class PreventAutomaticScreenLockView extends AbstractThing {

    TimerWidget timerWidget = new TimerWidget();

    public PreventAutomaticScreenLockView() {

        add(md("""
                There are various scenarios where you might want to prevent the device from automatically locking the screen.
                For example when displaying a presentation, a map for navigation, or a video.
                
                To achieve this, we can use the [Screen Wake Lock API](https://developer.mozilla.org/en-US/docs/Web/API/Screen_Wake_Lock_API), which allows web applications to request a wake 
                lock to keep the screen on. This is supported by most modern browsers.
                
                The example builds on the Java API built into Vaadin 25.2 (`com.vaadin.flow.component.wakelock.WakeLock`): request a wake lock and start counting seconds (check sleep timeout from your device settings). Before 25.2 the Viritin add-on offered a similar helper.
                """));

        Div wakeLockStatus = new Div("Wake lock status: unknown");

        // activeSignal() reflects the live state: it flips back to false automatically
        // when the browser releases the lock (e.g. the document became inactive).
        Signal<Boolean> active = WakeLock.activeSignal();
        Signal.effect(this, () -> wakeLockStatus.setText("Wake lock active: " + active.get()));

        add(new Button("Request wake lock and reset timer", event -> {
            timerWidget.resetTimer();
            WakeLock.request(error -> wakeLockStatus.setText("Wake lock request failed: " + error.message()));
        }));

        add(new Button("Reset timer", event -> {
            timerWidget.resetTimer();
        }));

        add(wakeLockStatus);
        add(timerWidget);

    }

    @Tag("timer-widget")
    public static class TimerWidget extends Component {

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            getElement().executeJs("""
                    this.startTimer = function() {
                        this.interval = setInterval(() => {
                            const start = this.start || Date.now();
                            const elapsed = Math.floor((Date.now() - start) / 1000);
                            this.textContent = `Elapsed time: ${elapsed} seconds`;
                        }, 1000);
                    };
                    """);
            resetTimer();
        }

        public void resetTimer() {
            getElement().setProperty("start", Instant.now().toEpochMilli());
            getElement().executeJs("this.startTimer();");
        }
    }
}