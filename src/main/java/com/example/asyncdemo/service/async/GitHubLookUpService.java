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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GitHubLookUpService {

    private static final Logger log = LoggerFactory.getLogger(GitHubLookUpService.class);
    private static final String GITHUB_USER_API_URL = "https://api.github.com/users/%s";

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    public GitHubLookUpService(RestTemplateBuilder restTemplateBuilder, WebClient.Builder webClient) {
        this.restTemplate = restTemplateBuilder.build();
        this.webClient = webClient.build();
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
            log.error("Erro ao buscar user " + user, e);
            throw new UserNotFoundException("User não encontrado: " + user, e);
        }
    }

    @Async
    public CompletableFuture<String> findBlog(String blogUrl) {

        log.info("Fetching blog content from " + blogUrl);

        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 2001));
            return CompletableFuture.completedFuture(blogUrl);
        } catch (InterruptedException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<String> checkUrl(String blogUrl) {

        log.info("Checking ulr " + blogUrl);

        return this.check(blogUrl.trim()).thenApply(result -> {
            if (result) {
                return "Blog está disponível!";
            } else {
                return "Blog não está disponível!";
            }
        }).exceptionally(ex -> {
            log.error("Erro ao verificar o URL", ex);
            return "Erro ao verificar o blog!";
        });
    }

    private CompletableFuture<Boolean> check(String url) {

        return webClient.options()
                .uri(url)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful()).toFuture();
    }
}
