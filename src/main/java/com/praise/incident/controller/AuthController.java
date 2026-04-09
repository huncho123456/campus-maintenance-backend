package com.praise.incident.controller;

import com.praise.incident.dto.Response;
import com.praise.incident.dto.UserDto;
import com.praise.incident.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Response registerUser(@RequestBody UserDto registrationRequest) {
        return userService.registerUser(registrationRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Response loginUser(@RequestBody UserDto loginRequest) {
        return userService.loginUser(loginRequest);
    }
}
