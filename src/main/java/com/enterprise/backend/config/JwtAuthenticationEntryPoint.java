package com.enterprise.backend.config;

import com.google.gson.Gson;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.response.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;
    private static final Gson gson = new Gson();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().println(gson.toJson(new ErrorResponse(ErrorCode.UNAUTHORIZED)));
        response.getWriter().close();
    }
}

