package wit.edu.inz.profile.service;

import api.model.ProfileResponse;
import api.model.ProfileTicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wit.edu.inz.exception.UserNotFoundException;
import wit.edu.inz.ticket.entity.TicketStatus;
import wit.edu.inz.ticket.repository.TicketRepository;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Override
    public ProfileResponse getProfile(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User '" + username + "' was not found."
                        )
                );

        List<ProfileTicketResponse> openTickets =
                ticketRepository
                        .findAllByCreatedByUsername(username)
                        .stream()
                        .filter(ticket ->
                                ticket.getStatus() != TicketStatus.COMPLETED
                                        && ticket.getStatus() != TicketStatus.CANCELLED
                        )
                        .map(ticket -> {
                            ProfileTicketResponse response =
                                    new ProfileTicketResponse();

                            response.setId(ticket.getId());
                            response.setTitle(ticket.getTitle());

                            response.setStatus(api.model.TicketStatus
                                            .valueOf(
                                                    ticket.getStatus().name()
                                            )
                            );

                            return response;
                        })
                        .toList();

        ProfileResponse response = new ProfileResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        response.setRole(api.model.Role.valueOf(
                        user.getRole().name()
                )
        );

        response.setTickets(openTickets);

        return response;
    }
}