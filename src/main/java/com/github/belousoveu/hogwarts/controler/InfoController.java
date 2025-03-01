package com.github.belousoveu.hogwarts.controler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class InfoController {

    @Value("${server.port}")
    private int port;

    @GetMapping("/port")
    public String getPort() {
        return String.valueOf(port);
    }

    @GetMapping("/sum")
    public int sum() {
        return Stream.iterate(1, a -> a +1).limit(1_000_000).parallel().reduce(0, Integer::sum);
    }
}
