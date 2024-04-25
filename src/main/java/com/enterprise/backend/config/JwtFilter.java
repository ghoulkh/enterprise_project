package com.enterprise.backend.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class JwtFilter extends GenericFilterBean {
    private final JwtToken jwtToken;
    public JwtFilter(JwtToken jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtTokenString = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtTokenString = requestTokenHeader.substring(7);
            if (jwtTokenString.equals("null"))
                jwtTokenString = null;
            try {
                username = this.jwtToken.getUsernameFromToken(jwtTokenString);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            } catch (Exception e) {
                logger.error("invalid token!");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.jwtToken.validateToken(jwtTokenString);
//            try {
//                userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
//            } catch (BannedUserException e) {
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.getWriter().println(gson.toJson(new ErrorResponse(ErrorCode.BANNED_USER)));
//                response.setStatus(HttpStatus.FORBIDDEN.value());
//                response.getWriter().close();
//                return;
//            }

            if (userDetails != null) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
