package model;

public enum RoleName {
    ROLE_ADMIN("ADMIN"),
    ROLE_WORKER("WORKER"),
    ROLE_RESIDENT("RESIDENT");

    private final String roleName;

    RoleName (String roleName) {
        this.roleName = roleName;
    }
}
