package user.mapper;

import api.model.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import role.entity.Role;
import user.entity.User;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = Role.class
)
public interface UserApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", expression = "java(Role.USER)")
    User mapToEntity(RegisterRequest request);
}