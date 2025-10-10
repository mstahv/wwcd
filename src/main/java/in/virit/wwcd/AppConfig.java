package in.virit.wwcd;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;

@Push
@PWA(name = "WWCD", shortName = "What We(b) can Do")
public class AppConfig implements AppShellConfigurator {
}
