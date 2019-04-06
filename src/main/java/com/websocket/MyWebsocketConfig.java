package com.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Component
public class MyWebsocketConfig implements WebSocketConfigurer {

    @Autowired
    public MyTextWebSocketHandler myTextWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(myTextWebSocketHandler, "/my-websocket-endpoint").setAllowedOrigins("*");
    }
}
