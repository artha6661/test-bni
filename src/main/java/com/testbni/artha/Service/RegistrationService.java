package com.testbni.artha.Service;

import org.springframework.http.ResponseEntity;

import com.testbni.artha.Model.User;
import com.testbni.artha.Model.Validation;

public interface RegistrationService {
    public ResponseEntity<?> register(User user);

    public ResponseEntity<?> sendValidationEmail(String validToken);

    public String checkParticipantStatus(String email) ;
}
