package wit.edu.inz.comment.mapper;


import api.model.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import wit.edu.inz.comment.entity.Comment;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {
            OffsetDateTime.class,
            ZoneOffset.class,
        }
)
public interface CommentApiMapper {

    @Mapping(
            target = "createdAt",
            expression = "java(comment.getCreatedAt() == null ? null : comment.getCreatedAt().atOffset(ZoneOffset.UTC))"
    )
    @Mapping(
            target = "ticketId",
            expression = "java(comment.getTicket() == null ? null : comment.getTicket().getId())"
    )
    @Mapping(
            target = "authorId",
            expression = "java(comment.getAuthor() == null ? null : comment.getAuthor().getId())"
    )
    @Mapping(
            target = "authorUsername",
            expression = "java(comment.getAuthor() == null ? null : comment.getAuthor().getUsername())"
    )
    CommentResponse mapToResponse(Comment comment);
}