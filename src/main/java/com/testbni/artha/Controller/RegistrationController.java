package com.testbni.artha.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.testbni.artha.Model.User;
import com.testbni.artha.Model.Validation;
import com.testbni.artha.Service.RegistrationService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@Slf4j
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;


    @PostMapping("/register")
    public ResponseEntity<?> registerParticipant(@RequestBody User participant) {
        return registrationService.register(participant);
    }

    // @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    // public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken) {
    //     return registrationService.sendValidationEmail(confirmationToken);
    // }

    @GetMapping("/confirmation/{token}")
    public ResponseEntity<?> confirmUserAccount(@PathVariable("token") String token){
        return registrationService.sendValidationEmail(token);
        
    }

    @GetMapping("/check-status")
    public ResponseEntity<String> checkStatus(@RequestParam String email) {
        String status = registrationService.checkParticipantStatus(email);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/test")
    public String test(){
        String test = "test";
        return test;
    }
}
