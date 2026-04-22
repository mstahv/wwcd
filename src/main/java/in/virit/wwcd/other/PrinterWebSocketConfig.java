package in.virit.wwcd.other;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class PrinterWebSocketConfig implements WebSocketConfigurer {

    private final RemotePrinterService printerService;

    public PrinterWebSocketConfig(RemotePrinterService printerService) {
        this.printerService = printerService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(printerService, "/ws/printer")
                .setAllowedOriginPatterns("*");
    }
}
