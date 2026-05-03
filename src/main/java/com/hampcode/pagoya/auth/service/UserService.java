package com.hampcode.pagoya.auth.service;

import com.hampcode.pagoya.auth.dto.RegisterUserRequest;
import com.hampcode.pagoya.auth.dto.UserResponse;
import com.hampcode.pagoya.auth.exception.EmailAlreadyExistsException;
import com.hampcode.pagoya.auth.mapper.UserMapper;
import com.hampcode.pagoya.auth.model.Role;
import com.hampcode.pagoya.auth.model.User;
import com.hampcode.pagoya.auth.repository.RoleRepository;
import com.hampcode.pagoya.auth.repository.UserRepository;
import com.hampcode.pagoya.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        // RN-01: email unico
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }
        // RN-02: rol CUSTOMER obligatorio
        Role role = roleRepository.findByName("CUSTOMER")
            .orElseThrow(() -> new ResourceNotFoundException(
                "rol CUSTOMER no esta configurado"));

        User user = userMapper.toEntity(request);
        user.setRole(role);
        return userMapper.toResponse(userRepository.save(user));
    }
}
