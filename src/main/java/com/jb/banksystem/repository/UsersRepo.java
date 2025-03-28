package com.jb.banksystem.repository;

import com.jb.banksystem.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Long> {
    Optional<OurUsers> findByUsername(String username);
    Optional<OurUsers> findByEmail(String email);
}
