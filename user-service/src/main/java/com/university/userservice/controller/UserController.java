package com.university.userservice.controller;

import com.university.userservice.dto.CreateUserRequestDto;
import com.university.userservice.dto.UpdateUserRequestDto;
import com.university.userservice.dto.UserResponseDto;
import com.university.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody @Valid CreateUserRequestDto request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,
                                      @RequestBody @Valid UpdateUserRequestDto request) {
        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}/block")
    public UserResponseDto blockUser(@PathVariable Long id) {
        return userService.blockUser(id);
    }

    @PatchMapping("/{id}/unblock")
    public UserResponseDto unblockUser(@PathVariable Long id) {
        return userService.unblockUser(id);
    }
}
