package service;

import entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserDetails {
    String getUsername();

    String getPassword();

    Collection<? extends GrantedAuthority> getAuthorities();
}
