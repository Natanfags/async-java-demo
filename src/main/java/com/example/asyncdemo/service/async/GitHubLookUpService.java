package com.example.asyncdemo.service.async;

import com.example.asyncdemo.User;
import com.example.asyncdemo.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GitHubLookUpService {

    private static final Logger log = LoggerFactory.getLogger(GitHubLookUpService.class);

    @Value("${github.user.api.url}")
    private String githubUserApiUrl;

    private final RestTemplate restTemplate;
    private final ExecutorService threadPool;

    public GitHubLookUpService(RestTemplateBuilder restTemplateBuilder, @Qualifier("limitedThreadPool") ExecutorService threadPool) {
        this.restTemplate = restTemplateBuilder.build();
        this.threadPool = threadPool;
    }

    @Async
    public CompletableFuture<User> findUser(String user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Looking up " + user);
                String url = String.format(githubUserApiUrl, user);
                User results = restTemplate.getForObject(url, User.class);
                Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 2001));
                return results;
            } catch (RestClientException | InterruptedException e) {
                log.error("Erro ao buscar user  " + user, e);
                throw new UserNotFoundException("User não encontrado: " + user, e);
            }
        }, threadPool);
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
    public CompletableFuture<String> checkIfBlogIsOnline(String blogUrl) {
        return CompletableFuture.supplyAsync(() -> {
            if (blogUrl == null || blogUrl.trim().isEmpty()) {
                return "Blog URL is empty";
            }
            try {
                ResponseEntity<String> response = restTemplate.exchange(blogUrl, HttpMethod.HEAD, null, String.class);
                System.out.println(response.getStatusCode());
                return response.getStatusCode().is2xxSuccessful() ? "Blog is online" : "Blog is offline";
            } catch (Exception e) {
                return "Error accessing blog: " + e.getMessage();
            }
        }, threadPool);
    }
}
