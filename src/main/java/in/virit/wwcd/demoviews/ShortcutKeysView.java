package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.html.NativeTable;
import com.vaadin.flow.component.html.NativeTableBody;
import com.vaadin.flow.component.html.NativeTableCell;
import com.vaadin.flow.component.html.NativeTableHeader;
import com.vaadin.flow.component.html.NativeTableHeaderCell;
import com.vaadin.flow.component.html.NativeTableRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import in.virit.wwcd.session.ShortcutTestSession;
import in.virit.wwcd.session.ShortcutTestSession.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.VButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MenuItem(title = "Shortcut Keys", icon = VaadinIcon.KEYBOARD)
@Route(layout = MainLayout.class)
public class ShortcutKeysView extends AbstractThing {

    private final boolean mac;
    private final KeyModifier modifier;
    private final String modifierName;
    private final ShortcutCompatibilityTable table;

    private final ShortcutTestSession testSession;

    public ShortcutKeysView(ShortcutTestSession testSession) {
        this.testSession = testSession;
        mac = VaadinSession.getCurrent().getBrowser().isMacOSX();
        modifier = mac ? KeyModifier.META : KeyModifier.CONTROL;
        modifierName = mac ? "Cmd" : "Ctrl";
        table = new ShortcutCompatibilityTable(modifierName);

        add(md("""
                Keyboard shortcuts are essential UX for apps people use daily — often called shortcut keys,
                hotkeys, or shortcut actions. The web platform allows web apps to listen and react to
                virtually any key combination, and most browser built-in shortcuts can be overridden nowadays,
                although Safari can still be quite picky about some of them.

                When deployed as a **PWA** (Progressive Web App), which is supported on at least macOS and
                Windows, your app runs in its own window without the browser chrome. This means fewer
                collisions with the browser's built-in keyboard shortcuts, giving you even more freedom to
                define the hotkeys that make sense for your application.
                """));

        add(md("""
                ### Button click shortcuts

                The simplest way to add a shortcut in Vaadin. The button handles both click and key press.

                Although browsers use a lot of shortcut keys by themselves, web apps can steal most of them.
                """));

        add(new VButton("Save (%s+S)".formatted(modifierName), VaadinIcon.CHECK, e -> {
            notify("%s+S: Save triggered!".formatted(modifierName));
            markPassed(modifierName + "+S");
        }) {{
            addClickShortcut(Key.KEY_S, modifier)
                    .setBrowserDefaultAllowed(false);
        }});

        add(md("For example only Firefox and Chrome allow overriding %s+R".formatted(modifierName)));
        add(new VButton("Reload (%s+R)".formatted(modifierName), VaadinIcon.REFRESH, e -> {
            notify("%s+R: App-level reload triggered!".formatted(modifierName));
            markPassed(modifierName + "+R");
        }) {{
            addClickShortcut(Key.KEY_R, modifier)
                    .setBrowserDefaultAllowed(false);
        }});

        add(md("None of the main stream browsers allow web apps to override shortcut associated to opening new window, unless running as **PWA**."));
        add(new VButton("New (%s+N)".formatted(modifierName), VaadinIcon.PLUS, e -> {
            notify("%s+N: New item created!".formatted(modifierName));
            markPassed(modifierName + "+N");
        }) {{
            addClickShortcut(Key.KEY_N, modifier)
                    .setBrowserDefaultAllowed(false);
        }});

        // Register listeners for ALL letter and number keys so this page works as a test page.
        // S, N, R are already covered by the buttons above.
        shortcut("A", Key.KEY_A);
        shortcut("B", Key.KEY_B);
        shortcut("C", Key.KEY_C);
        shortcut("D", Key.KEY_D);
        shortcut("E", Key.KEY_E);
        shortcut("F", Key.KEY_F);
        shortcut("G", Key.KEY_G);
        shortcut("H", Key.KEY_H);
        shortcut("I", Key.KEY_I);
        shortcut("J", Key.KEY_J);
        shortcut("K", Key.KEY_K);
        shortcut("L", Key.KEY_L);
        shortcut("M", Key.KEY_M);
        shortcut("O", Key.KEY_O);
        shortcut("P", Key.KEY_P);
        shortcut("Q", Key.KEY_Q);
        shortcut("T", Key.KEY_T);
        shortcut("U", Key.KEY_U);
        shortcut("V", Key.KEY_V);
        shortcut("W", Key.KEY_W);
        shortcut("X", Key.KEY_X);
        shortcut("Y", Key.KEY_Y);
        shortcut("Z", Key.KEY_Z);
        shortcut("0", Key.DIGIT_0);
        shortcut("1", Key.DIGIT_1);
        shortcut("2", Key.DIGIT_2);
        shortcut("3", Key.DIGIT_3);
        shortcut("4", Key.DIGIT_4);
        shortcut("5", Key.DIGIT_5);
        shortcut("6", Key.DIGIT_6);
        shortcut("7", Key.DIGIT_7);
        shortcut("8", Key.DIGIT_8);
        shortcut("9", Key.DIGIT_9);
        fnShortcut("F1", Key.F1);
        fnShortcut("F2", Key.F2);
        fnShortcut("F3", Key.F3);
        fnShortcut("F4", Key.F4);
        fnShortcut("F5", Key.F5);
        fnShortcut("F6", Key.F6);
        fnShortcut("F7", Key.F7);
        fnShortcut("F8", Key.F8);
        fnShortcut("F9", Key.F9);
        fnShortcut("F10", Key.F10);
        fnShortcut("F11", Key.F11);
        fnShortcut("F12", Key.F12);

        add(md("""
                ### Browser shortcut override test

                Most browser shortcuts can be overridden by web apps, but browsers reserve certain
                critical actions for themselves. This demo registers listeners for all %1$s+letter
                and %1$s+number combinations so you can test which ones your browser allows to
                override — a notification appears when the override succeeds. In a real app,
                developers would design their own shortcut scheme to avoid conflicts.

                Try pressing the shortcuts listed below. Successfully intercepted ones get a \u2705.
                Click a cell in the test column to manually mark it as \uD83D\uDEAB (failed) or click
                again to cycle through states. Results are saved across page reloads.
                """.formatted(modifierName)));

        add(table);

        add(md("""
                *The situation is mostly similar on Windows and Linux, but with **Ctrl** instead of **Cmd**.*

                *Some shortcuts like %1$s+M (minimize) and %1$s+H (hide) are reserved by the
                **operating system** rather than the browser — these may be protected even in a PWA.*

                As a **PWA**, the app runs without browser chrome, so most browser-protected shortcuts
                become available for your application.
                """.formatted(modifierName)));

        add(md("""
                ### UI-level shortcuts

                Shortcuts registered on the UI are active globally, regardless of which component is focused.
                Great for application-wide hotkeys.
                """));

        shortcutWithExtra("%s+Shift+F: Global search opened!".formatted(modifierName),
                Key.KEY_F, KeyModifier.SHIFT);
        shortcutWithExtra("%s+Shift+H: Help panel toggled!".formatted(modifierName),
                Key.KEY_H, KeyModifier.SHIFT);

        Shortcuts.addShortcutListener(this,
                () -> notify("Alt+1: Switched to first tab!"),
                Key.DIGIT_1, KeyModifier.ALT);

        Shortcuts.addShortcutListener(this,
                () -> notify("Alt+2: Switched to second tab!"),
                Key.DIGIT_2, KeyModifier.ALT);

        add(md("""
                Try **%1$s+Shift+F** (global search), **%1$s+Shift+H** (help), **Alt+1** / **Alt+2** (tab switching).
                These are component-scoped — active only while this view is attached.
                """.formatted(modifierName)));

        add(md("""
                ### Safe shortcut suggestions

                To avoid conflicts with browser shortcuts, consider these patterns:

                | Shortcut | Typical use | Notes |
                |---|---|---|
                | **%1$s+Shift+letter** | App actions (search, help) | Rarely used by browsers |
                | **Alt+number** | Tab/panel switching | Free in most browsers |
                | **Alt+letter** | Quick actions | Some conflict in Firefox menus |
                | **F2–F9** | Mode toggles, editing | F5=refresh, F12=devtools, rest are mostly free |
                | **Escape** | Close/cancel | Universally understood |

                **Tip:** When your app is installed as a PWA, it runs outside the browser — meaning
                shortcuts like %1$s+T (new tab), %1$s+L (address bar), and even F5 (refresh) become
                available for your app to use freely.
                """.formatted(modifierName)));

        // Restore saved test results from session
        restoreTestResults();
    }

