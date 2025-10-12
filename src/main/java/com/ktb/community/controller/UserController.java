package com.ktb.community.controller;

import com.ktb.community.dto.user.EmailCheckResponse;
import com.ktb.community.dto.user.UserRegistrationRequest;
import com.ktb.community.dto.user.UserResponse;
import com.ktb.community.entity.User;
import com.ktb.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(new EmailCheckResponse(available));
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.register(request.email(), request.password(), request.nickname(), request.profileImageId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getByEmailOrThrow(principal.getUsername());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getByEmailOrThrow(principal.getUsername());
        userService.markDeleted(user);
        return ResponseEntity.noContent().build();
    }
}
