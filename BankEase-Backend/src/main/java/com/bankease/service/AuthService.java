package com.bankease.service;

import com.bankease.dto.request.ChangePasswordRequest;
import com.bankease.dto.request.LoginRequest;
import com.bankease.dto.request.RegisterRequest;
import com.bankease.dto.response.AuthResponse;
import com.bankease.dto.response.UserDto;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    UserDto getCurrentUserProfile();
    void changePassword(ChangePasswordRequest request);
}
