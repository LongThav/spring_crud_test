package com.learn.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learn.api.dto.AuthRespone;
import com.learn.api.dto.ResponseWrapper;
import com.learn.api.dto.RoleDTO;
import com.learn.api.dto.UserDTO;
import com.learn.api.models.CreateUserWithRoleRequest;
import com.learn.api.models.Role;
import com.learn.api.models.User;
import com.learn.api.user_services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private boolean isTokenMissing(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        return authorizationHeader == null || !authorizationHeader.startsWith("Bearer ");
    }

    @PostMapping
    public ResponseEntity<?> createUserWithRole(@RequestBody CreateUserWithRoleRequest request, HttpServletRequest httpRequest) {
        if (isTokenMissing(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRespone(false, "User not authorized"));
        }
        
        User createdUser = userService.createUserWithRole(request);
        if (createdUser != null) {
            return ResponseEntity.ok(convertToDTO(createdUser));
        } else {
            return ResponseEntity.notFound().build(); // Handle the case when the role is not found or user creation fails
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpServletRequest httpRequest) {
        if (isTokenMissing(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRespone(false, "User not authorized"));
        }
        
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ResponseWrapper<>(200, "Response successfully", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest httpRequest) {
        if (isTokenMissing(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRespone(false, "User not authorized"));
        }
        
        UserDTO userDTO = userService.getUserById(id);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest httpRequest) {
        if (isTokenMissing(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRespone(false, "User not authorized"));
        }
        
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return new ResponseEntity<>(new AuthRespone(true, "User deleted successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new AuthRespone(false, "User not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody String password, HttpServletRequest httpRequest) {
        if (isTokenMissing(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRespone(false, "User not authorized"));
        }
        
        User updatedUser = userService.updatePassword(id, password);
        if (updatedUser != null) {
            return ResponseEntity.ok(new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserPartial(@PathVariable Long id, @RequestBody User user, HttpServletRequest httpRequest) {
        if (isTokenMissing(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRespone(false, "User not authorized"));
        }
        
        User updatedUser = userService.updateUserPartial(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private UserDTO convertToDTO(User user) {
        Role role = user.getRole();
        RoleDTO roleDTO = null;
        if (role != null) {
            roleDTO = new RoleDTO(role.getId(), role.getName());
        }
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), roleDTO);
    }
}
