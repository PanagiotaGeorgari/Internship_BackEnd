package com.logicea.cards.service.impl;

import com.logicea.cards.entity.UserInfo;
import com.logicea.cards.mapper.UserInfoUserDetailsMapper;
import com.logicea.cards.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public class UserInfoUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo=userInfoRepository.findByEmail(email);
        return userInfo.map(UserInfoUserDetailsMapper::new)
                .orElseThrow(() -> new UsernameNotFoundException("User "+ email+" not found"));
    }

}
