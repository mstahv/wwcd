package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.util.WebAudio;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@MenuItem(title = "WebAudio API", icon = VaadinIcon.VOLUME_UP)
@Route(layout = MainLayout.class)
public class WebAudioView extends AbstractThing {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public WebAudioView() {
        add(md("""
                The browser ships with a powerful [Web Audio API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API)
                that can synthesize sounds on the fly — no audio files needed. Even if you are not building a DJ
                software online, sometimes a small sound maybe be relevant to catch the attention of a users.
                
                The Viritin add-on contains a small `WebAudio` helper class that wraps the most common use cases for a 
                Java/Vaadin developer: short beeps to confirm an action, an alarm tone to grab attention, or a custom 
                tone with a chosen frequency and waveform.

                Browsers require a *user gesture* before the audio context is allowed to play. The first
                button click on this page initializes the context for you. Background-thread alarms (the
                "Delayed alarm" button below) work because the context is already running by then.
                """));

        add(new H4("Preset sounds"));
        add(new VHorizontalLayout(
                new Button("OK beep (cashier style)", e -> WebAudio.get().playOkBeep()),
                new Button("Alarm", e -> WebAudio.get().playAlarm()),
                new Button("Start sequence", e -> WebAudio.get().playStartSequence())
        ));

        add(new H4("Delayed alarm (5 s)"));
        add(md("Demonstrates triggering a sound from a background thread — useful e.g. for a server-side warning."));
        add(new Button("Schedule alarm in 5 s", e -> {
            var ui = ui();
            scheduler.schedule(() -> ui.access(() -> WebAudio.get().playAlarm()),
                    5, TimeUnit.SECONDS);
            Notification.show("Alarm scheduled — switch tabs if you like, it will still fire.");
        }));

        add(new H4("Custom beep"));
        add(new CustomBeepPanel());

        add(new H4("Server-side jam session"));
        add(md("""
                Each note is sent from a Java *virtual thread* on the server, one tone at a time —
                `Thread.sleep` between notes, no audio file in sight. Sawtooth waveform for that
                cheap-synth vibe.
                """));
        add(new JamButton("Play Popcorn using VirtualThread", POPCORN));
    }

    private record Note(double freq, int durationMs) {}

    /** {@code staccato} is the fraction of each slot the tone actually sounds (1.0 = legato). */
    private record Song(String title, WebAudio.Waveform waveform, double staccato, List<Note> notes) {}

    private static final double REST = 0;
    private static final double B5 = 987.77, D6 = 1174.66, F6 = 1396.91;
    private static final double G6 = 1567.98, A6 = 1760.00, B6 = 1975.53;
    private static final double CS7 = 2217.46, D7 = 2349.32;

    private static final Song POPCORN = popcorn();

    private static Song popcorn() {
        int s = 130;       // sixteenth note at ~115 BPM
        int e = s * 2;
        return new Song("Popcorn", WebAudio.Waveform.SAWTOOTH, 0.45, List.of(
                // Phrase A: B6 A6 B6 F6 D6 F6 B5
                new Note(B6, s), new Note(A6, s), new Note(B6, s), new Note(F6, s),
                new Note(D6, s), new Note(F6, s), new Note(B5, e), new Note(REST, s),
                // Phrase A repeat
                new Note(B6, s), new Note(A6, s), new Note(B6, s), new Note(F6, s),
                new Note(D6, s), new Note(F6, s), new Note(B5, e), new Note(REST, s),
                // Phrase B: B6 C#7 D7 C#7 D7 B6 C#7 B6 C#7 A6 B6 A6 B6 G6 A6 B6
                new Note(B6, s), new Note(CS7, s), new Note(D7, s), new Note(CS7, s),
                new Note(D7, s), new Note(B6, s), new Note(CS7, s), new Note(B6, s),
                new Note(CS7, s), new Note(A6, s), new Note(B6, s), new Note(A6, s),
                new Note(B6, s), new Note(G6, s), new Note(A6, s), new Note(B6, e)
        ));
    }

    private class JamButton extends Button {
        private final Song song;

        public JamButton(String label, Song song) {
            this.song = song;
            setText(label);
            setIcon(VaadinIcon.MUSIC.create());
            addClickListener(e -> play());
        }

        private void play() {
            UI ui = ui();
            WebAudio audio = WebAudio.get();
            setEnabled(false);
            Thread.ofVirtual().name("jam-" + song.title()).start(() -> {
                try {
                    for (Note note : song.notes()) {
                        if (note.freq() > 0) {
                            int toneMs = Math.max(40, (int) (note.durationMs() * song.staccato()));
                            ui.access(() -> audio.playBeep(toneMs, note.freq(), song.waveform()));
                        }
                        Thread.sleep(note.durationMs());
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } finally {
                    ui.access(() -> setEnabled(true));
                }
            });
        }
    }

    private static class CustomBeepPanel extends VHorizontalLayout {

        private final IntegerField duration = new IntegerField("Duration (ms)") {{
            setValue(200);
            setStepButtonsVisible(true);
            setMin(10);
            setMax(5000);
        }};

        private final NumberField frequency = new NumberField("Frequency (Hz)") {{
            setValue(WebAudio.DEFAULT_FREQUENCY_HZ);
            setStepButtonsVisible(true);
            setMin(20);
            setMax(20000);
            setStep(50);
        }};

        private final Select<WebAudio.Waveform> waveform = new Select<>() {{
            setLabel("Waveform");
            setItems(WebAudio.Waveform.values());
            setValue(WebAudio.Waveform.SINE);
        }};

        private final Button play = new Button("Play", e ->
                WebAudio.get().playBeep(duration.getValue(), frequency.getValue(), waveform.getValue()));

        public CustomBeepPanel() {
            setAlignItems(Alignment.END);
            add(duration, frequency, waveform, play);
        }
    }
}
