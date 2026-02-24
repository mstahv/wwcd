package in.virit.wwcd;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.aura.Aura;

@StyleSheet(Aura.STYLESHEET)
@PWA(name = "What We(b) can Do", shortName = "WWCD")
@Push
public class AppConfig implements AppShellConfigurator {
}
