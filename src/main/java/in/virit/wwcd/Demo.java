package in.virit.wwcd;

import com.vaadin.flow.component.Component;
import in.virit.wwcd.demoviews.AccessOtherHardwareView;
import in.virit.wwcd.demoviews.ClipboardAPIView;
import in.virit.wwcd.demoviews.GeolocationView;
import in.virit.wwcd.demoviews.PreventAutomaticScreenLockView;

public enum Demo {
    Clipboard(ClipboardAPIView.class),
    HardwareAccess(AccessOtherHardwareView.class, true),
    DeviceMotion(in.virit.wwcd.demoviews.DeviceMotionView.class),
    ExecutingRealProgrammingLanguages("Executing real programming languages", in.virit.wwcd.demoviews.ExecuteJavaInTheBrowserView.class),
    Fullscreen("Fullscreen API", in.virit.wwcd.demoviews.FullscreenView.class),
    Geolocation(GeolocationView.class),
    Graphics(in.virit.wwcd.demoviews.GraphicsView.class),
    HardwareAccelerated3DGraphics(in.virit.wwcd.demoviews.HardwareAccelerated3DView.class),
    Notifications(in.virit.wwcd.demoviews.NotificationsView.class),
    PageVisibility(in.virit.wwcd.demoviews.PageVisibilityView.class, true),
    PreventAutomaticScreenLock(PreventAutomaticScreenLockView.class),
    ReadAndWriteFiles(in.virit.wwcd.demoviews.ReadAndWriteFilesView.class),
    ScreenOrientation(in.virit.wwcd.demoviews.ScreenOrientationView.class),
    SelectionAPI(in.virit.wwcd.demoviews.SelectionAPIView.class),
    WebAuthn(in.virit.wwcd.demoviews.WebAuthnView.class),
    PWA(in.virit.wwcd.demoviews.InstallingView.class),
    ;

    private String name;
    private Class<?extends Component> view;
    private boolean preselected = false;

    Demo(String name, Class<? extends Component> view) {
        this.name = name;
        this.view = view;
    }

    Demo(Class<? extends Component> view) {
        this.view = view;
    }
    Demo(String name, Class<? extends Component> view, boolean preselected) {
        this.view = view;
        this.preselected = preselected;
    }
    Demo(Class<? extends Component> view, boolean preselected) {
        this.view = view;
        this.preselected = preselected;
    }

    public String getName() {
        if(name == null) {
            String simpleName = view.getSimpleName();
            simpleName = simpleName.replace("View", "");
            // de-camelcase
            name = simpleName.replaceAll("([a-z])([A-Z])", "$1 $2");
            return name;
        }
        return name;
    }

    public boolean isPreselected() {
        return preselected;
    }

    public Class<? extends Component> getView() {
        return view;
    }
}
