package in.virit.wwcd.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.Tagline;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.html.VSpan;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(layout = MainLayout.class)
@MenuItem(hidden = true)
public class AgendaView extends VVerticalLayout {

    private final AppContext appContext;
    private final AdminSession adminSession;
    private DemoGrid grid = new DemoGrid();

    public AgendaView(AppContext appContext, AdminSession adminSession) {
        this.appContext = appContext;
        this.adminSession = adminSession;
        addAndExpand(grid);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        grid.setItems(appContext.getAgenda());
    }

    public void pickNext() {
        grid.getListDataView().getItems().filter(a -> !a.presented().get()).findFirst().ifPresent(next -> {
            appContext.showDemo(next);
            navigate(next.demo().getView());
        });
    }

    public class DemoGrid extends VGrid<AppContext.AgendaItem> {

        public DemoGrid() {
            addComponentColumn(t -> {
                String presentationName = t.demo().getName();
                if(adminSession.isAdmin()) {
                    Button button = new VButton(presentationName){{
                        addThemeVariants(ButtonVariant.AURA_TERTIARY);
                        if(t.presented().get()) {
                            getStyle().setColor(NamedColor.DARKOLIVEGREEN);
                        }
                        addClickListener(e -> {
                            appContext.showDemo(t);
                            navigate(t.demo().getView());
                        });
                    }};
                    return button;
                } else {
                    return new VSpan(presentationName) {{
                        if(t.presented().get()) {
                            getStyle().setColor(NamedColor.DARKOLIVEGREEN);
                        }
                    }};
                }
            });
            addColumn(t -> t.points()).setHeader("Votes").setAutoWidth(true).setFlexGrow(0);
            addComponentColumn(t -> t.demo().isPreselected() ? VaadinIcon.CHECK.create() : new Span()).setHeader("Preselected").setAutoWidth(true).setFlexGrow(0);
            addComponentColumn(t -> t.presented().get() ? VaadinIcon.CHECK.create() : new Span()).setHeader("Presented").setAutoWidth(true).setFlexGrow(0);
            setSizeFull();
        }

        private int votes(Tagline t) {
            int votes = 0;
            return votes;
        }

    }

}
