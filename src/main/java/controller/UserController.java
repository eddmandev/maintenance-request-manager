package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.UserService;
import user.entity.User;

import java.util.Objects;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user){
        return userService.verify(user);
    }
}
