package com.incarcloud.boar.flux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

/**
 * FluxController
 *
 * @author Aaric, created on 2020-05-26T09:29.
 * @version 1.4.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/api/flux")
public class FluxController {

    @Autowired
    private FluxService fluxService;

    /**
     * Mono：实现Publisher并返回0或1个元素
     */
    @GetMapping("/just1")
    public Mono<String> just1() {
        return Mono.just("Hello WebFlux");
    }

    /**
     * Flux：实现Publisher并返回N个元素
     */
    @GetMapping("/just2")
    public Flux<String> just2() {
        return Flux.just("Hello", "Web", "Flux");
    }

    @GetMapping("/sub")
    public Mono<Object> sub() {
        return Mono.create(sink -> {
            log.debug("sink: {}", sink);
            sink.success("Hello WebFlux");
        }).doOnSubscribe(sub -> {
            log.info("sub: {}", sub);
        }).doOnNext(o -> {
            log.debug("o: {}", o);
        });
    }

    @GetMapping(value = "/sse1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse1() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(data -> "-> " + data);
    }

    @GetMapping("/sse2")
    public Flux<ServerSentEvent<String>> sse2() {
        /*Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        Flux<ServerSentEvent<String>> event = flux.map(data -> {
            return ServerSentEvent.<String>builder()
                    .id(UUID.randomUUID().toString())
                    .event("random")
                    .data(data.toString())
                    .build();
        });*/
        return Flux.interval(Duration.ofSeconds(1)).map(data ->
                ServerSentEvent.<String>builder()
                        .id(UUID.randomUUID().toString())
                        .event("random")
                        .data(data.toString())
                        .build()
        );
    }
}
