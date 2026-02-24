package in.virit.wwcd;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

import java.util.concurrent.CompletableFuture;

public class PasswordPrompt extends Dialog {

    public record Result(String password, boolean spectatorMode) {}

    private PasswordField passwordField = new PasswordField();
    private Checkbox spectatorCheckbox = new Checkbox("Spectator mode (audience follows screen)", true);

    private PasswordPrompt(CompletableFuture<Result> future) {
        setHeaderTitle("Enter Password");
        add(new VerticalLayout(passwordField, spectatorCheckbox));
        passwordField.addKeyDownListener(Key.ENTER, e -> {
            future.complete(new Result(passwordField.getValue(), spectatorCheckbox.getValue()));
            close();
        });
        open();
        passwordField.focus();
    }

    static CompletableFuture<Result> prompt() {
        CompletableFuture<Result> future = new CompletableFuture<>();
        new PasswordPrompt(future);
        return future;
    }
}
