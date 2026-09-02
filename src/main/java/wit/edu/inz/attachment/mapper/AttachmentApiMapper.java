package wit.edu.inz.attachment.mapper;

import api.model.AttachmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import wit.edu.inz.attachment.entity.Attachment;

@Mapper(componentModel = "spring")
public interface AttachmentApiMapper {

    @Mapping(target = "filename", source = "originalFilename")
    @Mapping(target = "size", source = "fileSize")
    @Mapping(target = "uploadedAt",
            expression = "java(attachment.getUploadedAt() == null ? null : attachment.getUploadedAt().atOffset(java.time.ZoneOffset.UTC))")
    AttachmentResponse mapToResponse(Attachment attachment);
}