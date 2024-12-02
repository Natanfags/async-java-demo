package com.example.asyncdemo.service.async;

import com.example.asyncdemo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GitHubLookUpService {

    private static final Logger log = LoggerFactory.getLogger(GitHubLookUpService.class);

    private final RestTemplate restTemplate;

    public GitHubLookUpService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        log.info("Looking up " + user);
        String url = String.format("https://api.github.com/users/%s", user);
        User results = restTemplate.getForObject(url, User.class);
        final long randon = ThreadLocalRandom.current().nextLong(1000, 2001);
        System.out.println(randon);
        Thread.sleep(randon);
        return CompletableFuture.completedFuture(results);
    }
}
