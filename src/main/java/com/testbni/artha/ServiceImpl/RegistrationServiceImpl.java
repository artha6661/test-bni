package com.testbni.artha.ServiceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.testbni.artha.Config.EmailConfig;
import com.testbni.artha.Config.Auth.JwtUtils;
import com.testbni.artha.Config.RefreshToken.RefreshTokenService;
import com.testbni.artha.Model.User;
import com.testbni.artha.Model.Validation;
import com.testbni.artha.Repository.UserRepository;
import com.testbni.artha.Repository.ValidationRepository;
import com.testbni.artha.Service.RegistrationService;
import com.testbni.artha.Util.Status;
import com.testbni.artha.Util.Response.UserInfoResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<?> register(User user) {
     
            if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
        Validation refreshToken = refreshTokenService.createRefreshToken(user.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getValidationCode());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8082/api/confirmation/"+refreshToken.getValidationCode());
        emailConfig.sendEmail(mailMessage);

        System.out.println("Confirmation Token: " + refreshToken.getValidationCode());

        return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
              .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
              .body("Verify email by the link sent on your email address");
       
    }

    @Override
    public ResponseEntity<?> sendValidationEmail(String validToken) {
        Optional<Validation> token = validationRepository.findByValidationCode(validToken);
        if(token != null)
        {
            try {
            refreshTokenService.verifyExpiration(token.get());
            log.info("TOKEN is EXPIRED : " +refreshTokenService.verifyExpiration(token.get()));
            User user = userRepository.findByEmailIgnoreCase(token.get().getUser().getEmail());
            user.setEnabled(true);
            user.setStatus(Status.VALIDATED);
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully!");
            } catch (Exception e) {
                ResponseEntity.badRequest().body("VERIV CODE EXPIRED");
            }
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