    private void markPassed(String keyDisplay) {
        testSession.markPassed(keyDisplay);
        table.updateTestCell(keyDisplay, TestResult.PASSED);
    }

    private void restoreTestResults() {
        for (var entry : testSession.getResults().entrySet()) {
            table.updateTestCell(entry.getKey(), entry.getValue());
        }
    }

    /** Registers a shortcut with the platform-appropriate modifier key. */
    private void shortcut(String keyName, Key key) {
        Shortcuts.addShortcutListener(this, () -> {
            notify("%s+%s intercepted!".formatted(modifierName, keyName));
            markPassed(modifierName + "+" + keyName);
        }, key, modifier);
    }

    /** Registers a function key shortcut (no modifier). */
    private void fnShortcut(String keyName, Key key) {
        Shortcuts.addShortcutListener(this, () -> {
            notify("%s intercepted!".formatted(keyName));
            markPassed(keyName);
        }, key);
    }

    /** Registers a shortcut with the platform modifier plus extra modifiers. */
    private void shortcutWithExtra(String message, Key key, KeyModifier... extraModifiers) {
        KeyModifier[] modifiers = new KeyModifier[extraModifiers.length + 1];
        modifiers[0] = modifier;
        System.arraycopy(extraModifiers, 0, modifiers, 1, extraModifiers.length);
        Shortcuts.addShortcutListener(this, () -> notify(message), key, modifiers);
    }

