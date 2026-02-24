package in.virit.wwcd;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Style;

import java.time.Instant;

/**
 * A component that shows a timer for the presentation. Timer is updated every second using a client-side
 * JavaScript. The timer starts from the given start time and counts up to a maximum of 45 minutes.
 * The timer shows the elapsed time in minutes and seconds, time spent on the current view and time remaining.
 */
@Tag("presentation-timer")
public class PresentationTimer extends Component {

    public PresentationTimer() {
        getStyle().setTextAlign(Style.TextAlign.CENTER);
    }

    public void setStartTime(Instant startTime) {
        getElement().setProperty("start", startTime.toEpochMilli());
    }

    public void setStartOfView(Instant startOfView) {
        getElement().setProperty("startOfView", startOfView.toEpochMilli());
    }

    @Override
    public void setVisible(boolean visible) {
        // this will for some reason kill Aura styles in AppLayout
        // super.setVisible(visible);
        getElement().getStyle().setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
        getElement().executeJs("""
                if(!this.timerInterval) {
                    const el = this;
                    const maxDuration = 45 * 60 * 1000;
                    this.timerInterval = setInterval(() => {
                        if(!el.start) {
                            el.textContent = "Timer not started";
                            return;
                        }
                    
                        const now = Date.now();
                        const elapsed = now - el.start;
                        const elapsedView = now - el.startOfView;
                        const minutes = Math.floor(elapsed / 60000);
                        const seconds = Math.floor((elapsed % 60000) / 1000);
                        const minutesView = Math.floor(elapsedView / 60000);
                        const secondsView = Math.floor((elapsedView % 60000) / 1000);
                        const remaining = Math.max(0, maxDuration - elapsed);
                        const remainingMinutes = Math.floor(remaining / 60000);
                        const remainingSeconds = Math.floor((remaining % 60000) / 1000);
                        this.textContent = `${minutes}:${seconds.toString().padStart(2, '0')} | ${minutesView}:${secondsView.toString().padStart(2, '0')} | ${remainingMinutes}:${remainingSeconds.toString().padStart(2, '0')}`;
                        if (elapsed >= maxDuration) {
                            clearInterval(this.timerInterval);
                            this.textContent += " (Time's up!)";
                        }
                    }, 1000);
                }
                """);

    }
}
