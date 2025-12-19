package com.logicea.cards.mapper;

import com.logicea.cards.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoUserDetailsMapper implements UserDetails {
   private int userId;
    private String userName;
   private String email;
   private String password;
   private String role;

   public UserInfoUserDetailsMapper (UserInfo userInfo) {
       this.userName = userInfo.getName();
       this.email = userInfo.getEmail();
       this.password = userInfo.getPassword();
       this.role=userInfo.getRole().toString();
       this.userId=userInfo.getUserId();

   }
    public int getUserId() { return userId; }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
