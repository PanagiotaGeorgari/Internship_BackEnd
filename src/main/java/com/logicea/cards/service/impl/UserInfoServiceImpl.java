package com.logicea.cards.service.impl;

import com.logicea.cards.dto.UserInfoDto;
import com.logicea.cards.entity.UserInfo;
import com.logicea.cards.mapper.UserInfoMapper;
import com.logicea.cards.repository.UserInfoRepository;
import com.logicea.cards.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserInfoDto createUser(UserInfoDto userInfoDto) {
        UserInfo userInfo= UserInfoMapper.toEntity(userInfoDto);
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        System.out.println(userInfo.getPassword());
        userInfoRepository.save(userInfo);
        return UserInfoMapper.toDto(userInfo);
    }
}
