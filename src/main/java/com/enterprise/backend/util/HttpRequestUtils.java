package com.enterprise.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@UtilityClass
public class HttpRequestUtils {

	private static final String X_REAL_IP_HEADER = "x-real-ip";
	private static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for";

	public static String getRequestUrl(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String query = request.getQueryString();
		if(!ObjectUtils.isEmpty(query)) url += "?" + query;
		int idx = (url = url.toLowerCase()).indexOf("password");
		if(idx > 0) return url.substring(0, idx) + "[SENSITIVE DATA - PWD]";

		idx = (url = url.toLowerCase()).indexOf("token=");
		if(idx > 0) return url.substring(0, idx) + "[SENSITIVE DATA - TOKEN]";

		idx = (url = url.toLowerCase()).indexOf("token-id" + "=");
		if (idx > 0) return url.substring(0, idx) + "[SENSITIVE DATA - TOKEN-ID]";

		idx = (url = url.toLowerCase()).indexOf("session=");
		if (idx > 0) return url.substring(0, idx) + "[SENSITIVE DATA - SESSION]";

		return url;
	}

	public static String getRealIp(HttpServletRequest request) {
		Enumeration<String> _enum = request.getHeaderNames();

		String forwardFor = null;

		while (_enum.hasMoreElements()) {
			String header = _enum.nextElement();
			if (X_REAL_IP_HEADER.equalsIgnoreCase(header)) {
				return request.getHeader(header);
			} else if(X_FORWARDED_FOR_HEADER.equalsIgnoreCase(header)) {
				forwardFor = request.getHeader(header);
			}
		}
		return ObjectUtils.isEmpty(forwardFor) ? request.getRemoteAddr() : forwardFor;
	}

}
