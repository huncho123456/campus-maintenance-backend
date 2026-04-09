package com.praise.incident.controller;

import com.praise.incident.dto.Response;
import com.praise.incident.dto.UserDto;
import com.praise.incident.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/account")
    public ResponseEntity<Response> getOwnAccountDetails() {
        return ResponseEntity.ok(userService.getOwnAccountDetails());
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateOwnAccount(@RequestBody UserDto userDTO) {
        return ResponseEntity.ok(userService.updateOwnAccount(userDTO));
    }

}
