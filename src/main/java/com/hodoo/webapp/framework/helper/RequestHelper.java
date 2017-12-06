package com.hodoo.webapp.framework.helper;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;

import com.hodoo.webapp.framework.interceptor.InjectCommonModelHandlerInterceptor;
import com.hodoo.webapp.framework.model.UserOperatingSystem;

/**
 * Request 관련 Helper Static Method.
 */
public final class RequestHelper {

    private static final String AJAX_REQUEST_HEADER_VALUE = "xmlhttprequest";

    public static final String AJAX_PAGINATION_REQUEST_PARAMETER_VALUE = "ajax-pagination";

    private RequestHelper() {
        throw new IllegalAccessError("Request Helper Static Method Class.");
    }

    /**
     * HTTP Request 가 Ajax 요청인지 여부를 확인. Ajax 여부는 Request Header 의 X-Requested-With 값으로 판별.
     * @param request http request.
     * @return ajax 요청 여부.
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null && requestedWith.equalsIgnoreCase(AJAX_REQUEST_HEADER_VALUE)
                && !isAjaxPaginationRequest(request);
    }

    /**
     * 목록 페이지의 Ajax 요청을 판별.
     * @param request http request.
     * @return ajax 요청 여부.
     */
    private static boolean isAjaxPaginationRequest(HttpServletRequest request) {
        String requestMode = request.getParameter("request-mode");
        return requestMode != null && requestMode.equalsIgnoreCase(AJAX_PAGINATION_REQUEST_PARAMETER_VALUE);
    }

    /**
     * HTTP Request 의 SCHEMA 확인.
     * @param request http request.
     * @return schema (http or https)
     */
    public static String getRequestSchema(HttpServletRequest request) {
        String schema = request.getHeader("X-Forwarded-Proto");
        if (schema == null || schema.trim().isEmpty()) {
            schema = request.getScheme();
        }
        return schema.toLowerCase();
    }

    /**
     * 현재 Http Request 의 보안 Schema 여부.
     * @param request
     * @return
     */
    public static boolean isSecureSchema(HttpServletRequest request) {
        return "https".equals(getRequestSchema(request));
    }

    /**
     * HTTP Request 의 User-Agent 로 OSX 여부 확인.
     * @param request 확인하려는 http request.
     * @return MacOS 일 경우 true.
     */
    public static boolean isMacOSRequest(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (!(userAgentString == null || userAgentString.isEmpty())) {
            String lowerUserAgentString = userAgentString.toLowerCase();
            if (lowerUserAgentString.contains("os x")) {
                return true;
            }
        }

        return false;
    }

    /**
     * HTTP Request 의 User-Agent 로 OS 확인.
     * @param request 확인하려는 http request.
     * @return 사용자 OS 유형.
     */
    public static UserOperatingSystem getUserOperationSystem(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null || userAgentString.isEmpty()) {
            return UserOperatingSystem.UNKNOWN;
        }
        String lowercaseUserAgentString = userAgentString.toLowerCase();
        if (lowercaseUserAgentString.contains("iphone") || lowercaseUserAgentString.contains("ipad") || lowercaseUserAgentString.contains("ios")) {
            return UserOperatingSystem.IOS;
        }
        if (lowercaseUserAgentString.contains("android")) {
            return UserOperatingSystem.ANDROID;
        }
        if (lowercaseUserAgentString.contains("windows")) {
            return UserOperatingSystem.WINDOWS;
        }

        return UserOperatingSystem.UNKNOWN;
    }

    /**
     * HTTP Request 의 요청 IP 확인.
     * @param request http request.
     * @return 사용자 IP.
     */
    public static String getRequestRemoteIp(HttpServletRequest request) {
        String remoteIp = request.getHeader("X-Forwarded-For");

        if (remoteIp == null || remoteIp.isEmpty()) {
            remoteIp = request.getHeader("Proxy-Client-IP");
        }

        if (remoteIp == null || remoteIp.isEmpty()) {
            remoteIp = request.getHeader("WL-Proxy-Client-IP");
        }

        if (remoteIp == null || remoteIp.isEmpty()) {
            remoteIp = request.getRemoteAddr();
        }

        if (remoteIp.contains(",")) {
            String[] remoteIpParts = remoteIp.split(",");
            remoteIp = remoteIpParts[0].trim();
        }
        return remoteIp;
    }

    public static String getFileServerHost(RequestAttributes requestAttributes) {
        Object fileServerHostObject = requestAttributes.getAttribute(
                InjectCommonModelHandlerInterceptor.FILE_SERVER_HOST_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (fileServerHostObject != null) {
            String fileServerHost = fileServerHostObject.toString();
            if (!fileServerHost.isEmpty()) {
                return fileServerHost;
            }
        }
        return "";
    }

    public static String getHostUrl(HttpServletRequest request) {
        String schema = getRequestSchema(request);
        int serverPort = -1;
        String portHeader = request.getHeader("X-Forwarded-Port");
        if (!(portHeader == null || portHeader.trim().isEmpty())) {
            try {
                serverPort = Integer.parseInt(portHeader);
            } catch (NumberFormatException ignore) {
            }
        }
        if (serverPort == -1) {
            serverPort = request.getServerPort();
        }
        String urlPort;
        if (("http".equalsIgnoreCase(schema) && serverPort == 80)
                || ("https".equalsIgnoreCase(schema) && serverPort == 443)) {
            urlPort = "";
        } else {
            urlPort = ":" + serverPort;
        }
        return schema + "://" + request.getServerName() + urlPort;
    }

}
