package com.daniel.practice.integration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daniel.practice.integration.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	Users findUsersById(Long id);
}
