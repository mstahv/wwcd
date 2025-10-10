package in.virit.wwcd;

public enum Tagline {
    SerialPort("Read data through serial port", Demo.HardwareAccess),
    Bluetooth("Plot ECG through bluetooth heart rate monitor", Demo.HardwareAccess),
    Print("Print documents", Demo.HardwareAccess),
    ClipboardWrite("Write to clipboard", Demo.Clipboard),
    ClipboardRead("Access clipboard with multiple data types", Demo.Clipboard),
    GeoSpeed("Read the speed over ground of your browser", Demo.Geolocation),
    Geo("Get the location of user", Demo.Geolocation),
    GeoHeading("Know where you user is heading", Demo.Geolocation),
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
