package user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import user.service.UserService;
import user.entity.User;

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
