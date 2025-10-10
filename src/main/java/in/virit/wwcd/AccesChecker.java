package in.virit.wwcd;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import in.virit.wwcd.demoviews.AbstractThing;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import in.virit.wwcd.session.UISession;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@VaadinServiceScoped
public class AccesChecker {

    @Inject
    AppContext appContext;

    @Inject
    AdminSession adminSession;

    @Inject
    UISession uiSession;

    public void beforeEnter(@Observes BeforeEnterEvent event) {
        System.out.println("Before enter, mode" + appContext.getState());
        if(adminSession.isAdmin()) {
            System.out.println("Admin access to " + event.getNavigationTarget().getSimpleName());
            return;
        }
        if(appContext.isNormal() && AbstractThing.class.isAssignableFrom(event.getNavigationTarget())) {
            System.out.println("Normal view, normal mode");
            return;
        }

        if(event.getNavigationTarget() == appContext.getCurrentView()) {
            System.out.println("Navigating during presentation to current view");
            return;
        }

       // presentation or some other non-active and trying to navigate on some "wrong view"
        System.out.println("presentation or some other non-active and trying to navigate on some \"wrong view\"");
        Class<? extends Component> currentView = appContext.getCurrentView();
        System.out.println("CV:" + currentView);
        System.out.println("CV:" + event.getNavigationTarget());
        if(currentView != null) {
            // presentation view
            event.forwardTo(currentView);
        } else {
            // Go home
            event.forwardTo(MainView.class);
        }
    }

    public void register(@Observes UIInitEvent uiInitEvent) {
        System.out.println("UI init event, App state" + appContext.getState());
        // Not needed as we use CDI event observers 😎
        //uiInitEvent.getUI().addBeforeEnterListener(this);
    }
}
