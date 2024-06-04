package com.enterprise.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class HttpRequestUtils {
    private static final String X_REAL_IP_HEADER = "x-real-ip";
    private static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for";

    public static String getRequestUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String query = request.getQueryString();
        if (!ObjectUtils.isEmpty(query)) {
            url += "?" + query;
        }

        url = url.toLowerCase();

        String[] sensitiveKeywords = {"password", "token=", "token-id=", "session="};
        String[] sensitiveReplacements = {
                "[SENSITIVE DATA - PWD]",
                "[SENSITIVE DATA - TOKEN]",
                "[SENSITIVE DATA - TOKEN-ID]",
                "[SENSITIVE DATA - SESSION]"
        };

        for (int i = 0; i < sensitiveKeywords.length; i++) {
            int idx = url.indexOf(sensitiveKeywords[i]);
            if (idx > 0) {
                return url.substring(0, idx) + sensitiveReplacements[i];
            }
        }

        return url;
    }

    public static String getRealIp(HttpServletRequest request) {
        String xRealIp = request.getHeader(X_REAL_IP_HEADER);
        if (!ObjectUtils.isEmpty(xRealIp)) {
            return xRealIp;
        }

        String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (!ObjectUtils.isEmpty(xForwardedFor)) {
            return xForwardedFor;
        }

        return request.getRemoteAddr();
    }
}
