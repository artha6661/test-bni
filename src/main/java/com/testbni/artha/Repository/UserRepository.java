package com.testbni.artha.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testbni.artha.Model.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String emailId);

    Boolean existsByEmail(String email);

    User IsEnabledTrue();

    User IsEnabledFalse();

    Optional<User> findByEmail(String email);
}
