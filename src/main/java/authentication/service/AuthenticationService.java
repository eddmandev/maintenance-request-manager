package authentication.service;

import api.model.AuthenticationRequest;
import api.model.AuthenticationResponse;
import api.model.RegisterRequest;
import authentication.jwt.JwtService;
import exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import role.entity.Role;
import user.entity.User;
import user.mapper.UserApiMapper;
import user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserApiMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request) {

        User user = userMapper.mapToEntity(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtService.generateToken(user));

        return response;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with username '" + request.getUsername() + "' doesn't exist."
                        ));

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtService.generateToken(user));
        return response;
    }
}