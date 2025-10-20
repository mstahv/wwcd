package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.textfield.VTextArea;
import org.vaadin.firitin.util.clipboard.CopyToClipboardButton;
import org.vaadin.firitin.util.clipboard.ReadFromClipboardButton;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@MenuItem(title = "Clipboard access", icon = VaadinIcon.COFFEE)
@Route(layout = MainLayout.class) // TODO fixme, why it doesn't work from parent class anymore!?
public class ClipboardAPIView extends AbstractThing {

    public ClipboardAPIView() {
        add(md("""
                Modern browsers support [Clipboard API](https://developer.mozilla.org/en-US/docs/Web/API/Clipboard) 
                to read and write contents to the clipboard. There are some security restrictions and browser 
                differences, but basic operations are achievable with modern browsers.
                """));

        add(new CopyToClipboardButton(() -> "Text content generated at " + LocalTime.now()){{
            setText("Copy generated text to clipboard");
            addClickListener(e -> Notification.show("Copied text to your clipboard, try pasting it somewhere"));
        }});


        TextArea textArea = new VTextArea("Just a text area to test copy/paste") {{
            setWidth("400px");
            setHeight("100px");
        }};

        add(new ReadFromClipboardButton(string -> {
            textArea.setValue(string);
            if(string.contains(";") && string.contains("\n")) {
                // treat as CSV, show as table;
                List<List<String>> cells = new ArrayList<>();
                String[] lines = string.split("\n");
                for (var l : lines) {
                    String[] split = l.split(";");
                    cells.add(Arrays.asList(split));
                }
                var grid = new Grid<List<String>>(){{
                    int cols = cells.get(0).size();
                    for (int i = 0; i < cols; i++) {
                        int finalI = i;
                        addColumn(line -> line.get(finalI)).setHeader("Column " + (i + 1));
                    }
                    setItems(cells);
                }};
                add(grid);
            }
        }) {{
            setText("Handle clipbooard value");
            addClickListener(event -> {
                Notification.show("Your clipboard value was requested and copied to the text area above. Browser" +
                        "might have requested a permission or showed a native menu with 'Paste' option.");
            });
        }});
        add(new Paragraph("The button above reads clipboard value as text and copy it to the text area above (and show as table if it looks like CSV)."));

        add(textArea);

    }
}