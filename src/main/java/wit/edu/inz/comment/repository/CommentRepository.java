package wit.edu.inz.comment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import wit.edu.inz.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}