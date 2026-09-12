package wit.edu.inz.admin.service;

import api.model.TicketResponse;
import api.model.UserResponse;
import api.model.WorkerCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wit.edu.inz.exception.UserAlreadyExistsException;
import wit.edu.inz.role.entity.Role;
import wit.edu.inz.ticket.mapper.TicketApiMapper;
import wit.edu.inz.ticket.repository.TicketRepository;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketApiMapper ticketApiMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getUsers() {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .map(this::mapUserToResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> getTickets() {
        return StreamSupport
                .stream(ticketRepository.findAll().spliterator(), false)
                .map(ticketApiMapper::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse createWorker(WorkerCreateRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username already exists."
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already exists."
            );
        }

        User worker = new User();

        worker.setFirstName(request.getFirstName());
        worker.setLastName(request.getLastName());
        worker.setUsername(request.getUsername());
        worker.setEmail(request.getEmail());
        worker.setPassword(
                passwordEncoder.encode(request.getPassword()));
        worker.setRole(Role.WORKER);

        User savedWorker = userRepository.save(worker);

        return mapUserToResponse(savedWorker);
    }

    private UserResponse mapUserToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(
                api.model.Role.valueOf(
                        user.getRole().name()
                )
        );

        return response;
    }
}