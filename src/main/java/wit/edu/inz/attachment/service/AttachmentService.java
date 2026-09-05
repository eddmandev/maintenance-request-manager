package wit.edu.inz.attachment.service;

import api.model.AttachmentResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

interface AttachmentService {
    AttachmentResponse uploadAttachment(Long ticketId, MultipartFile file);

    List<AttachmentResponse> getTicketAttachments(Long ticketId);

    ResponseEntity<Resource> downloadAttachment(Long attachmentId);

    void deleteAttachment(Long attachmentId);
}

