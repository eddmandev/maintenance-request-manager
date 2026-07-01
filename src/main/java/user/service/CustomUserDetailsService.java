package user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserRepository userRepository;
}
