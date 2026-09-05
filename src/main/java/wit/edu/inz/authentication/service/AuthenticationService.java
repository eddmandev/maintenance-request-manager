package wit.edu.inz.authentication.service;

import api.model.AuthenticationRequest;
import api.model.AuthenticationResponse;
import api.model.RegisterRequest;
import api.model.WorkerCreateRequest;
import wit.edu.inz.authentication.jwt.JwtService;
import wit.edu.inz.exception.UserAlreadyExistsException;
import wit.edu.inz.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wit.edu.inz.role.entity.Role;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.mapper.UserApiMapper;
import wit.edu.inz.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserApiMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = userMapper.mapToEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setActive(true);
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
                ));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User with username '" + request.getUsername() + "' doesn't exist."
                        ));

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtService.generateToken(user));
        return response;
    }

    public AuthenticationResponse createWorker(
            WorkerCreateRequest request) {

        User worker = userMapper.mapWorkerToEntity(request);

        worker.setPassword(passwordEncoder.encode(request.getPassword()));

        worker.setRole(Role.WORKER);
        worker.setEnabled(true);
        worker.setActive(true);

        userRepository.save(worker);

        AuthenticationResponse authResponse = new AuthenticationResponse();
        authResponse.setToken(jwtService.generateToken(worker));
        return authResponse;
    }
}