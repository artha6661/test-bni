package com.testbni.artha.ServiceImpl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.testbni.artha.Config.EmailConfig;
import com.testbni.artha.Model.User;
import com.testbni.artha.Model.Validation;
import com.testbni.artha.Repository.UserRepository;
import com.testbni.artha.Repository.ValidationRepository;
import com.testbni.artha.Service.RegistrationService;
import com.testbni.artha.Util.Status;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public ResponseEntity<?> register(User user) {
     
            if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        Validation confirmationToken = new Validation(user);

        validationRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8082/api/confirmation/"+confirmationToken.getValidationCode());
        emailConfig.sendEmail(mailMessage);

        System.out.println("Confirmation Token: " + confirmationToken.getValidationCode());

        return ResponseEntity.ok("Verify email by the link sent on your email address");
       
    }

    @Override
    public ResponseEntity<?> sendValidationEmail(String validToken) {
        Validation token = validationRepository.findByValidationCode(validToken);
        if(token != null)
        {
            // Set<Status> isEnabled = new HashSet<>();
            // isEnabled.add(Status.VALIDATED);
            // isEnabled.add(Status.REGISTERED);
            User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            // user.setIsEnabled(isEnabled);
            user.setEnabled(true);
            user.setStatus(Status.VALIDATED);
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully!");
        }
        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

    @Override
    public String checkParticipantStatus(String email) {
        Optional<Validation> validation = validationRepository.findByEmail(email);
        if (validation.isPresent() && userRepository.IsEnabledTrue() != null) {
            return "REGISTERED";
        } else if (userRepository.existsByEmail(email) && userRepository.IsEnabledFalse() != null) {
            return "NOT VALIDATED";
        } else {
            return "NOT REGISTERED";
        }
    }

    
    
}
