package wit.edu.inz.admin.controller;

import api.model.TicketResponse;
import api.model.UserResponse;
import api.model.WorkerCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wit.edu.inz.admin.service.AdminService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(
                adminService.getUsers()
        );
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getTickets() {
        return ResponseEntity.ok(
                adminService.getTickets()
        );
    }

    @PostMapping("/workers")
    public ResponseEntity<UserResponse> createWorker(
            @Valid @RequestBody WorkerCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createWorker(request));
    }
}