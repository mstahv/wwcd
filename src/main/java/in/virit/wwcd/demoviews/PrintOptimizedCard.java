package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeTable;
import com.vaadin.flow.component.html.NativeTableCell;
import com.vaadin.flow.component.html.NativeTableHeaderCell;
import com.vaadin.flow.component.html.NativeTableRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import org.vaadin.firitin.components.button.VButton;

import java.util.List;
import java.util.stream.IntStream;

/**
 * A demo card that is used both as part of a standard UI and with slight modifications
 * as a printable version of the same data (Grid -> html table for tabular demo data).
 * <p>
 * Note, this print optimized view is omitted from main navigation on purpose and
 * does NOT use the main layout other views do.
 * </p>
 */
@Route( /*layout = MainLayout.class*/)
public class PrintOptimizedCard extends Card {

    List<String> stringList = IntStream.range(1, 100).mapToObj(i -> "Row " + i).toList();

    /**
     * Default constructor for "routing"
     */
    public PrintOptimizedCard() {
        this(true);
    }

    /**
     * A constructor if used in basic UI
     * @param optimizeForPrinting true if this instance is to be printed, false if note (e.g. print button
     *                            then visible)
     */
    public PrintOptimizedCard(boolean optimizeForPrinting) {
        setTitle("Printing a virtualized Grid");
        setSubtitle("Grid only renders the rows currently in the viewport, so a naive print job "
                + "drops the rest. The Print button opens a dedicated route that renders every "
                + "row as a plain <table> and strips the app chrome before calling window.print().");

        // Add print button used in standard UI, not if creating version for printing
        if(!optimizeForPrinting) {
            setHeaderSuffix(new PrintRouteButton(PrintOptimizedCard.class));
        }

        if(!optimizeForPrinting) {
            // Simple Grid is best for UX
            add(new Grid<String>() {{
                setHeight("10em");
                setItems(stringList);
                addColumn(s -> s).setHeader("Str");
                addColumn(s -> s.length()).setHeader("Str lenght");
            }});
            setWidth("30em");
        } else {
            // use basic html table for printed version and show all lines at once
            add(new NativeTable(){{
                add(new NativeTableRow(){{
                    add(new NativeTableHeaderCell("Str"));
                    add(new NativeTableHeaderCell("Str lenght"));
                }});
                for(String str : stringList) {
                    add(new NativeTableRow(){{
                        add(new NativeTableCell(str));
                        add(new NativeTableCell(str.length()+""));
                    }});
                }
            }});

            /* You can also combine some CSS, could also load some other css file
               This app loads Aura theme in MainLayout, so this print version has only
               "base styles" that are easy to override.
            */
            getStyle().set("font-family", "Helvetica");
            getStyle().set("--vaadin-card-border-width", "0");
            getStyle().set("--vaadin-card-background", "none");

        }
    }

    /**
     * A class that wraps the infamous print hidden iframe hack for a Vaadin view
     * (optimized for printing).
     */
    public static class PrintRouteButton extends VButton {
        public PrintRouteButton(Class<? extends Component> routeClass) {
            setIcon(VaadinIcon.PRINT.create());
            addClickListener(e -> {
                var iframe = new Element("iframe");
                iframe.getStyle().setDisplay(Style.Display.NONE);
                String printUrl = RouteConfiguration.forSessionScope().getUrl(routeClass);
                iframe.setAttribute("src", printUrl);
                getElement().appendChild(iframe);
                iframe.executeJs("""
                    const iframe = this;
                    iframe.onload = function() {
                        setTimeout(function() {
                            iframe.focus();
                            iframe.contentWindow.print();
                        }, 500);
                    };
                    """);
            });
        }
    }
}