package in.virit.wwcd.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A simple service class for printing to remote printer devices (in the example built
 * as Raspberry Pi Zero 2W devices).
 *
 * <p>
 *   When a device connects using websocket to this server at {@code /ws/printer}, it gets
 *   registered and then users of this class can print through them. Print jobs are
 *   broadcast as text frames to every connected device.
 * </p>
 */
@Service
public class RemotePrinterService extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(RemotePrinterService.class);

    private final Set<WebSocketSession> printers = new CopyOnWriteArraySet<>();

    /**
     * @return true if there is printers online to accept print jobs
     */
    public boolean printerAvailable() {
        return !printers.isEmpty();
    }

    /**
     * @param stringToPrint string to print to any available printers
     */
    public void print(String stringToPrint) {
        TextMessage message = new TextMessage(stringToPrint);
        for (WebSocketSession session : printers) {
            try {
                synchronized (session) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                log.warn("Failed to send print job to {}, dropping session", session.getId(), e);
                printers.remove(session);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        printers.add(session);
        log.info("Printer registered: {} ({} online)", session.getId(), printers.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        printers.remove(session);
        log.info("Printer unregistered: {} ({} online)", session.getId(), printers.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("Transport error on printer {}", session.getId(), exception);
        printers.remove(session);
    }
}
