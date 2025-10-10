package in.virit.wwcd.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.markdown.Markdown;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractView extends VVerticalLayout {

    private UI ui;

    public static final Component md(String markdown) {
        return new MarkdownWithProperLinks(markdown);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        ui = attachEvent.getUI();
        super.onAttach(attachEvent);
        for(Component c : navbarHelpers) {
            findAncestor(MainLayout.class).addNavbarHelper(c);
        }
        navbarHelpers.clear();
    }

    protected UI ui() {
        if(ui == null) {
            ui = UI.getCurrent();
        }
        return ui;
    }

    private List<Component> navbarHelpers = new ArrayList<>();

    public void addNavbarHelper(Component... components) {
        MainLayout layout = findAncestor(MainLayout.class);
        for(Component c : components) {
            if(layout == null) {
                navbarHelpers.add(c);
            } else {
                layout.addNavbarHelper(c);
            }
        }
    }

    protected void image(String imageUrl, String description) {
        image(imageUrl, description, "100%");
    }
    protected void image(String imageUrl, String description, String maxWidth) {
        add(new Image(imageUrl, description) {{
            setMaxWidth(maxWidth);
        }});
        add(md("*" + description + "*"));
    }

    private static class MarkdownWithProperLinks extends Markdown {

        public MarkdownWithProperLinks(String markdown) {
            super(markdown);
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            // add (and router-ignore="") to all links and open external ones in new tab
            getElement().executeJs("""
                        const el = this;
                        setTimeout(() => {
                            const links = el.querySelectorAll('a');
                            links.forEach(link => {
                                link.setAttribute('router-ignore', '');
                                const href = link.getAttribute('href');
                                if (href && (href.startsWith('http://') || href.startsWith('https://'))) {
                                    link.setAttribute('target', '_blank');
                                    link.setAttribute('rel', 'noopener');
                                }
                            });
                        },1000);
                    """);
        }
    }
}
