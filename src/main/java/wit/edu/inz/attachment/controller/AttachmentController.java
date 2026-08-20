package wit.edu.inz.attachment.controller;

import api.model.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wit.edu.inz.attachment.entity.Attachment;
import wit.edu.inz.attachment.service.AttachmentService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/{ticketId}/attachments")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file) {
        Attachment attachment =
                attachmentService.uploadAttachment(ticketId, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(attachment));
    }

    @GetMapping("/{ticketId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @PathVariable Long ticketId) {
        List<AttachmentResponse> attachments =
                attachmentService.getTicketAttachments(ticketId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long attachmentId) {
        Attachment attachment =
                attachmentService.getAttachment(attachmentId);

        Path path = Paths.get(attachment.getStoragePath());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType;

        try {
            mediaType = MediaType.parseMediaType(
                    attachment.getContentType()
            );
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(attachment.getFileSize())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(attachment.getOriginalFilename())
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long attachmentId) {

        attachmentService.deleteAttachment(attachmentId);

        return ResponseEntity.noContent().build();
    }

    private AttachmentResponse toResponse(Attachment attachment) {

        AttachmentResponse response = new AttachmentResponse();

        response.setId(attachment.getId());
        response.setFilename(attachment.getOriginalFilename());
        response.setContentType(attachment.getContentType());
        response.setSize(attachment.getFileSize());

        if (attachment.getUploadedAt() != null) {
            response.setUploadedAt(
                    attachment.getUploadedAt()
                            .atOffset(ZoneOffset.UTC)
            );
        }

        return response;
    }
}