package com.hodoo.webapp.framework.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * IE 에서의 브라우져 호환성 보기를 무력화 하기 위해 Response Header 에 값을 추가하는 Interceptor.
 */
public class BrowserCompatibilityForceDisableInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.toUpperCase().contains("MSIE")) {
            response.setHeader("X-UA-Compatible", "IE=edge");
        }
    }
}
