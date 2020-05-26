package com.incarcloud.boar.controller;

import com.incarcloud.boar.service.FluxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * WebFlux Test
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

    @GetMapping("/just1")
    public Mono<String> just1() {
        return Mono.just("Hello WebFlux");
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

    @GetMapping("/just2")
    public Flux<String> just2() {
        return Flux.just("Hello", "Web", "Flux");
    }

    @GetMapping("/sse")
    public Flux<ServerSentEvent<Integer>> sse() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> Tuples.of(seq, ThreadLocalRandom.current().nextInt()))
                .map(data -> {
                    ServerSentEvent.Builder<Integer> builder = ServerSentEvent.builder();
                    return builder.event("random")
                            .id(Long.toString(data.getT1()))
                            .data(data.getT2())
                            .build();
                });
    }
}
