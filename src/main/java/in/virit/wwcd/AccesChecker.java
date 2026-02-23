package in.virit.wwcd;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import in.virit.wwcd.demoviews.AbstractThing;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class AccesChecker implements VaadinServiceInitListener {

    @Autowired
    AppContext appContext;

    @Autowired
    AdminSession adminSession;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(init ->
            init.getUI().addBeforeEnterListener(this::beforeEnter));
    }

    private void beforeEnter(BeforeEnterEvent event) {
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
}
