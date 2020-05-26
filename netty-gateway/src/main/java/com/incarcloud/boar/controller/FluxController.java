package com.incarcloud.boar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * WebFlux Test
 *
 * @author Aaric, created on 2020-05-26T09:29.
 * @version 1.4.0-SNAPSHOT
 */
@RestController
@RequestMapping("/api/flux")
public class FluxController {

    /**
     * 同步非阻塞
     */
    @GetMapping("/mono")
    public Mono<String> mono() {
        return Mono.just("Hello WebFlux");
    }
}
