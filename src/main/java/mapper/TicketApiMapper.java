package mapper;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = spring)
public interface TicketApiMapper {

    Ticket mapToEntity(TicketCreateRequest request);

    TicketResponse mapToResponse(TicketCreateRequest request);
}
