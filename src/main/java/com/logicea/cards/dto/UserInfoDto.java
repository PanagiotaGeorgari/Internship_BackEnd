package com.logicea.cards.dto;

import com.logicea.cards.enums.UserRole;

public record UserInfoDto(int userId, String email, String name,
                          UserRole role, String password) {

}
