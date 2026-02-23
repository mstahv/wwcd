package in.virit.wwcd.session;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import in.virit.wwcd.Tagline;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.HashSet;
import java.util.Set;

/**
 * A regular UI session, with methods for presentation.
 */
@SpringComponent
@UIScope
public class UISession {
    public static final int MAXVOTES = 10;

    private final UI ui;

    @Autowired
    AppContext context;

    @Autowired
    ApplicationEventPublisher eventPublisher;

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
            eventPublisher.publishEvent(new VotesChanged(t, -1));
            return false;
        } else {
            if(votes.size() >= MAXVOTES) {
                throw new Exception("Max votes already used!");
            }
            votes.add(t);
            eventPublisher.publishEvent(new VotesChanged(t, 1));
            return true;
        }
    }
}
