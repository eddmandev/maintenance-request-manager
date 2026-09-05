package wit.edu.inz.attachment.controller;

import api.model.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wit.edu.inz.attachment.service.AttachmentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentServiceImpl attachmentService;

    @PostMapping("/{ticketId}/attachments")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.uploadAttachment(ticketId, file));
    }

    @GetMapping("/{ticketId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                attachmentService.getTicketAttachments(ticketId)
        );
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long attachmentId) {

        return attachmentService.downloadAttachment(attachmentId);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long attachmentId) {

        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}