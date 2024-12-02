package com.example.asyncdemo.service.async;

import com.example.asyncdemo.User;
import com.example.asyncdemo.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GitHubLookUpService {

    private static final Logger log = LoggerFactory.getLogger(GitHubLookUpService.class);
    private static final String GITHUB_USER_API_URL = "https://api.github.com/users/%s";

    private final RestTemplate restTemplate;

    public GitHubLookUpService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<User> findUser(String user) {

        log.info("Looking up " + user);

        try {
            String url = String.format(GITHUB_USER_API_URL, user);
            User results = restTemplate.getForObject(url, User.class);
            final long randon = ThreadLocalRandom.current().nextLong(1000, 2001);
            System.out.println(randon);
            Thread.sleep(randon);
            return CompletableFuture.completedFuture(results);
        } catch (RestClientException | InterruptedException e) {
            log.error("Erro ao buscar user  " + user, e);
            throw new UserNotFoundException("User não encontrado: " + user, e);
        }
    }
}
