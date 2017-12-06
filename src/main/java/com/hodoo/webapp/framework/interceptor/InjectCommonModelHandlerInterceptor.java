package com.hodoo.webapp.framework.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.hodoo.webapp.framework.helper.RequestHelper;

/**
 * 사이트에 필요한 공통적인 모델 주입하는 interceptor.
 * @author Nomad (nomad@etribe.co.kr)
 */
public class InjectCommonModelHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final String ADMIN_VIEW_NAME_PREFIX = "admin";

    private static final String ADMIN_BASE_URL_ATTRIBUTE = "adminBaseUrl";

    public static final String FILE_SERVER_HOST_ATTRIBUTE = "fileServerHost";

    private String fileServerHost;

    private String adminBaseUrl;

    @Value("${host.file-server}")
    public void setFileServerHost(String fileServerHost) {
        if (fileServerHost == null || fileServerHost.isEmpty()) {
            return;
        }
        this.fileServerHost = fileServerHost;
    }

    @Value("${admin.base.url}")
    public void setAdminBaseUrl(String adminBaseUrl) {
        if (adminBaseUrl == null || adminBaseUrl.isEmpty()) {
            return;
        }
        this.adminBaseUrl = adminBaseUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (fileServerHost == null) {
            request.setAttribute(FILE_SERVER_HOST_ATTRIBUTE, "");
        } else {
            request.setAttribute(FILE_SERVER_HOST_ATTRIBUTE, fileServerHost);
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        super.postHandle(request, response, handler, modelAndView);

        if (modelAndView == null) {
            return;
        }

        if (!isHtmlView(modelAndView) || (RequestHelper.isAjaxRequest(request))) {
            return;
        }

        if (isHtmlView(modelAndView) && modelAndView.getViewName().startsWith(ADMIN_VIEW_NAME_PREFIX)) {
            if (adminBaseUrl == null) {
                modelAndView.addObject(ADMIN_BASE_URL_ATTRIBUTE, "/admin");
            } else {
                modelAndView.addObject(ADMIN_BASE_URL_ATTRIBUTE, adminBaseUrl);
            }
        }

        Locale currentLocale = LocaleContextHolder.getLocale();
        modelAndView.addObject("currentLanguage", currentLocale.getLanguage());
        modelAndView.addObject("currentSchema", RequestHelper.getRequestSchema(request));
    }

    private boolean isHtmlView(ModelAndView modelAndView) {
        return !(modelAndView == null
                || modelAndView.getViewName() == null
                || modelAndView.getViewName().startsWith("redirect:")
                || modelAndView.getViewName().startsWith("forward:")
                || modelAndView.getView() instanceof RedirectView);
    }
}
