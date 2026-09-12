package wit.edu.inz.attachment.service;

import api.model.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "pdf");

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.APPLICATION_PDF_VALUE
            );

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AttachmentApiMapper attachmentApiMapper;

    @Value("${app.file-storage.path:uploads}")
    private String storageDirectory;

    @Value("${app.file-storage.max-size:10485760}")
    private long maxFileSize;

    @Override
    public AttachmentResponse uploadAttachment(
            Long ticketId,
            MultipartFile file
    ) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id '" + ticketId + "' doesn't exist."
                        )
                );

        User user = getAuthenticatedUser();

        validateFile(file);

        Path uploadDirectory = getUploadDirectory();
        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String extension = getExtension(originalFilename);

        String storedFilename =
                UUID.randomUUID() + "." + extension;

        Path filePath = uploadDirectory
                .resolve(storedFilename)
                .normalize();

        validateStoragePath(uploadDirectory, filePath);

        try {
            Files.createDirectories(uploadDirectory);
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to store uploaded file.",
                    e
            );
        }

        Attachment attachment = Attachment.builder()
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .storagePath(filePath.toString())
                .ticket(ticket)
                .uploadedBy(user)
                .build();

        try {
            Attachment savedAttachment =
                    attachmentRepository.save(attachment);

            return attachmentApiMapper.mapToResponse(savedAttachment);
        } catch (RuntimeException e) {
            deleteFileAfterFailedPersistence(filePath);
            throw e;
        }
    }

    @Override
    public List<AttachmentResponse> getTicketAttachments(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new TicketNotFoundException(
                    "Ticket with id '" + ticketId + "' doesn't exist."
            );
        }

        return attachmentRepository.findByTicketId(ticketId)
                .stream()
                .map(attachmentApiMapper::mapToResponse)
                .toList();
    }

    @Override
    public ResponseEntity<Resource> downloadAttachment(Long attachmentId) {
        Attachment attachment = getAttachment(attachmentId);

        validateAttachmentAccess(attachment);

        Path uploadDirectory = getUploadDirectory();

        Path filePath = Paths.get(attachment.getStoragePath())
                .toAbsolutePath()
                .normalize();

        validateStoragePath(uploadDirectory, filePath);

        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = getMediaType(
                attachment.getContentType()
        );

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(attachment.getFileSize())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        attachment.getOriginalFilename()
                                )
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @Override
    public Attachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Attachment with id '" +
                                        attachmentId +
                                        "' doesn't exist."
                        )
                );
    }

    @Override
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = getAttachment(attachmentId);

        Path uploadDirectory = getUploadDirectory();

        Path filePath = Paths.get(attachment.getStoragePath())
                .toAbsolutePath()
                .normalize();

        validateStoragePath(uploadDirectory, filePath);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to delete attachment file.",
                    e
            );
        }

        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Uploaded file cannot be empty."
            );
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    "Uploaded file exceeds the maximum allowed size of "
                            + maxFileSize + " bytes."
            );
        }

        String originalFilename =
                sanitizeFilename(file.getOriginalFilename());

        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Unsupported file extension: " + extension
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(
                        contentType.toLowerCase(Locale.ROOT)
                )) {
            throw new IllegalArgumentException(
                    "Unsupported file content type."
            );
        }

        if (!extensionMatchesContentType(
                extension,
                contentType
        )) {
            throw new IllegalArgumentException(
                    "File extension does not match content type."
            );
        }
    }

    private boolean extensionMatchesContentType(
            String extension,
            String contentType
    ) {
        return switch (extension) {
            case "jpg", "jpeg" ->
                    MediaType.IMAGE_JPEG_VALUE.equalsIgnoreCase(contentType);
            case "png" ->
                    MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(contentType);
            case "pdf" ->
                    MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);
            default -> false;
        };
    }

    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null ||
                originalFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "Uploaded file must have a filename."
            );
        }

        String normalizedFilename =
                originalFilename.replace("\\", "/");

        String filename = Paths.get(normalizedFilename)
                .getFileName()
                .toString();

        if (filename.isBlank() ||
                filename.equals(".") ||
                filename.equals("..")) {
            throw new IllegalArgumentException(
                    "Invalid filename."
            );
        }

        return filename;
    }

    private String getExtension(String filename) {
        int separatorIndex = filename.lastIndexOf('.');

        if (separatorIndex < 0 ||
                separatorIndex == filename.length() - 1) {
            throw new IllegalArgumentException(
                    "Uploaded file must have an extension."
            );
        }

        return filename
                .substring(separatorIndex + 1)
                .toLowerCase(Locale.ROOT);
    }

    private Path getUploadDirectory() {
        return Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    private void validateStoragePath(
            Path uploadDirectory,
            Path filePath
    ) {
        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException(
                    "Invalid attachment storage path."
            );
        }
    }

    private void validateAttachmentAccess(Attachment attachment) {
        User user = getAuthenticatedUser();

        String role = user.getRole().name();

        if ("ADMIN".equals(role) || "WORKER".equals(role)) {
            return;
        }

        Ticket ticket = attachment.getTicket();

        if ("USER".equals(role)
                && ticket.getCreatedBy() != null
                && ticket.getCreatedBy().getId().equals(user.getId())) {
            return;
        }

        throw new AccessDeniedException(
                "You don't have permission to access this attachment."
        );
    }

    private MediaType getMediaType(String contentType) {
        if (contentType == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private void deleteFileAfterFailedPersistence(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
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
                                "Authenticated user '" +
                                        username +
                                        "' doesn't exist."
                        )
                );
    }
}