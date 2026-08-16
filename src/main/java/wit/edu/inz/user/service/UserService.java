package wit.edu.inz.user.service;

import wit.edu.inz.authentication.jwt.JwtService;
import wit.edu.inz.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public User registerUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String verify(User user) throws UserNotFoundException {
        var authenticate = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), user.getPassword())
        );

        if (!authenticate.isAuthenticated()){
            throw new UserNotFoundException("User of the username " + user.getUsername() +" doesn't exist.");
        }
        return jwtService.generateToken(user);
    }
}
