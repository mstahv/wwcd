package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import in.virit.wwcd.MainLayout;
import in.virit.wwcd.views.AbstractView;

public abstract class AbstractThing extends AbstractView {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // Show link to source code in GitHub
        String url = "https://github.com/mstahv/wwcd/tree/main/src/main/java/in/virit/wwcd/demoviews/%s.java".formatted(getClass().getSimpleName());
        findAncestor(MainLayout.class).addNavbarHelper(new Anchor(url, VaadinIcon.CODE.create()){{
            setTarget("_blank");
            setRouterIgnore(true);
        }});
        super.onAttach(attachEvent);
    }
}
