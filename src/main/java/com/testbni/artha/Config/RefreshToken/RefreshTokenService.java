package com.testbni.artha.Config.RefreshToken;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testbni.artha.Model.Validation;
import com.testbni.artha.Repository.UserRepository;
import com.testbni.artha.Repository.ValidationRepository;
import com.testbni.artha.Util.Exception.TokenRefreshException;


@Service
public class RefreshTokenService {
  @Value("${artha.test.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private ValidationRepository validationRepository;

  @Autowired
  private UserRepository userRepository;

  public Optional<Validation> findByToken(String token) {
    return validationRepository.findByValidationCode(token);
  }

  public Validation createRefreshToken(Long userId) {
    Validation refreshToken = new Validation();

    refreshToken.setUser(userRepository.findById(userId).get());
    refreshToken.setExpirationTime(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setValidationCode(UUID.randomUUID().toString());
    refreshToken.setCreatedDate(new Date());
    refreshToken.setEmail(userRepository.findById(userId).get().getEmail());

    refreshToken = validationRepository.save(refreshToken);
    return refreshToken;
  }

  public Validation verifyExpiration(Validation token) {
    if (token.getExpirationTime().compareTo(Instant.now()) < 0) {
      validationRepository.delete(token);
      throw new TokenRefreshException(token.getValidationCode(), "Refresh token was expired. Please make a new signin/signup request");
    }

    return token;
  }

  @Transactional
  public int deleteByUserId(Long userId) {
    return validationRepository.deleteByUser(userRepository.findById(userId).get());
  }
}