    private void notify(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    // --- Shortcut compatibility data model ---

    enum Support {
        PROTECTED("\uD83D\uDEAB", "Protected by browser"),
        OVERRIDABLE("\uD83D\uDC4C", "Overridable"),
        FREE("\uD83D\uDC4D", "Free — no browser binding"),
        NA("\u2014", "Not applicable");

        final String symbol;
        final String tooltip;

        Support(String symbol, String tooltip) {
            this.symbol = symbol;
            this.tooltip = tooltip;
        }
    }

    record ShortcutInfo(String keyFormat, String browserAction,
            Support chromeMac, Support safariMac, Support firefoxMac,
            Support chromeWin, Support firefoxWin) {
    }

    record ShortcutGroup(String label, List<ShortcutInfo> entries) {
    }

    private static final Support P = Support.PROTECTED;
    private static final Support O = Support.OVERRIDABLE;
    private static final Support FR = Support.FREE;
    private static final Support NA = Support.NA;

    private static final List<ShortcutGroup> SHORTCUT_GROUPS = List.of(
            new ShortcutGroup("Protected — avoid these, unless using a PWA", List.of(
                    new ShortcutInfo("%s+L", "Address bar", O, P, P, O, O),
                    new ShortcutInfo("%s+M", "Minimize (Mac OS)", P, P, O, O, O),
                    new ShortcutInfo("%s+N", "New window", P, P, P, P, P),
                    new ShortcutInfo("%s+Q", "Quit (Mac)", P, P, P, O, O),
                    new ShortcutInfo("%s+R", "Reload", O, P, O, O, O),
                    new ShortcutInfo("%s+T", "New tab", P, P, P, P, P),
                    new ShortcutInfo("%s+W", "Close tab", P, P, P, P, P)
            )),
            new ShortcutGroup("Commonly used by browser — but mostly overridable", List.of(
                    new ShortcutInfo("%s+A", "Select all", O, O, O, O, O),
                    new ShortcutInfo("%s+C", "Copy", O, O, O, O, O),
                    new ShortcutInfo("%s+D", "Bookmark page", O, O, O, O, O),
                    new ShortcutInfo("%s+F", "Find in page", O, O, O, O, O),
                    new ShortcutInfo("%s+G", "Find next", O, O, O, O, O),
                    new ShortcutInfo("%s+H", "History / Hide (Mac)", O, P, O, O, O),
                    new ShortcutInfo("%s+O", "Open file", O, P, O, O, O),
                    new ShortcutInfo("%s+P", "Print", O, O, O, O, O),
                    new ShortcutInfo("%s+S", "Save page", O, O, O, O, O),
                    new ShortcutInfo("%s+U", "View source", O, O, O, O, O),
                    new ShortcutInfo("%s+V", "Paste", O, O, O, O, O),
                    new ShortcutInfo("%s+X", "Cut", O, O, O, O, O),
                    new ShortcutInfo("%s+Z", "Undo", O, O, O, O, O),
                    new ShortcutInfo("%s+0", "Reset zoom", O, O, O, O, O),
                    new ShortcutInfo("%s+1", "Switch to tab 1", O, O, O, O, O),
                    new ShortcutInfo("%s+2", "Switch to tab 2", O, O, O, O, O),
                    new ShortcutInfo("%s+3", "Switch to tab 3", O, O, O, O, O),
                    new ShortcutInfo("%s+4", "Switch to tab 4", O, O, O, O, O),
                    new ShortcutInfo("%s+5", "Switch to tab 5", O, O, O, O, O),
                    new ShortcutInfo("%s+6", "Switch to tab 6", O, O, O, O, O),
                    new ShortcutInfo("%s+7", "Switch to tab 7", O, O, O, O, O),
                    new ShortcutInfo("%s+8", "Switch to tab 8", O, O, O, O, O),
                    new ShortcutInfo("%s+9", "Switch to last tab", O, O, O, O, O)
            )),
            new ShortcutGroup("Safest — no known common browser binding", List.of(
                    new ShortcutInfo("%s+B", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("%s+E", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("%s+I", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("%s+J", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("%s+K", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("%s+Y", "", FR, FR, FR, FR, FR)
            )),
            new ShortcutGroup("Function keys (no modifier)", List.of(
                    new ShortcutInfo("F1", "Help", FR, FR, FR, O, O),
                    new ShortcutInfo("F2", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("F3", "Find next", O, FR, O, O, O),
                    new ShortcutInfo("F4", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("F5", "Reload", O, NA, O, O, O),
                    new ShortcutInfo("F6", "Focus address bar", O, O, O, O, O),
                    new ShortcutInfo("F7", "Caret browsing (Firefox)", FR, FR, O, FR, O),
                    new ShortcutInfo("F8", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("F9", "", FR, FR, FR, FR, FR),
                    new ShortcutInfo("F10", "Activate menu bar", FR, FR, FR, O, O),
                    new ShortcutInfo("F11", "Fullscreen / Exposé (Mac)", O, P, O, O, O),
                    new ShortcutInfo("F12", "DevTools", O, FR, O, O, O)
            ))
    );

    // --- Compatibility table component ---

    private class ShortcutCompatibilityTable extends NativeTable {

        private static final int COL_COUNT = 8;
        private final Map<String, NativeTableCell> testCells = new HashMap<>();

        ShortcutCompatibilityTable(String modifierName) {
            addClassName("shortcut-table");

            var thead = new NativeTableHeader();
            var headerRow = new NativeTableRow();
            headerRow.addClassName("header-row");
            for (String col : new String[]{"Shortcut", "Browser/OS action",
                    "Chrome (Mac)", "Safari (Mac)", "Firefox (Mac)",
                    "Chrome/Edge (Win)", "Firefox (Win)", "Your test (click to mark protected)"}) {
                headerRow.add(th(col));
            }
            thead.add(headerRow);
            add(thead);

            var tbody = new NativeTableBody();
            for (var group : SHORTCUT_GROUPS) {
                var groupCell = th(group.label());
                groupCell.getElement().setAttribute("colspan", String.valueOf(COL_COUNT));
                var groupRow = new NativeTableRow();
                groupRow.addClassName("group-header");
                groupRow.add(groupCell);
                tbody.add(groupRow);

                for (var entry : group.entries()) {
                    var row = new NativeTableRow();

                    String keyDisplay = entry.keyFormat().formatted(modifierName);
                    var keyCell = td(keyDisplay);
                    keyCell.addClassName("shortcut-key");
                    row.add(keyCell);

                    row.add(td(entry.browserAction()));

                    for (var support : new Support[]{entry.chromeMac(), entry.safariMac(),
                            entry.firefoxMac(), entry.chromeWin(), entry.firefoxWin()}) {
                        var cell = td(support.symbol);
                        cell.getElement().setAttribute("title", support.tooltip);
                        cell.addClassName("browser-support");
                        cell.addClassName(support.name().toLowerCase());
                        row.add(cell);
                    }

                    var testCell = td("");
                    testCell.addClassName("test-result");
                    testCell.getElement().addEventListener("click", e -> cycleTestResult(keyDisplay, testCell));
                    row.add(testCell);
                    testCells.put(keyDisplay, testCell);

                    tbody.add(row);
                }
            }
            add(tbody);
        }

        private void cycleTestResult(String keyDisplay, NativeTableCell cell) {
            TestResult current = testSession.getResult(keyDisplay);
            if (current == null) {
                testSession.markFailed(keyDisplay);
                applyTestResult(cell, TestResult.FAILED);
            } else if (current == TestResult.FAILED) {
                testSession.clear(keyDisplay);
                applyTestResult(cell, null);
            } else {
                testSession.markFailed(keyDisplay);
                applyTestResult(cell, TestResult.FAILED);
            }
        }

        void updateTestCell(String keyDisplay, TestResult result) {
            var cell = testCells.get(keyDisplay);
            if (cell != null) {
                applyTestResult(cell, result);
            }
        }

        private void applyTestResult(NativeTableCell cell, TestResult result) {
            cell.removeClassName("tested");
            cell.removeClassName("failed");
            if (result == null) {
                cell.setText("");
            } else if (result == TestResult.PASSED) {
                cell.setText("\u2705");
                cell.addClassName("tested");
            } else {
                cell.setText("\uD83D\uDEAB");
                cell.addClassName("failed");
            }
        }

        private static NativeTableHeaderCell th(String text) {
            var cell = new NativeTableHeaderCell();
            cell.setText(text);
            return cell;
        }

        private static NativeTableCell td(String text) {
            var cell = new NativeTableCell();
            cell.setText(text);
            return cell;
        }
    }
}
