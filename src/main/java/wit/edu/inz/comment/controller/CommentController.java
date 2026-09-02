package wit.edu.inz.comment.controller;


import api.model.CommentCreateRequest;
import api.model.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wit.edu.inz.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long ticketId,
            @RequestBody CommentCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        commentService.createComment(
                                ticketId,
                                request
                        )
                );
    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                commentService.getTicketComments(ticketId)
        );
    }
}