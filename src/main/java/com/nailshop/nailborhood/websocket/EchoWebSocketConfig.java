package com.nailshop.nailborhood.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class EchoWebSocketConfig implements WebSocketConfigurer {

    private final EcoHandler ecoHandler;

    public EchoWebSocketConfig(EcoHandler ecoHandler) {
        this.ecoHandler = ecoHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ecoHandler, "/echo")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}

