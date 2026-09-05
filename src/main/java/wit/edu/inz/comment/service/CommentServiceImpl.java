package wit.edu.inz.comment.service;


import api.model.CommentCreateRequest;
import api.model.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import wit.edu.inz.comment.entity.Comment;
import wit.edu.inz.comment.mapper.CommentApiMapper;
import wit.edu.inz.comment.repository.CommentRepository;
import wit.edu.inz.exception.TicketNotFoundException;
import wit.edu.inz.exception.UserNotFoundException;
import wit.edu.inz.ticket.entity.Ticket;
import wit.edu.inz.ticket.repository.TicketRepository;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentApiMapper commentApiMapper;

    public CommentResponse createComment(
            Long ticketId,
            CommentCreateRequest request) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" +
                                        ticketId +
                                        "' doesn't exist."));

        User author = getAuthenticatedUser();

        Comment comment = Comment.builder()
                .content(request.getContent())
                .ticket(ticket)
                .author(author)
                .build();

        return commentApiMapper.mapToResponse(
                commentRepository.save(comment));
    }

    public List<CommentResponse> getTicketComments(Long ticketId) {

        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(
                    "Ticket with id '" + ticketId + "' doesn't exist.");
        }

        return commentRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(commentApiMapper::mapToResponse)
                .toList();
    }

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Authenticated user '"
                                        + username
                                        + "' doesn't exist."));
    }
}