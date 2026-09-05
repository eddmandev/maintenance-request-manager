package wit.edu.inz.admin.service;

import api.model.TicketResponse;
import api.model.UserResponse;
import api.model.WorkerCreateRequest;

import java.util.List;

public interface AdminService {

    List<UserResponse> getUsers();

    List<TicketResponse> getTickets();

    UserResponse createWorker(WorkerCreateRequest request);
}