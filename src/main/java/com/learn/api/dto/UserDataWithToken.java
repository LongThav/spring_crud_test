package com.learn.api.dto;

public class UserDataWithToken<T> {
    private String token;
    private T user;

    // Constructor, getters and setters
    public UserDataWithToken(String token, T user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }
}

class Role {
    private int id;
    private String name;

    // Constructor, getters and setters
    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
