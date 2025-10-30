package com.khokhlov.cloudstorage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, CredentialsContainer {
    private long id;
    private transient String password;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("Username=").append(this.username).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Granted Authorities=").append(this.authorities).append("]");
        return sb.toString();
    }
}
