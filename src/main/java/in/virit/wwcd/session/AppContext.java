package in.virit.wwcd.session;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.spring.annotation.SpringComponent;
import in.virit.wwcd.Demo;
import in.virit.wwcd.MainView;
import in.virit.wwcd.Tagline;
import in.virit.wwcd.views.AgendaView;
import in.virit.wwcd.views.DemosView;
import in.virit.wwcd.views.LobbyView;
import in.virit.wwcd.views.QAView;
import in.virit.wwcd.views.VotingLeaderboardView;
import in.virit.wwcd.views.VotingView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SpringComponent
public class AppContext {

    private static final Logger log = LoggerFactory.getLogger(AppContext.class);

    private Class<? extends Component> currentView;
    private List<AgendaItem> agenda;
    private Instant startOfView;
    private Instant startofPresentation;
    private boolean spectatorMode;
    private AgendaItem currentDemo;

    public void registerUI(UISession uiSession) {
        uiSessions.add(uiSession);
    }

    public void unregisterUI(UISession uiSession) {
        uiSessions.remove(uiSession);
    }

    public Class<? extends Component> getCurrentView() {
        return currentView;
    }

    public void closePresentation() {
        state = AppState.Normal;
        spectatorMode = false;
        currentDemo = null;
        moveSessionsTo(MainView.class);
        UI.getCurrent().navigate(MainView.class);
    }

    public void vote() {
        log.info("Voting phase started");
        state = AppState.Voting;
        voters.set(0);
        for(Tagline t : Tagline.values()) {
            taglinePointMap.get(t).set(0);
        }
        moveSessionsTo(VotingView.class);
        UI.getCurrent().navigate(VotingLeaderboardView.class);
    }

    public void closeVoting() {
        int totalVotes = taglinePointMap.values().stream().mapToInt(AtomicInteger::get).sum();
        log.info("Closing voting: {} voters, {} total votes cast", voters.get(), totalVotes);
        buildAgenda();
        state = AppState.Agenda;
        moveSessionsTo(AgendaView.class);
        UI.getCurrent().navigate(AgendaView.class);
    }

    public AppState getState() {
        return state;
    }

    public void showDemo(AgendaItem t) {
        state = AppState.Demos;
        t.presented().set(true);
        currentDemo = t;
        if (spectatorMode) {
            moveSessionsTo(DemosView.class);
        } else {
            moveSessionsTo(t.demo().getView());
        }
    }

    public void qa() {
        state = AppState.QA;
        moveSessionsTo(QAView.class);
    }

    public Instant getStartOfView() {
        return startOfView;
    }

    public Instant getStartOfPresentation() {
        return startofPresentation;
    }

    public enum AppState {
        Normal, Presentation, Voting, Agenda, Demos, QA
    }

    private volatile AppState state = AppState.Normal;

    private Set<UISession> uiSessions = new CopyOnWriteArraySet<>();

    public int sessionCount() {
        return uiSessions.size();
    }

    public boolean isPresentation() {
        return state != AppState.Normal;
    }

    public boolean isVoting() {
        return state == AppState.Voting;
    }

    public boolean isNormal() {
        return state == AppState.Normal;
    }

    @Value("${app.password}")
    private String appPassword;

    public void present(UISession uiSession, AdminSession adminSession, String password, boolean spectatorMode) {
        if(!password.equals(appPassword)) {
            Notification.show("Password did not match! If you want to use the hosted presentation mode, contact matti ät vaadin dot com").setPosition(Notification.Position.MIDDLE);
            return;
        }
        state = AppState.Presentation;
        startofPresentation = Instant.now();
        this.spectatorMode = spectatorMode;
        taglinePointMap.forEach((t, v) -> v.set(0));
        uiSessions.remove(uiSession);
        adminSession.setAdmin(true);
        moveSessionsTo(LobbyView.class);
        Notification.show("Entered presentation mode").setPosition(Notification.Position.MIDDLE);
        uiSession.navigate(LobbyView.class);
    }
    public void moveSessionsTo(Class<? extends Component> view) {
        startOfView = Instant.now();
        currentView = view;
        for(UISession session : uiSessions) {
            session.navigate(view);
        }
    }


    private Map<Tagline, AtomicInteger> taglinePointMap = new ConcurrentHashMap<>();
    {
        for(Tagline t : Tagline.values()) {
            taglinePointMap.put(t, new AtomicInteger(0));
        }
    }

    private final AtomicInteger voters = new AtomicInteger(0);

    public void registerVoter() {
        int count = voters.incrementAndGet();
        log.info("Voter registered, total voters: {}", count);
    }

    public int getVoters() {
        return voters.get();
    }

    @EventListener
    public void votesChanged(VotesChanged votesChanged) {
        if(state == AppState.Voting) {
            int newCount = taglinePointMap.get(votesChanged.tagline()).addAndGet(votesChanged.votes());
            log.info("Vote: {} {} (now {})", votesChanged.tagline(), votesChanged.votes() > 0 ? "+" + votesChanged.votes() : votesChanged.votes(), newCount);
        } else {
            log.warn("Ignoring votes change event, not in voting state");
        }
    }

    public Map<Tagline, Integer> calculatePoints() {
        Map<Tagline, Integer> snapshot = new HashMap<>();
        taglinePointMap.forEach((t, v) -> snapshot.put(t, v.get()));
        return Collections.unmodifiableMap(snapshot);
    }

    public boolean isSpectatorMode() {
        return spectatorMode;
    }

    public AgendaItem getCurrentDemo() {
        return currentDemo;
    }

    public record AgendaItem(Demo demo, int points, AtomicBoolean presented) {}

    public List<AgendaItem> getAgenda() {
        return agenda;
    }

    public void buildAgenda() {
        // based on tagline points, build an agenda of demos, order top most voted demos to have priority
        Map<Demo, Integer> demoPoints = new HashMap<>();
        for(Demo d : Demo.values()) {
            demoPoints.put(d, 0);
        }
        // take a consistent snapshot of votes
        Map<Tagline, Integer> pointSnapshot = calculatePoints();
        for(Map.Entry<Tagline, Integer> entry : pointSnapshot.entrySet()) {
            Tagline t = entry.getKey();
            int points = entry.getValue();
            if(points > 0) {
                Demo d = t.getDemo();
                demoPoints.put(d, demoPoints.getOrDefault(d, 0) + points);
            }
        }
        agenda = demoPoints.entrySet().stream()
                .map(e -> new AgendaItem(e.getKey(), e.getValue(), new AtomicBoolean(false)))
                .sorted((b, a) -> Boolean.compare(a.demo().isPreselected(), b.demo().isPreselected()))
                .sorted((a, b) -> Integer.compare(b.points(), a.points()))
                .toList();
    }

}
