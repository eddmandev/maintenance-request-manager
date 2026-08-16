package wit.edu.inz.user.mapper;

import api.model.RegisterRequest;
import api.model.WorkerCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import wit.edu.inz.role.entity.Role;
import wit.edu.inz.user.entity.User;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = Role.class
)
public interface UserApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "WORKER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "addressLine", ignore = true)
    @Mapping(target = "addressLine2", ignore = true)
    @Mapping(target = "buildingNr", ignore = true)
    @Mapping(target = "apartmentNr", ignore = true)
    User mapWorkerToEntity(WorkerCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    User mapRegisterRequestToEntity(RegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    User mapToEntity(RegisterRequest request);

}