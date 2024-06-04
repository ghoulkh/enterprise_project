package com.enterprise.backend.config;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.model.CustomOAuth2User;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OauthHandle implements AuthenticationSuccessHandler {
    private final JwtToken jwtToken;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        String email = "";
        String name = "";
        String avatar = "";
        if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser oauthUser = (DefaultOidcUser) principal;
            email = oauthUser.getAttribute("email");
            name = oauthUser.getAttribute("name");
            avatar = oauthUser.getAttribute("picture");
        } else if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User oauthUser = (CustomOAuth2User) principal;
            email = oauthUser.getEmail();
            name = oauthUser.getName();
            avatar = oauthUser.getAvatar();
        }

        processOAuthPostLogin(email, name, avatar);
        List<String> roles = new ArrayList<>();
        roles.add(AuthoritiesConstants.ROLE_USER);

        final String token = jwtToken.generateToken(email, roles);

        httpServletResponse.sendRedirect("http://localhost:3000/login?token=" + token);
    }

    public void processOAuthPostLogin(String username, String name, String avaUrl) {
        Optional<User> existUser = userRepository.findById(username);

        if (existUser.isEmpty()) {
            User user = new User();
            user.setId(username);
            user.setActive(true);
            user.setAvaUrl(avaUrl);
            user.setFullName(name);
            userRepository.save(user);
        }
    }
}
