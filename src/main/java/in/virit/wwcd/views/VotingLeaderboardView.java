package in.virit.wwcd.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.Tagline;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.appframework.VAppLayout;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.grid.VGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static in.virit.wwcd.session.UISession.MAXVOTES;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class VotingLeaderboardView extends AbstractView {

    private final Div totalVotes = new Div();
    private final AppContext appContext;
    private final UI ui;
    private int votesUsed;
    private TaglineGrid grid = new TaglineGrid();
    private Map<Tagline, Integer> points;

    public VotingLeaderboardView(AppContext appContext, AdminSession adminSession) {
        this.appContext = appContext;
        add(new Emphasis("Vote, vote, vote! Open w.virit.in"));
        addAndExpand(grid);
        calculateVotes();
        this.ui = UI.getCurrent();
        ui.setPollInterval(5000);
        ui.addPollListener(e -> calculateVotes());
    }

    private void calculateVotes() {
        points = appContext.calculatePoints();

        ArrayList<Tagline> taglines = new ArrayList<>();
        for (var t : Tagline.values()) taglines.add(t);
        Collections.shuffle(taglines);
        Collections.sort(taglines, (a, b) -> Integer.compare(points.get(b), points.get(a)));
        grid.setItems(taglines);

        votesUsed = points.values().stream().reduce(0, Integer::sum);
        totalVotes.setText("%s / %s vote(s) used".formatted(votesUsed, MAXVOTES * appContext.getVoters()));

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        findAncestor(VAppLayout.class).addNavbarHelper(totalVotes);
        findAncestor(VAppLayout.class).addNavbarHelper(new DefaultButton("Close voting", () -> appContext.closeVoting()));
    }

    public class TaglineGrid extends VGrid<Tagline> {

        public TaglineGrid() {
            addColumn(t -> t.getTagline());
            addColumn(t -> points.get(t).toString()).setHeader("Votes").setAutoWidth(true).setFlexGrow(0);
            setSizeFull();
        }

        private int votes(Tagline t) {
            int votes = 0;
            return votes;
        }

    }


}
