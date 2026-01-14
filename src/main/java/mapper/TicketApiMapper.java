package mapper;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    Ticket mapToEntity(TicketCreateRequest request);

    @Mapping(target = "status", expression = "java(ticket.getStatus().name())")
    @Mapping(target = "createdAt", expression = "java(ticket.getCreatedAt().atOffset(java.time.ZoneOffset.UTC))")
    TicketResponse mapToResponse(Ticket ticket);
}
