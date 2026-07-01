package controller;

import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import repository.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody User user){
        var foundUser = userRepository.findByUsername(user.getEmail());
        if (Objects.isNull(foundUser)){
            throw new UsernameNotFoundException("User by the email " + user.getEmail() + " was not found");
        }
        return null;
    }
}
