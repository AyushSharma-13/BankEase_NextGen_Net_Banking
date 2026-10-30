package com.bankease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private Long customerId;
    private String username;
    private String role;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
