package com.testbni.artha.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

import com.testbni.artha.Config.Auth.JwtUtils;
import com.testbni.artha.Config.RefreshToken.RefreshTokenService;
import com.testbni.artha.Model.User;
import com.testbni.artha.Model.Validation;
import com.testbni.artha.Service.RegistrationService;
import com.testbni.artha.Util.Exception.TokenRefreshException;
import com.testbni.artha.Util.Response.MessageResponse;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@Slf4j
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;


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

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
    String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        
    if ((refreshToken != null) && (refreshToken.length() > 0)) {
    return refreshTokenService.findByToken(refreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(Validation::getUser)
        .map(user -> {
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Token is refreshed successfully!"));
        })
        .orElseThrow(() -> new TokenRefreshException(refreshToken,
            "Refresh token is not in database!"));
    }
        
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }

    @GetMapping("/test")
    public String test(){
        String test = "test";
        jwtUtils.getCleanJwtCookie();
        jwtUtils.getCleanJwtRefreshCookie();
        return test;
    }
}
