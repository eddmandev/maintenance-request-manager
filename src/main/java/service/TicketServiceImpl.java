package service;

import api.model.TicketCreateRequest;
import api.model.TicketResponse;
import mapper.TicketApiMapper;
import model.TicketStatus;
import org.springframework.stereotype.Service;
import repository.TicketRepository;

@Service
public class TicketServiceImpl implements TicketService{

    private final TicketApiMapper mapper;
    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketApiMapper mapper){
        this.ticketRepository = ticketRepository;
        this.mapper = mapper;
    }

    @Override
    public TicketResponse createTicketFromRequest(TicketCreateRequest ticketRequest) {
        var ticketEntity = mapper.mapToEntity(ticketRequest);
        ticketRepository.save(ticketEntity);
        return mapper.mapToResponse(ticketEntity);
    }

    @Override
    public void updateTicketStatus(TicketStatus status) {

    }

    @Override
    public TicketResponse getTicketDetails(String id) {
        return null;
    }

}
