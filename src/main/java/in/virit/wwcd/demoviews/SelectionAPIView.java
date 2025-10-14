package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.textfield.VTextArea;

@MenuItem(title = "Selection API", icon = VaadinIcon.CURSOR)
@Route(layout = MainLayout.class)
public class SelectionAPIView extends AbstractThing {

    public SelectionAPIView() {
        add(md("""
                The *Selection API* in browsers have a pretty advanced API available for inspecting and modifying text 
                at the cursor point or "selection". In this view we are utilizing that via a Java API in a Vaadin app
                (available as an add-on).
                
                """));

        VTextArea textArea = new VTextArea();
        textArea.setWidthFull();
        textArea.setHeight("200px");
        textArea.setValue("This is an example text area. You can select text here and then use the buttons below to manipulate the selection. Try selecting some text and then click 'Select all' or 'Replace selection with current date'.");
        add(textArea);

        add(new VHorizontalLayout(
                new Button("Select all", e -> {
                    textArea.selectAll();
                }),
                new Button("Uppercase selection", e -> {
                    textArea.getSelectionRange( (selStart, selEnd,  selection) -> {
                        String upperCase = selection.toUpperCase();
                        textArea.setValue(textArea.getValue().substring(0, selStart) + upperCase + textArea.getValue().substring(selEnd));
                    });
                }),
                new Button("Insert date at cursor position (SHIFT-CTRL-D)", e -> {
                    textArea.getSelectionRange((selStart, selEnd,  selection) -> {
                        String currentDate = java.time.LocalDate.now().toString();
                        textArea.setValue(textArea.getValue().substring(0, selStart) + currentDate + textArea.getValue().substring(selEnd));
                        textArea.setCursorPosition(selStart + currentDate.length());
                    });
                }){{
                    addClickShortcut(Key.KEY_D, KeyModifier.SHIFT, KeyModifier.CONTROL);
                }}
        ));


    }
}