package com.learn.api.dto;


public class UserDTO {
    @SuppressWarnings("unused")
    private String token;
    private Long id;
    private String name;
    private String email;
    private RoleDTO role; // Add this field for role information

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String name, String email, RoleDTO role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }
}
