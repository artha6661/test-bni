package com.testbni.artha.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testbni.artha.Model.Validation;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {
    Optional<Validation> findByEmail(String email);
    Validation findByValidationCode(String confirmationToken);
}