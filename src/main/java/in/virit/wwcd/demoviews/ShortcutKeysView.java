package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.button.VButton;

@MenuItem(title = "Shortcut Keys", icon = VaadinIcon.KEYBOARD)
@Route(layout = MainLayout.class)
public class ShortcutKeysView extends AbstractThing {

    private final boolean mac;
    private final KeyModifier modifier;
    private final String modifierName;

    public ShortcutKeysView() {
        mac = VaadinSession.getCurrent().getBrowser().isMacOSX();
        modifier = mac ? KeyModifier.META : KeyModifier.CONTROL;
        modifierName = mac ? "Cmd" : "Ctrl";

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
        }) {{
            addClickShortcut(Key.KEY_S, modifier)
                    .setBrowserDefaultAllowed(false);
        }});

        add(md("For example only Firefox allows overriding %s+R".formatted(modifierName)));
        add(new VButton("Reload (%s+R)".formatted(modifierName), VaadinIcon.REFRESH, e -> {
            notify("%s+R: App-level reload triggered!".formatted(modifierName));
        }) {{
            addClickShortcut(Key.KEY_R, modifier)
                    .setBrowserDefaultAllowed(false);
        }});

        add(md("None of the main stream browsers allow web apps to override shortcut associated to opening new window, unless running as **PWA**."));
        add(new VButton("New (%s+N)".formatted(modifierName), VaadinIcon.PLUS, e -> {
            notify("%s+N: New item created!".formatted(modifierName));
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
        Shortcuts.addShortcutListener(this,
                () -> notify("%s+F5 intercepted!".formatted(modifierName)), Key.F5);

        add(md("""
                ### Browser shortcut override test

                Most browser shortcuts can be overridden by web apps, but browsers reserve certain
                critical actions for themselves. This demo registers listeners for all %1$s+letter
                and %1$s+number combinations so you can test which ones your browser allows to
                override — a notification appears when the override succeeds. In a real app,
                developers would design their own shortcut scheme to avoid conflicts.

                #### Protected — avoid these

                | Shortcut | Browser/OS action | Chrome | Safari (Mac) | Firefox | Chrome/Edge (Win) | Firefox (Win) |
                |---|---|---|---|---|---|---|
                | **%1$s+L** | Address bar | **Protected** | **Protected** | **Protected** | Overridable | Overridable |
                | **%1$s+M** | Minimize (Mac OS) | **Protected** | **Protected** | Overridable | Overridable | Overridable |
                | **%1$s+N** | New window | **Protected** | **Protected** | **Protected** | **Protected** | **Protected** |
                | **%1$s+Q** | Quit (Mac) | **Protected** | **Protected** | **Protected** | Overridable | Overridable |
                | **%1$s+R** | Reload | **Protected** | **Protected** | Overridable | Overridable | Overridable |
                | **%1$s+T** | New tab | **Protected** | **Protected** | **Protected** | **Protected** | **Protected** |
                | **%1$s+W** | Close tab | **Protected** | **Protected** | **Protected** | **Protected** | **Protected** |

                #### Commonly used by browser — but overridable

                | Shortcut | Browser action | Chrome | Safari (Mac) | Firefox | Chrome/Edge (Win) | Firefox (Win) |
                |---|---|---|---|---|---|---|
                | **%1$s+A** | Select all | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+C** | Copy | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+D** | Bookmark page | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+F** | Find in page | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+G** | Find next | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+H** | History / Hide (Mac) | Overridable | **Protected** | Overridable | Overridable | Overridable |
                | **%1$s+O** | Open file | Overridable | **Protected** | Overridable | Overridable | Overridable |
                | **%1$s+P** | Print | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+S** | Save page | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+U** | View source | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+V** | Paste | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+X** | Cut | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+Z** | Undo | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+0** | Reset zoom | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **%1$s+1–9** | Switch to tab | Overridable | Overridable | Overridable | Overridable | Overridable |
                | **F5** | Reload | Overridable | N/A (macOS) | Overridable | Overridable | Overridable |

                #### Safest — no common browser binding

                | Shortcut | Chrome | Safari (Mac) | Firefox | Chrome/Edge (Win) | Firefox (Win) |
                |---|---|---|---|---|---|
                | **%1$s+B** | Free | Free | Free | Free | Free |
                | **%1$s+E** | Free | Free | Free | Free | Free |
                | **%1$s+I** | Free | Free | Free | Free | Free |
                | **%1$s+J** | Free | Free | Free | Free | Free |
                | **%1$s+K** | Free | Free | Free | Free | Free |
                | **%1$s+Y** | Free | Free | Free | Free | Free |

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
    }

    /** Registers a shortcut with the platform-appropriate modifier key. */
    private void shortcut(String keyName, Key key) {
        Shortcuts.addShortcutListener(this,
                () -> notify("%s+%s intercepted!".formatted(modifierName, keyName)),
                key, modifier);
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
}
