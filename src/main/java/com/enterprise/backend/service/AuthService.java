package com.enterprise.backend.service;

import com.enterprise.backend.config.JwtToken;
import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.CodeForgotPass;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ChangePasswordRequest;
import com.enterprise.backend.model.request.LoginRequest;
import com.enterprise.backend.model.request.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtToken jwtToken;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CodeForgotPassService codeForgotPassService;

    public void changePassword(ChangePasswordRequest request) {
        User userToChange = userService.getByUsernameOrEmailOrPhone(request.getUsername());
        authenticate(userToChange.getId(), request.getOldPassword());
        userToChange.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(userToChange);
    }

    public String validateUsernamePasswordAndGenToken(LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return jwtToken.generateToken(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername(), roles);
    }

    private Authentication authenticate(String username, String password) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new EnterpriseBackendException(ErrorCode.BANNED_USER);
        } catch (BadCredentialsException e) {
            throw new EnterpriseBackendException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }
    }

    public void forgotPasswordByUsername(String username) {
        User user = userService.getByUsernameOrEmailOrPhone(username);
        CodeForgotPass codeForgotPass = codeForgotPassService.generateCode(user);
        emailService.sendMailForgotPassword(codeForgotPass.getCode(), user.getEmail(), user.getFullName());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User userToChange = userService.getByUsernameOrEmailOrPhone(request.getUsername());
        codeForgotPassService.validateCode(userToChange, request.getCode());
        userToChange.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(userToChange);

    }
}
