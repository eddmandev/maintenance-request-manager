package role.model;

import lombok.Getter;

@Getter
public enum RoleName {
    ROLE_ADMIN("ADMIN"),
    ROLE_WORKER("WORKER"),
    ROLE_RESIDENT("USER");

    private final String roleName;

    RoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}