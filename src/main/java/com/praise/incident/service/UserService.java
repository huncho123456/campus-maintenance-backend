package com.praise.incident.service;

import com.praise.incident.dto.Response;
import com.praise.incident.dto.UserDto;
import com.praise.incident.entity.UserEntity;
import com.praise.incident.enums.UserRole;
import com.praise.incident.exception.InvalidCredentialException;
import com.praise.incident.exception.NotFoundException;
import com.praise.incident.repo.UserRepo;
import com.praise.incident.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    public Response registerUser(UserDto registrationRequest) {
        log.info("Inside registerUser()");

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + registrationRequest.getEmail());
        }

        UserRole role = registrationRequest.getRole() != null ? registrationRequest.getRole() : UserRole.USER;

        String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        UserEntity userToSave = UserEntity.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(hashedPassword)
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(role)
                .active(true)
                .sex(registrationRequest.getSex())
                .build();

        UserEntity savedUser = userRepository.save(userToSave);

        UserDto userDTO = modelMapper.map(savedUser, UserDto.class);

        return Response.builder()
                .status(201)
                .user(userDTO)
                .message(savedUser.getEmail() + " Registered successfully.")
                .build();

    }

        public Response loginUser(UserDto loginRequest) {

            log.info("Inside loginUser() " + loginRequest.getEmail());
            String Message = "invalid Credential";
            String JWTExpires = "30 days";

            UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new NotFoundException("Email Not Found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new InvalidCredentialException(Message);
            }

            String token = jwtUtils.generateToken(user.getEmail());

            return Response.builder()
                    .status(200)
                    .message(user.getEmail() + " logged in successfully")
                    .role(user.getRole())
                    .token(token)
                    .active(user.isActive())
                    .expirationTime(JWTExpires)
                    .build();

        }

    public Response getOwnAccountDetails() {

        log.info("INSIDE getOwnAccountDetails()");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user not found"));

        UserDto userDTO = modelMapper.map(user, UserDto.class);

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    public UserEntity getCurrentLoggedInUser() {

        log.info("INSIDE getCurrentLoggedInUser()");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }


    public Response updateOwnAccount(UserDto userDTO) {

        log.info("Inside updateOwnAccount()");
        UserEntity existingUser = getCurrentLoggedInUser();

        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getFirstName() != null) existingUser.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) existingUser.setLastName(userDTO.getLastName());
        if (userDTO.getPhoneNumber() != null) existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());
        if (userDTO.getSex() != null) existingUser.setSex(userDTO.getSex());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("User Updated Successfully")
                .build();

    }

}