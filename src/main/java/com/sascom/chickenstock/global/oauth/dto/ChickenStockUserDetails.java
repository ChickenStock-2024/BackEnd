package com.sascom.chickenstock.global.oauth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface ChickenStockUserDetails extends UserDetails {
    @Override
    Collection<? extends GrantedAuthority> getAuthorities();

    @Override
    String getPassword();

    @Override
    String getUsername();

    @Override
    default boolean isAccountNonExpired() {
        return true;
    }

    @Override
    default boolean isAccountNonLocked() {
        return true;
    }

    @Override
    default boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    default boolean isEnabled() {
        return true;
    }
}
