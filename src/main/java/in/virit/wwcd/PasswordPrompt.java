package in.virit.wwcd;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.PasswordField;

import java.util.concurrent.CompletableFuture;

public class PasswordPrompt extends Dialog {

    private PasswordField passwordField = new PasswordField();

    private PasswordPrompt(CompletableFuture<String> future) {
        setHeaderTitle("Enter Password");
        add(passwordField);
        passwordField.addKeyDownListener(Key.ENTER, e -> {
            future.complete(passwordField.getValue());
            close();
        });
        open();
        passwordField.focus();
    }

    static CompletableFuture<String> prompt() {
        CompletableFuture<String> future = new CompletableFuture<>();
        PasswordPrompt dialog = new PasswordPrompt(future);
        return future;
    }
}
