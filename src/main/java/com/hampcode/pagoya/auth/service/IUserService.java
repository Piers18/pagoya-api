package com.hampcode.pagoya.auth.service;

import com.hampcode.pagoya.auth.dto.RegisterUserRequest;
import com.hampcode.pagoya.auth.dto.UserResponse;

public interface IUserService {
    UserResponse register(RegisterUserRequest request);
}
