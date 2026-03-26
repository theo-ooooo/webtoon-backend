package com.webtoon.domain.auth.service;

import com.webtoon.domain.auth.dto.request.LoginRequest;
import com.webtoon.domain.auth.dto.request.SignupRequest;
import com.webtoon.domain.auth.dto.response.LoginResponse;
import com.webtoon.domain.user.entity.User;
import com.webtoon.domain.user.repository.UserRepository;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import com.webtoon.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().name());

        return new LoginResponse(token, new LoginResponse.UserInfo(
                user.getId(), user.getEmail(), user.getNickname(),
                user.getRole().name(), user.getCoinBalance()
        ));
    }

    public LoginResponse.UserInfo getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return new LoginResponse.UserInfo(
                user.getId(), user.getEmail(), user.getNickname(),
                user.getRole().name(), user.getCoinBalance()
        );
    }
}
