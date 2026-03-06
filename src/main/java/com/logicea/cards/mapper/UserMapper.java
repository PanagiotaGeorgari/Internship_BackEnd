package com.logicea.cards.mapper;

import com.logicea.cards.dto.UserDto;
import com.logicea.cards.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getUserId(), user.getEmail(), user.getName(),
                user.getRole(), user.getPassword());
    }

    public static User toEntity(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.email());
        user.setName(userDto.name());
        user.setRole(userDto.role());
        user.setPassword(userDto.password());
        return user;
    }
}
