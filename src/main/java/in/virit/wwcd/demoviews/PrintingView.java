package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import in.virit.dymo.DymoLetraTag200B;
import in.virit.wwcd.other.RemotePrinterService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.PrintPdfButton;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import org.vaadin.firitin.util.VStyleUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@MenuItem(title = "Print", icon = VaadinIcon.PASSWORD)
@Route(layout = MainLayout.class)
public class PrintingView extends AbstractThing {

    public PrintingView(RemotePrinterService remotePrinterService) {
        add(new H1("Tricks for printing from single-page web apps"));

        String bodyText = """
                Three typical approaches: tune the live DOM with a `@media print` stylesheet,
                generate a PDF on the server, or push jobs to a service-connected printer.
                Try `Ctrl/Cmd-P` on this page — the `@media print` block further down hides
                the drawer, navbar and anything tagged `.noprint`.
                """;

        add(md(bodyText));

        add(new Button("Open the browser print dialog (this button has .noprint, so it won't appear in the output)") {{
            // See css rule below, ignore when printing using media query
            addClassName("noprint");

            getElement().executeJs("""
                        // opens print the OS print dialog
                        this.onclick = () => window.print();
                    
                        // CSS with Aura & vaadin-app-layout is too complicated with its animations,
                        // complement CSS rules from JS event to help hiding the drawer properly, 
                        // this is not needed for Lumo
                        window.addEventListener("beforeprint", (event) => {
                            console.log("Before print");
                            document.querySelector("vaadin-app-layout").shadowRoot.querySelector("[content]").style.paddingLeft = "0";
                        });
                        window.addEventListener("afterprint", (event) => {
                          console.log("After print");
                          // reset the hardcoded value
                          document.querySelector("vaadin-app-layout").shadowRoot.querySelector("[content]").style.paddingLeft = "";
                        });
                    
                    """);
        }});

        // Typically you put this to your styles.css, here we hide the drawer and tune the
        // non-print optimized vaadin-app-layout for better printing and special class
        // for components that developer don't want to be printed
        VStyleUtil.injectAsFirst("""
                        @media print {
                            /* hide components with noprint class name */
                            .noprint {
                                display: none;
                            }
                
                            /* Hide navigation (drawer) and navbar by default, usually useless when printing and disturbs print layout */
                            vaadin-app-layout::part(navbar),
                            vaadin-app-layout::part(drawer) {
                                display: none;
                            }
                            /* App layout does some dynamic calculations that are not re-evaluated by browsers
                               -> force padding to 0 to make sure no empty space is reserved for hidden drawer.
                               Also disable overflow auto/height from the element -> make all content printed.
                            */
                            vaadin-app-layout {
                                padding-left: 0px;
                                overflow: visible;
                                height: auto;
                            }
                            
                            /* You might also e.g. want to remove e.g. certain backgound images */
                            html,
                            vaadin-app-layout vaadin-vertical-layout {
                                background:none !important;
                                border:none !important;
                            }
                
                            /* These may be visible if you are running the demo in development mode */ 
                            copilot-main, vaadin-dev-tools {
                                display:none;
                            }
                
                        }
                
                """);


        add(new H3("Print an optimized view"));

        add(md("""
        Often the cleanest fix is to render a second view tailored for paper. Framework code
        is usually clearer than a tangle of print CSS, and some components — virtualized grids,
        certain kinds of charts, video — simply don't "paginate".
        """));

        add(new PrintOptimizedCard(false));

        add(new H3("As PDF"));

        add(md("""
        A server-generated PDF gives full control over page size, fonts and colors — the
        standard for anything that has to look the same everywhere. The easiest integration
        is a plain download link; users print or archive the file as they prefer.
        """));

        // This anchor just downloads the PDF. People know how to proceed and
        // these days quite often they actually just prefer saving it for their
        // digital archives as PDF files. My suggestion is to only provide this.
        add(new Anchor() {{
            setText("Download 'Hello world' as PDF");
            setDownload(true);
            setHref(download -> {
                download.setContentType("application/pdf");
                writePDF("Hello world from PDF!", download.getOutputStream());
            });
        }});

        add(md("""
        The hidden-iframe trick below pipes a freshly generated PDF into an offscreen iframe
        and calls `window.print()` on it — snappy, desktop-like UX. Downside: users who only
        want to archive the file have to go through the OS dialog's "Save as PDF".
        """));

        // Or if you really want to trigger printing directly, it can be done with a classical
        // open in hidden iframe and then print hack. This is packaged as a re-usable component
        // in the Viritin add-on
        add(new PrintPdfButton(outputStream -> {
            writePDF("This is directly printed PDF", outputStream);
        }));

        add(new H3("Skipping the OS print dialog entirely"));

        add(md("""
        To bypass the OS dialog — useful for automation or remote destinations, e.g. *a hotel
        self check-in kiosk dropping a receipt on a lobby printer* — the job has to reach the
        device some other way.

        Chromium-based browsers can talk to some Bluetooth LE printers directly via the Web
        Bluetooth API, which is handy for handheld label printers. This was recently covered
        in a [vibe-coded Point of Sale demo for Vaadin](https://github.com/vaadin/point-of-sales-ai-demo/tree/main).
        If you happen to own a Dymo LetraTag BT device and are viewing this from a Chromium-based
        browser, you can try printing to your own device with the form below.
        """));

        var name = new VTextField("Text");
        name.setValue("Vaadin");

        var printBtn = new Button(){{
            setText("Print using Dymo LetraTag 200B");
            // Hidden proxy to BLE printer, needs to be added to component tree
            var printer = new DymoLetraTag200B();
            PrintingView.this.add(printer);
            addClickListener(e -> {
                printer.print("}> " + name.getValue());
            });
        }};
        add(new HorizontalFloatLayout(name, printBtn));

        add(md("""
        More commonly the target is reachable from the server: options range from shelling out
        to `lp` with a generated PDF (needs the printer configured on the host) to driving a
        CUPS server with [cups4j](https://github.com/harwey/cups4j). Some advanced office
        printers expose a CUPS endpoint themselves, so no intermediate print server is required
        — just some firewall configuration.

        This demo takes a fully custom route: a WebSocket endpoint at `/ws/printer` that
        Raspberry Pi-based "print servers" connect to and receive print jobs as text frames.
        The Raspberry Pi example server then drives the same Dymo LetraTag Bluetooth printer
        used above. TODO add links to the Raspberry Pi example.
        """));

        Button dymoLetraPrinter = new Button("Print a timestamp label via the remote service") {{
            addClickListener(e -> {
                if(remotePrinterService.printerAvailable()) {
                    remotePrinterService.print(LocalDateTime.now() + " @ server time");
                    Notification.show("Print job sent to printer!");
                } else {
                    Notification.show("Printer offline");
                }
            });
            setEnabled(remotePrinterService.printerAvailable());
        }};
        add(dymoLetraPrinter);

        if(!remotePrinterService.printerAvailable()) {
            add(new Emphasis("No remote printers online currently."));
            add(new Button("Refresh", event -> {
                dymoLetraPrinter.setEnabled(remotePrinterService.printerAvailable());
            }));
        }

    }

    private static void writePDF(String content, OutputStream out) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.newLineAtOffset(20,700);
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 12);
        contentStream.showText(content);
        contentStream.endText();
        contentStream.close();
        document.save(out);
        document.close();
    }

}