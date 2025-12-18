package com.logicea.cards.mapper;

import com.logicea.cards.dto.UserInfoDto;
import com.logicea.cards.entity.UserInfo;

public class UserInfoMapper {
    public static UserInfoDto toDto (UserInfo userInfo) {
        return new UserInfoDto(userInfo.getUserId(), userInfo.getEmail(), userInfo.getName(),
                userInfo.getRole(), userInfo.getPassword());
    }
    public static UserInfo toEntity (UserInfoDto userInfoDto) {
        return new UserInfo(userInfoDto.userId(), userInfoDto.email(), userInfoDto.name(),
                userInfoDto.role(), userInfoDto.password());
    }
}
