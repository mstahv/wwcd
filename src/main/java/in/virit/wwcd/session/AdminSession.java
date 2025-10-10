package in.virit.wwcd.session;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import com.vaadin.quarkus.annotation.VaadinSessionScoped;

/**
 * A regular UI session, with methods for presentation.
 */
@VaadinSessionScoped
public class AdminSession {

    private boolean admin;

    public AdminSession() {
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
