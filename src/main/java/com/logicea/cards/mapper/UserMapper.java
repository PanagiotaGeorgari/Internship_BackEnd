package com.logicea.cards.mapper;

import com.logicea.cards.dto.UserDto;
import com.logicea.cards.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getUserId(), user.getEmail(), user.getName(),
                user.getRole(), user.getPassword());
    }

    public static User toEntity(UserDto userDto) {
        return new User(userDto.userId(), userDto.email(), userDto.name(),
                userDto.role(), userDto.password());
    }
}
