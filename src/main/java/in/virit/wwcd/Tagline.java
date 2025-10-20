package in.virit.wwcd;

public enum Tagline {
    DrawVectorGraphics("Draw stunning graphics programmatically.", Demo.Graphics),
    DomGraphics("Build interactive components with vector graphics.", Demo.Graphics),
    WebGLGraphics("Render hardware-accelerated 3D graphics.", Demo.HardwareAccelerated3DGraphics),
    SerialPort("Read data directly from serial ports.", Demo.HardwareAccess),
    Bluetooth("Visualize ECG data via Bluetooth heart rate monitor.", Demo.HardwareAccess),
    Bluetooth2("Connect and communicate with local Bluetooth devices.", Demo.HardwareAccess),
    Print("Print documents directly from your web app.", Demo.HardwareAccess),
    ClipboardWrite("Copy values to clipboard seamlessly.", Demo.Clipboard),
    ClipboardRead("Access clipboard with multiple data types.", Demo.Clipboard),
    DeviceMotion("Build input UI using accelerometer and gyroscope data.", Demo.DeviceMotion),
    DeviceMotion2("Create games that react to your phone's rotation.", Demo.DeviceMotion),
    ExecuteJava("Run Java directly in the browser.", Demo.ExecutingRealProgrammingLanguages),
    ExecuteJava2("Execute JVM inside the browser sandbox.", Demo.ExecutingRealProgrammingLanguages),
    Fullscreen1("Scale any HTML element to fill the entire screen.", Demo.Fullscreen),
    Fullscreen2("Go fullscreen with a single API call.", Demo.Fullscreen),
    GeoSpeed("Track your browser's speed over ground.", Demo.Geolocation),
    Geo("Pinpoint your user's location instantly.", Demo.Geolocation),
    GeoHeading("Know where your user is heading.", Demo.Geolocation),
    Install("Add your web app to the desktop application switcher.", Demo.PWA),
    Install2("Install to home screen like a native app.", Demo.PWA),
    Notification1("Engage users with notifications, even when the browser is closed.", Demo.Notifications),
    Visibility("Detect when your app is actively used.", Demo.PageVisibility),
    Files("Read and manipulate file portions directly in the browser.", Demo.ReadAndWriteFiles),
    Files2("Write directly to the local filesystem.", Demo.ReadAndWriteFiles),
    Orientation("Detect screen orientation changes instantly.", Demo.ScreenOrientation),
    Orientation2("Respond dynamically to screen rotation.", Demo.ScreenOrientation),
    Orientation3("Lock screen orientation to your preferred angle.", Demo.ScreenOrientation),
    Selection1("Replace selected text programmatically.", Demo.SelectionAPI),
    Selection2("Insert custom content at the cursor position.", Demo.SelectionAPI),
    Selection3("Capture and use the current text selection.", Demo.SelectionAPI),
    WakeLock("Keep devices awake during critical operations.", Demo.PreventAutomaticScreenLock),
    WebAuthn1("Authenticate with face or fingerprint recognition.", Demo.WebAuthn),
    WebAuthn2("Enforce strong authentication beyond weak passwords.", Demo.WebAuthn),
    WebAuthn3("Protect users with modern, secure authentication.", Demo.WebAuthn),
    ;

    private String tagline;
    private Demo demo;

    Tagline(String tagline, Demo demo) {
        this.tagline = tagline;
        this.demo = demo;
    }

    public String getTagline() {
        return tagline;
    }

    public Demo getDemo() {
        return demo;
    }
}
