package com.learn.api.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.api.models.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {

}