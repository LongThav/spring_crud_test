package com.learn.api.user_services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.learn.api.models.User;
import com.learn.api.dto.RoleDTO;
import com.learn.api.dto.UserDTO;
import com.learn.api.repositorys.UserRepository;
import com.learn.api.models.CreateUserWithRoleRequest;
import com.learn.api.models.Role; // Import the Role entity
import com.learn.api.repositorys.RoleRepository; // Import the RoleRepository

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository; // Autowire RoleRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

    // public User createUserWithRole(CreateUserWithRoleRequest request) {
    //     // Retrieve the role from the database based on the roleId
    //     Long roleId = request.getRoleId();
    //     Role role = roleRepository.findById(roleId).orElse(null);
    //     if (role != null) {
    //         User user = new User();
    //         user.setName(request.getName());
    //         user.setEmail(request.getEmail());
    //         // user.setPassword(request.getPassword());
    //         user.setPassword(passwordEncoder.encode(request.getPassword()));
    //         user.setRole(role); // Associate the role with the user
    //         logger.info("Creating user with role: {}", user);
    //         return userRepository.save(user);
    //     } else {
    //         logger.warn("Role not found with id: {}", roleId);
    //         // Handle the case when the role is not found
    //         return null;
    //     }
    // }

   
    public User createUserWithRole(CreateUserWithRoleRequest request) {
        Long roleId = request.getRoleId();
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
            user.setRole(role);
            logger.info("Creating user with role: {}", user);
            return userRepository.save(user);
        } else {
            logger.warn("Role not found with id: {}", roleId);
            return null;
        }
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Fetching all users: {}", users);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("Fetching user by id: {} - {}", id, user.get());
            return convertToDTO(user.get());
        } else {
            logger.warn("User not found with id: {}", id);
            return null;
        }
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            logger.info("Deleting user by id: {}", id);
            userRepository.deleteById(id);
            return true;
        } else {
            logger.warn("User not found with id: {}", id);
            return false;
        }
    }

    public User updatePassword(Long id, String password) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(password);
            logger.info("Updating password for user id: {}", id);
            return userRepository.save(user);
        } else {
            logger.warn("User not found with id: {}", id);
            return null;
        }
    }

    public User updateUserRole(Long userId, Long roleId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalUser.isPresent() && optionalRole.isPresent()) {
            User user = optionalUser.get();
            Role role = optionalRole.get();
            user.setRole(role); // Assuming there's a setter for role in the User entity
            logger.info("Updating role for user id: {} with role id: {}", userId, roleId);
            return userRepository.save(user);
        } else {
            logger.warn("User or Role not found with id: {}", userId);
            return null;
        }
    }

    // private UserDTO convertToDTO(User user) {
    // return new UserDTO(user.getId(), user.getName(), user.getEmail(), role);
    // }

    private UserDTO convertToDTO(User user) {
        Role role = user.getRole();
        RoleDTO roleDTO = null;
        if (role != null) {
            roleDTO = new RoleDTO(role.getId(), role.getName());
        }
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), roleDTO);
    }

    public User updateUserPartial(Long id, User partialUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            if (partialUser.getName() != null) {
                existingUser.setName(partialUser.getName());
            }
            if (partialUser.getEmail() != null) {
                existingUser.setEmail(partialUser.getEmail());
            }
            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }
}
