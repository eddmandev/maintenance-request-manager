package ticket.mapper;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ticket.entity.Ticket;
import ticket.entity.TicketCategory;
import ticket.entity.TicketPriority;
import ticket.entity.TicketStatus;
import ticket.entity.TicketType;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {OffsetDateTime.class, ZoneOffset.class}
)
public interface TicketApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedWorker", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Ticket mapToEntity(TicketCreateRequest request);

    @Mapping(
            target = "createdAt",
            expression = "java(ticket.getCreatedAt() == null ? null : ticket.getCreatedAt().atOffset(ZoneOffset.UTC))"
    )
    TicketResponse mapToResponse(Ticket ticket);

    // HELPER MAPPINGS
    default api.model.TicketStatus mapTicketStatusToApi(TicketStatus status) {
        return status == null ? null : api.model.TicketStatus.valueOf(status.name());
    }

    default TicketStatus mapTicketStatusToEntity(api.model.TicketStatus status) {
        return status == null ? null : TicketStatus.valueOf(status.name());
    }
    default api.model.TicketPriority mapTicketPriorityToApi(TicketPriority priority) {
        return priority == null ? null : api.model.TicketPriority.valueOf(priority.name());
    }
    default TicketPriority mapTicketPriorityToEntity(api.model.TicketPriority priority) {
        return priority == null ? null : TicketPriority.valueOf(priority.name());
    }
    default api.model.TicketCategory mapTicketCategoryToApi(TicketCategory category) {
        return category == null ? null : api.model.TicketCategory.valueOf(category.name());
    }
    default TicketCategory mapTicketCategoryToEntity(api.model.TicketCategory category) {
        return category == null ? null : TicketCategory.valueOf(category.name());
    }
    default api.model.TicketType mapTicketTypeToApi(TicketType type) {
        return type == null ? null : api.model.TicketType.valueOf(type.name());
    }
    default TicketType mapTicketTypeToEntity(api.model.TicketType type) {
        return type == null ? null : TicketType.valueOf(type.name());
    }
}