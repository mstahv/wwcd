package in.virit.wwcd.session;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.quarkus.annotation.UIScoped;
import in.virit.wwcd.Tagline;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Set;

/**
 * A regular UI session, with methods for presentation.
 */
@UIScoped
public class UISession {
    public static final int MAXVOTES = 10;

    private final UI ui;

    @Inject
    AppContext context;

    @Inject
    Event<VotesChanged> votesChangedEvent;

    Set<Tagline> votes = new HashSet<>();

    public UISession() {
        this.ui = UI.getCurrent();
    }

    @PostConstruct
    void init() {
        context.registerUI(this);
    }

    @PreDestroy
    void destroy() {
        context.unregisterUI(this);
    }

    public void navigate(Class<? extends Component> view) {
        ui.access(() -> {
            ui.navigate(view);
        });
    }

    public void vote(Tagline tagline) {
        votes.add(tagline);
    }

    public void removeVote(Tagline tagline) {
        votes.remove(tagline);
    }

    public Set<Tagline> getVotes() {
        return votes;
    }

    public boolean toggleVote(Tagline t) throws Exception {

        if(votes.contains(t)) {
            votes.remove(t);
            votesChangedEvent.fire(new VotesChanged(t, -1));
            return false;
        } else {
            if(votes.size() >= MAXVOTES) {
                throw new Exception("Max votes already used!");
            }
            votes.add(t);
            votesChangedEvent.fire(new VotesChanged(t, 1));
            return true;
        }
    }
}
