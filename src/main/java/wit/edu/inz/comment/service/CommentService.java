package wit.edu.inz.comment.service;

import api.model.CommentCreateRequest;
import api.model.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getTicketComments(Long ticketId);

    CommentResponse createComment(Long ticketId,  CommentCreateRequest request);


}
