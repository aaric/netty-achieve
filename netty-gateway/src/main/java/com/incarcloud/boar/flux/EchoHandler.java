package com.incarcloud.boar.flux;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

/**
 * EchoHandler
 *
 * @author Aaric, created on 2020-05-26T10:37.
 * @version 1.4.0-SNAPSHOT
 */
@Component
public class EchoHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive().map(msg -> {
            String payloadText = "[echo] " + msg.getPayloadAsText();
            return session.textMessage(payloadText);
        }));
    }
}
