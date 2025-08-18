package com.davon.library.dto;

import java.util.Set;

public record UserMeDTO(
        String username,
        String fullName,
        Set<String> roles) {
}
