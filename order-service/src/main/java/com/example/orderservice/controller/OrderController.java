package com.example.orderservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@RestController
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/order")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "fallbackMethod")
    @Retry(name = "productServiceRetry")
    @RateLimiter(name = "productServiceRateLimiter", fallbackMethod = "fallbackMethod")
    public String placeOrder() {
        System.out.println(">>> Calling product service...");
        return restTemplate.getForObject("http://localhost:8081/product", String.class);
    }

    public String fallbackMethod(Throwable t) {
        System.out.println(">>> RateLimiter fallback triggered: " + t.getMessage());
        return "Fallback: Product service is unavailable.";
    }

}
