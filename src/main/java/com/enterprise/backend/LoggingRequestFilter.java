package com.enterprise.backend;

import com.enterprise.backend.util.HttpRequestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingRequestFilter extends GenericFilterBean {

    private static final String[] IGNORE_PATHS = {
            "/resources/", "favicon.ico"
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = HttpRequestUtils.getRequestUrl(request);

        if (request.getMethod().equals("OPTIONS")) {
            try {
                response.getWriter().print("OK");
                response.getWriter().flush();
            } catch (IOException e) {
                logger.error(e.toString(), e);
            }
            return;
        }


        if (isIgnorePath(url)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        String referer = request.getHeader("Referer");
        String realIp = HttpRequestUtils.getRealIp(request);
        logger.info("Real: " + realIp + " - Remote: " + request.getRemoteAddr() + " - " + (ObjectUtils.isEmpty(referer) ? "[No Refer]" : referer) + " - " + url);
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            logger.info("Real: " + realIp + " - Remote: " + request.getRemoteAddr() + " - " + url + " - " + response.getStatus() + " - " + (System.currentTimeMillis() - start));
        }
    }

    private boolean isIgnorePath(String url) {
        for (String item : IGNORE_PATHS) {
            if (url.contains(item)) return true;
        }
        return false;
    }
}