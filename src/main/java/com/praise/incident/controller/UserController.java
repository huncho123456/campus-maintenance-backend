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

    @GetMapping("/all")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        log.info("Controller: Request to delete user ID: {}", id);
        Response response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

}
