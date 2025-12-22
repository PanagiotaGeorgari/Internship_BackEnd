package com.logicea.cards.dto;

import com.logicea.cards.enums.UserRole;

public record UserDto(int userId, String email, String name,
                      UserRole role, String password) {

}
