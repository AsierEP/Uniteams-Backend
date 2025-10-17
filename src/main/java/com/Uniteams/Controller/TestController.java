package com.Uniteams.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "¡Uniteams Backend funcionando! 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "✅ Servidor activo - Uniteams Backend";
    }

    @PostMapping("/echo")
    public String echo(@RequestBody String message) {
        return "Uniteams recibió: " + message;
    }
}