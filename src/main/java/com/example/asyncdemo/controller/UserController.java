package com.example.asyncdemo.controller;

import com.example.asyncdemo.User;
import com.example.asyncdemo.exception.UserNotFoundException;
import com.example.asyncdemo.service.async.GitHubLookUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final GitHubLookUpService lookUpService;

    public UserController(GitHubLookUpService lookUpService) {
        this.lookUpService = lookUpService;
    }

    @GetMapping("/{user}")
    public CompletableFuture<ResponseEntity<User>> getUser(@PathVariable String user) {
        return lookUpService.findUser(user).thenApply(result -> {
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @GetMapping("/{username}/blog")
    public CompletableFuture<String> getUserWithBlog(@PathVariable String username) {
        return lookUpService.findUser(username)
                .thenCompose(user -> lookUpService.findBlog(user.getBlog()))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof UserNotFoundException) {
                        return ex.getMessage();
                    } else {
                        return "Erro ocorreu ao tentar recuperar blog do user";
                    }
                });
    }
}
