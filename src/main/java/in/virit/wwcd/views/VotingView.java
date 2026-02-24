package in.virit.wwcd.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import in.virit.color.Color;
import in.virit.color.NamedColor;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.Tagline;
import in.virit.wwcd.session.AppContext;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.appframework.VAppLayout;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.grid.VGrid;

import java.util.ArrayList;
import java.util.Collections;

import static in.virit.wwcd.session.UISession.MAXVOTES;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class VotingView extends AbstractView {

    private final UISession uiSession;
    private final Div totalVotes = new Div();
    private int votesUsed;

    public VotingView(UISession uiSession, AppContext appContext) {
        this.uiSession = uiSession;
        appContext.registerVoter();
        add(new Emphasis("Let's plan the session! You have total of 10 votes you can assign to taglines, off which we'll cover as many as we can! Note, some taglines may cover same feature/demo."));
        addAndExpand(new TaglineGrid());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        findAncestor(VAppLayout.class).addNavbarHelper(totalVotes);
        updateVotes();
    }

    private void updateVotes() {
        votesUsed = uiSession.getVotes().size();
        totalVotes.setText("%s / %s vote(s) used".formatted(votesUsed, MAXVOTES));
    }

    public class TaglineGrid extends VGrid<Tagline> {

        public TaglineGrid() {
            addColumn(t -> t.getTagline());
            addComponentColumn(t -> new VotingComponent(t)).setAutoWidth(true).setFlexGrow(0);
            ArrayList<Tagline> taglines = new ArrayList<>();
            for(Tagline t : Tagline.values()) {
                taglines.add(t);
            }
            Collections.shuffle(taglines);
            setItems(taglines);
            setSizeFull();
        }

        public class VotingComponent extends VButton {
            public VotingComponent(Tagline t) {
                super(VaadinIcon.THUMBS_UP);
                assignColor(uiSession.getVotes().contains(t));
                addClickListener(() -> {
                    try {
                        boolean voted = uiSession.toggleVote(t);
                        assignColor(voted);
                    } catch (Exception e) {
                        Notification.show(e.getMessage());
                    }
                    updateVotes();
                });
            }

            private void assignColor(boolean voted) {
                Color color = voted ? NamedColor.PURPLE : NamedColor.BLACK;
                getStyle().setColor(color);
            }
        }
    }

}
