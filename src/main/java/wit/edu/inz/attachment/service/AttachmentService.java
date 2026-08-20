package wit.edu.inz.attachment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wit.edu.inz.attachment.entity.Attachment;
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

    @Value("${app.file-storage.path:uploads}")
    private String storageDirectory;

    public Attachment uploadAttachment(
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

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = uploadDirectory.resolve(storedFilename);
            file.transferTo(filePath.toFile());

            Attachment attachment = Attachment.builder()
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .storagePath(filePath.toString())
                    .ticket(ticket)
                    .uploadedBy(user)
                    .build();

            return attachmentRepository.save(attachment);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to store uploaded file.",
                    e
            );
        }
    }

    public List<Attachment> getTicketAttachments(Long ticketId) {

        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(
                    "Ticket with id '" + ticketId + "' doesn't exist."
            );
        }

        return attachmentRepository.findByTicketId(ticketId);
    }

    public Attachment getAttachment(Long attachmentId) {

        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Attachment with id '" + attachmentId + "' doesn't exist."
                        ));
    }

    public void deleteAttachment(Long attachmentId) {

        Attachment attachment = getAttachment(attachmentId);

        try {
            Path filePath = Paths.get(attachment.getStoragePath());

            Files.deleteIfExists(filePath);

            attachmentRepository.delete(attachment);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to delete attachment file.",
                    e
            );
        }
    }

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Authenticated user '" + username + "' doesn't exist."
                        ));
    }
}