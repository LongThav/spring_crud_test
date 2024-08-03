package com.learn.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import com.learn.api.models.CreateUserWithRoleRequest;
import com.learn.api.dto.LoginRequest;
import com.learn.api.dto.RoleDTO;
import com.learn.api.dto.UserDTO;
import com.learn.api.dto.UserDataWithToken;
import com.learn.api.models.User;
import com.learn.api.user_services.UserService;
import com.learn.api.webToken.JwtUtil;
import com.learn.api.dto.ResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @SuppressWarnings("rawtypes")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {

            User user = userService.findByEmail(loginRequest.getEmail());

            if (user == null) {
                // logger.error("User not found for email: {}", loginRequest.getEmail());
                // return ResponseEntity.status(404).body("User not found");
                System.out.println("User not found for email: " + loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper<>(404, "Email not found", ""));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("User retrieved: " + user);

            // logger.error("User not found");

            String token = jwtUtil.generateToken(loginRequest.getEmail());
            RoleDTO roleDTO = new RoleDTO(user.getRole().getId(), user.getRole().getName());
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), roleDTO);

            // logger.debug("Authentication successful for user: {}",
            // loginRequest.getEmail());

            UserDataWithToken userDataWithToken = (new UserDataWithToken<>(token, userDTO));

            return ResponseEntity.ok(new ResponseWrapper<>(200, "Response successfully", userDataWithToken));
        } catch (UsernameNotFoundException ex) {
            // logger.error("Invalid email or password", ex);
            return ResponseEntity.status(404).body("Invalid email or password");
        } catch (AuthenticationServiceException ex) {
            // logger.error("Invalid email or password", ex);
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (Exception ex) {
            ex.printStackTrace(); // Add this to see the actual error in the logs
            // // logger.error("An error occurred during authentication", ex);
            // return ResponseEntity.status(500).body("An error occurred during
            // authentication");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(500, "An error occurred during authentication", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserWithRoleRequest request) {
        try {
            // Check if user with the given email already exists
            User existingUser = userService.findByEmail(request.getEmail());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseWrapper<>(409, "Email already in use", ""));
            }

            // Proceed with user creation
            User createdUser = userService.createUserWithRole(request);
            if (createdUser != null) {
                RoleDTO roleDTO = new RoleDTO(createdUser.getRole().getId(), createdUser.getRole().getName());
                UserDTO userDTO = new UserDTO(createdUser.getId(), createdUser.getName(), createdUser.getEmail(),
                        roleDTO);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseWrapper<>(201, "User registered successfully", userDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper<>(404, "Role not found", null));
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Add this to see the actual error in the logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(500, "An error occurred during registration", null));
        }
    }

}
