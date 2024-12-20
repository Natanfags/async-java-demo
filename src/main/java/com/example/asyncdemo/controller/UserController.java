package com.example.asyncdemo.controller;

import com.example.asyncdemo.User;
import com.example.asyncdemo.exception.UserNotFoundException;
import com.example.asyncdemo.service.async.GitHubLookUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
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
                .thenCompose(user -> lookUpService.findBlog(user.getBlog())
                        .thenApply(blogContent -> "Content is :" + blogContent))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof UserNotFoundException) {
                        return ex.getMessage();
                    } else {
                        return "Erro ocorreu ao tentar recuperar blog do user";
                    }
                });
    }

    @GetMapping("/{userName}/blog-status")
    public CompletableFuture<String> checkStatus(@PathVariable String userName) {
        return lookUpService.findUser(userName)
                .thenCompose(user -> lookUpService.findBlog(user.getBlog())
                        .thenCompose(lookUpService::checkIfBlogIsOnline)
                        .exceptionally(ex -> {
                            if (ex.getCause() instanceof Exception) {
                                return "Erro ao verificar a URL do blog: " + ex.getMessage();
                            } else {
                                return "Erro desconhecido ao verificar a URL.";
                            }
                        })
                );
    }

    @GetMapping("/f")
    public CompletableFuture<ResponseEntity<List<User>>> getUsers(@RequestParam List<String> users) {
        List<CompletableFuture<User>> userFutures = users.stream().map(user -> lookUpService.findUser(user).thenCompose(foundUser -> {
            if (foundUser != null && foundUser.getBlog() != null) {
                return lookUpService.checkIfBlogIsOnline(foundUser.getBlog()).thenApply(blogStatus -> {
                    System.out.println(blogStatus);
                    return foundUser;
                });
            } else {
                return CompletableFuture.completedFuture(foundUser);
            }
        })).toList();

        return CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0])).thenApply(v -> userFutures.stream().map(CompletableFuture::join).filter(Objects::nonNull).toList()).thenApply(ResponseEntity::ok);
    }
}
