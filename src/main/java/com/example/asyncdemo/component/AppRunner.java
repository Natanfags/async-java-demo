package com.example.asyncdemo.component;

import com.example.asyncdemo.service.async.GitHubLookUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AppRunner.class);

    private final GitHubLookUpService gitHubLookUpService;

    public AppRunner(GitHubLookUpService gitHubLookUpService) {
        this.gitHubLookUpService = gitHubLookUpService;
    }

    @Override
    public void run(String... args) throws Exception {

        // desligado devido a consulta agora ser via API

        /*long start = System.currentTimeMillis();

        CompletableFuture<User> page1 = gitHubLookUpService.findUser("PivotalSoftware");
        CompletableFuture<User> page2 = gitHubLookUpService.findUser("CloudFoundry");
        CompletableFuture<User> page3 = gitHubLookUpService.findUser("Spring-Projects");

        CompletableFuture.allOf(page1, page2, page3).join();

        log.info("Tempo decorrido: " + (System.currentTimeMillis() - start));
        log.info("--> " + page1.get());
        log.info("--> " + page2.get());
        log.info("--> " + page3.get());*/
    }
}
