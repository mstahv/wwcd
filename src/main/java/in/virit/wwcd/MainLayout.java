package in.virit.wwcd;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import in.virit.wwcd.demoviews.AbstractThing;
import in.virit.wwcd.session.AdminSession;
import in.virit.wwcd.session.AppContext;
import in.virit.wwcd.session.UISession;
import in.virit.wwcd.views.AgendaView;
import in.virit.wwcd.views.QAView;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.util.fullscreen.FullScreen;
import org.vaadin.firitin.util.style.LumoProps;

@StyleSheet("main-layout.css")
@SpringComponent
@UIScope
public class MainLayout extends org.vaadin.firitin.appframework.MainLayout implements AfterNavigationObserver {

    static int layoutCount = 1;
    public PresentationDisplay presentationDisplay = new PresentationDisplay();
    @Autowired
    AppContext appContext;
    @Autowired
    UISession uiSession;
    @Autowired
    AdminSession adminSession;
    Button enterPresentation = new VButton(VaadinIcon.PLAY, this::enterPresentation)
            .withTooltip("Enter presentation mode");
    Button enterFullscreen = new VButton(VaadinIcon.EXPAND_FULL, this::toggleFullscreen)
            .withTooltip("Toggle fullscreen mode");
    Button forward = new VButton(VaadinIcon.FAST_FORWARD) {{
        addClickListener(e -> {
            fordard();
        });
        addClickShortcut(Key.ARROW_RIGHT, KeyModifier.CONTROL, KeyModifier.SHIFT);
    }};

    private void fordard() {
        Component content = getContent();
        if(content instanceof AbstractThing at) {
            UI.getCurrent().navigate(AgendaView.class);
        }
        if(content instanceof AgendaView av) {
            av.pickNext();
        }
    }

    PresentationTimer timer = new PresentationTimer();
    private int layoutId;

    private void toggleFullscreen() {
        FullScreen.isFullscreen().thenAccept(fullscreen -> {
            if (fullscreen) {
                FullScreen.exitFullscreen();
            } else {
                FullScreen.requestFullscreen();
            }
        });
    }

    private void enterPresentation() {
        PasswordPrompt.prompt().thenAccept(pw -> {
            FullScreen.requestFullscreen();
            appContext.present(uiSession, adminSession, pw);
        });
    }

    @Override
    protected Object getDrawerHeader() {
        return new Image("icons/icon-line.png", "WWCD") {{
            setHeight("3em");
            getStyle().setDisplay(Style.Display.BLOCK);
            getStyle().setMarginLeft("auto");
            getStyle().setMarginRight("auto");
            getStyle().setMarginTop("2em");
            getStyle().setMarginBottom("1em");
        }};
    }

    @Override
    protected void addDrawerContent() {
        super.addDrawerContent();
        addToDrawer(
                timer,
                new VHorizontalLayout() {{
                    space();
                    add(enterPresentation, enterFullscreen, forward);
                    space();
                    setPadding(false);
                    setSpacing(false);
                    setWidthFull();
                }});
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        super.afterNavigation(event);
        adjustNavigation();
    }

    private void adjustNavigation() {
        if (appContext.isPresentation()) {
            getMenuScroller().setContent(presentationDisplay);
            if (appContext.getStartOfPresentation() != null) {
                timer.setStartOfView(appContext.getStartOfView());
                timer.setStartTime(appContext.getStartOfPresentation());
                timer.setVisible(true);
            }
        } else {
            getMenuScroller().setContent(getMenu());
            timer.setVisible(false);
        }
//        togglePresentation.setVisible(!(appContext.isPresentation() && !adminSession.isAdmin()));
        presentationDisplay.setEnabled(!(appContext.isPresentation() && !adminSession.isAdmin()));
        forward.setVisible(appContext.isPresentation() && adminSession.isAdmin());
        if (appContext.isPresentation()) {
            presentationDisplay.updateStage();
        }
    }

    public int getLayoutId() {
        if (layoutId != 0) return layoutId;
        this.layoutId = layoutCount++;
        return layoutId;
    }

    class PresentationDisplay extends VerticalLayout {
        PresentationStage lobby = new PresentationStage("1. Intro");
        PresentationStage voting = new PresentationStage("2. Voting");
        PresentationStage agenda = new PresentationStage("3. Agenda") {{
            addClickListener(e -> {
                navigate(AgendaView.class);
            });
        }};
        PresentationStage demos = new PresentationStage("4. Demos");
        PresentationStage qa = new PresentationStage("5. Q&A") {{
            addClickListener(e -> {
                appContext.qa();
                navigate(QAView.class);
            });
        }};

        public PresentationDisplay() {
            add(new H5("Presentation stages"));
            add(lobby, voting, agenda, demos, qa);
        }

        public void updateStage() {
            getStage().activate();
        }

        private PresentationStage getStage() {
            AppContext.AppState state = appContext.getState();
            switch (appContext.getState()) {
                case Normal:
                    return lobby;
                // treat normal as presentation for stage display
                case Presentation:
                    return lobby;
                case Voting:
                    return voting;
                case Agenda:
                    return agenda;
                case Demos:
                    return demos;
                case QA:
                    return qa;
            }
            return null;
        }

        public class PresentationStage extends VButton {
            public PresentationStage(String caption) {
                super(caption);
                addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                addClickListener(e -> {
                });
            }

            public void activate() {
                getStyle().setFontWeight(Style.FontWeight.BOLD);
                PresentationDisplay.this.getChildren().filter(c -> c != PresentationStage.this)
                        .forEach(c -> c.getStyle().setFontWeight(Style.FontWeight.NORMAL));
            }

            @Override
            protected void onAttach(AttachEvent attachEvent) {
                super.onAttach(attachEvent);
                if (adminSession.isAdmin()) {
                    setEnabled(true);
                } else {
                    setEnabled(false);
                    getStyle().setColor(LumoProps.CONTRAST.var());
                }
            }
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        attachEvent.getUI().setPollInterval(1000);
    }
}
