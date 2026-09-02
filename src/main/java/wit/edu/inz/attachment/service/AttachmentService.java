package wit.edu.inz.attachment.service;

import api.model.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wit.edu.inz.attachment.entity.Attachment;
import wit.edu.inz.attachment.mapper.AttachmentApiMapper;
import wit.edu.inz.attachment.repository.AttachmentRepository;
import wit.edu.inz.exception.TicketNotFoundException;
import wit.edu.inz.exception.UserNotFoundException;
import wit.edu.inz.ticket.entity.Ticket;
import wit.edu.inz.ticket.repository.TicketRepository;
import wit.edu.inz.user.entity.User;
import wit.edu.inz.user.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AttachmentApiMapper attachmentApiMapper;

    @Value("${app.file-storage.path:uploads}")
    private String storageDirectory;

    public AttachmentResponse uploadAttachment(
            Long ticketId,
            MultipartFile file) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + ticketId + "' doesn't exist."
                        ));

        User user = getAuthenticatedUser();

        try {
            Path uploadDirectory = Paths.get(storageDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadDirectory);

            String originalFilename = file.getOriginalFilename();

            if (originalFilename == null || originalFilename.isBlank()) {
                originalFilename = "attachment";
            }

            String storedFilename =
                    UUID.randomUUID() + "_" + originalFilename;

            Path filePath = uploadDirectory.resolve(storedFilename);

            file.transferTo(filePath.toFile());

            Attachment attachment = Attachment.builder()
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .contentType(
                            file.getContentType() != null
                                    ? file.getContentType()
                                    : MediaType.APPLICATION_OCTET_STREAM_VALUE
                    )
                    .fileSize(file.getSize())
                    .storagePath(filePath.toString())
                    .ticket(ticket)
                    .uploadedBy(user)
                    .build();

            return attachmentApiMapper.mapToResponse(
                    attachmentRepository.save(attachment)
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to store uploaded file.", e);
        }
    }

    public List<AttachmentResponse> getTicketAttachments(Long ticketId) {

        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(
                    "Ticket with id '" + ticketId + "' doesn't exist.");
        }

        return attachmentRepository.findByTicketId(ticketId)
                .stream()
                .map(attachmentApiMapper::mapToResponse)
                .toList();
    }

    public ResponseEntity<Resource> downloadAttachment(Long attachmentId) {
        Attachment attachment = getAttachment(attachmentId);
        Path filePath = Paths.get(attachment.getStoragePath());
        Resource resource = new FileSystemResource(filePath);

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

    public Attachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Attachment with id '" +
                                        attachmentId +
                                        "' doesn't exist."
                        ));
    }

    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = getAttachment(attachmentId);

        try {
            Path filePath = Paths.get(
                    attachment.getStoragePath()
            );

            Files.deleteIfExists(filePath);
            attachmentRepository.delete(attachment);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete attachment file.", e);
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Authenticated user '" + username + "' doesn't exist."
                        ));
    }
}