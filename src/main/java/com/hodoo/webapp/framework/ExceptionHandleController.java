package com.hodoo.webapp.framework;

import java.io.IOException;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.hodoo.webapp.framework.exception.NoRecordException;

//import com.junglim.webapp.website.security.ApplicationAdmin;

@Controller
@ControllerAdvice
public class ExceptionHandleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandleController.class);

    //    private String fileServerHost;
    //
    //    @Value("${host.file-server}")
    //    public void setFileServerHost(String fileServerHost) {
    //        if (fileServerHost == null || fileServerHost.isEmpty()) {
    //            this.fileServerHost = null;
    //            return;
    //        }
    //        this.fileServerHost = fileServerHost;
    //    }

    @RequestMapping("/admin/health-check")
    public void healthCheck(ModelMap model, HttpServletRequest request, Device device) {
        String deviceType = "unknown";
        if (device.isNormal()) {
            deviceType = "normal";
        } else if (device.isMobile()) {
            deviceType = "mobile";
        } else if (device.isTablet()) {
            deviceType = "tablet";
        }

        model.addAttribute("healthStatus", deviceType);
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            model.addAttribute(parameterName, request.getParameter(parameterName));
        }
    }
    
    @ExceptionHandler(NoRecordException.class)
    public ModelAndView handleNoRecordException(HttpServletRequest request, HttpServletResponse response,
            NoRecordException exception) {

        response.setStatus(HttpStatus.NO_CONTENT.value());
        request.setAttribute("errMsg", "No Content");
        return getExceptionModelAndView(request, response, exception, "error/exception");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(HttpServletRequest request, HttpServletResponse response,
            NoRecordException exception) {

        response.setStatus(HttpStatus.NO_CONTENT.value());
        request.setAttribute("errMsg", "No Content");
        return getExceptionModelAndView(request, response, exception, "error/exception");
    }



    /**
     * 접근 URL 유효성 체크 ExceptionHandler.
     * @param request ServletRequest
     * @param response ServletResponse
     * @param exception 예외 객체
     * @throws ServletException
     * @throws IOException
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(HttpServletRequest request, HttpServletResponse response,
            Exception exception) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return getExceptionModelAndView(request, response, exception, "error/404");
    }

    @ExceptionHandler
    public ModelAndView handleDefaultException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        /** Socket Exception 의 경우에는 단순 정보 로그만 남도록 수정. */
        if (exception instanceof SocketException
                || exception.getCause() instanceof SocketException
                || exception.getCause() instanceof SSLException) {
            LOGGER.info("Socket Exception", exception.getMessage());
            return getExceptionModelAndView(request, response, exception);
        }

        /**
         * Java Agent 에 의한 Connection 확인 Request 일 경우에 대한 처리.
         */
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.matches("Java/[0-9_.]+")
                && exception instanceof HttpRequestMethodNotSupportedException) {
            return getExceptionModelAndView(request, response, exception);
        }

        String lowerRequestUrl = request.getRequestURL().toString().toLowerCase();
        if (lowerRequestUrl.contains("/favicon.ico")
                || lowerRequestUrl.contains("robots.txt")
                || lowerRequestUrl.contains("masiteinfo.ini")) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        writeExceptionLoggerMessage(request, exception);

        return getExceptionModelAndView(request, response, exception);
    }

    private ModelAndView getExceptionModelAndView(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        return getExceptionModelAndView(request, response, exception, "error/exception");
    }

    private ModelAndView getExceptionModelAndView(HttpServletRequest request, HttpServletResponse response, Exception exception, String viewName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("result", "fail");
        modelAndView.addObject("exception", getShortExceptionMessage(exception.getMessage()));
        if (request != null && request.getHeader("Accept") != null && request.getHeader("Accept").contains("json")) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } else {
            String previousUrl = getPreviousUrl(request);

            //            Locale locale = LocaleContextHolder.getLocale();
            //            modelAndView.addObject("exceptionCurrentLanguage", locale.getLanguage());
            //            modelAndView.addObject("exceptionCurrentCountry", locale.getCountry().toLowerCase());

            //            if (fileServerHost == null) {
            //                modelAndView.addObject("exceptionFileServerHost", "");
            //            } else {
            //                modelAndView.addObject("exceptionFileServerHost", fileServerHost);
            //            }

            modelAndView.addObject("previousUrl", previousUrl);
            modelAndView.setViewName(viewName);
        }
        return modelAndView;
    }

    private String getShortExceptionMessage(String paramterMessage) {
        String message = paramterMessage;
        if (message == null) {
            return "";
        }

        message = message.trim();
        int firstRowDelimiter = message.indexOf("\n");
        if (firstRowDelimiter > -1) {
            return message.substring(0, firstRowDelimiter);
        }
        return message;
    }

    private void writeExceptionLoggerMessage(HttpServletRequest request, Exception exception) {
        StringBuilder message = new StringBuilder();
        if (request == null) {
            message.append("\n\n[!! Request is NULL !!]");
        } else {
            message.append("\n\nRequestURL: \"").append(request.getRequestURL()).append("\"");
        }

        message.append("\n\nException Message: \"");
        if (exception != null) {
            if (exception instanceof NoHandlerFoundException) {
                message.append("No handler found");
            } else {
                message.append(getShortExceptionMessage(exception.getMessage()));
            }
        }
        message.append("\"\n");

        if (request != null) {
            if (request.getUserPrincipal() != null) {
                message.append("\nPrincipal: ").append(request.getUserPrincipal().toString()).append("\n");
            }
            message.append("\n*************************************************************")
            .append("\nREQUEST")
            .append("\n*************************************************************\n");
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    message.append("\n").append(headerName.toUpperCase())
                    .append(": \"").append(request.getHeader(headerName)).append("\"");
                }
            }

            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null && parameterMap.size() > 0) {
                message.append("\n\nMethod: ").append(request.getMethod().toUpperCase())
                .append("\nParameters:");
                for (String key : parameterMap.keySet()) {
                    message.append("\n[ ").append(key).append(" ] => [ ");
                    boolean isFirst = true;
                    for (String value : parameterMap.get(key)) {
                        if (!isFirst) {
                            message.append(" , ");
                        }
                        message.append(value);
                        isFirst = false;
                    }
                    message.append(" ]");
                }
            }
        }

        if (exception == null) {
            LOGGER.error(message.toString());
        } else {
            message.append("\n");
            if (NoHandlerFoundException.class.isAssignableFrom(exception.getClass())) {
                message.append("##### No Handler Found Exception #####");
                LOGGER.error(message.toString());
            } else if (HttpRequestMethodNotSupportedException.class.isAssignableFrom(exception.getClass())) {
                message.append("##### Not Supported Request Method Exception #####");
                LOGGER.error(message.toString());
            } else {
                message.append("\n*************************************************************")
                .append("\nEXCEPTION DETAIL")
                .append("\n*************************************************************\n\n");
                LOGGER.error(message.toString(), exception);
            }
        }
    }

    private String getPreviousUrl(HttpServletRequest request) {
        String defaultUrl = "/";
        if (request == null) {
            return defaultUrl;
        }

        String previousUrl = request.getHeader("Referer");
        if (previousUrl == null || previousUrl.isEmpty()) {
            return defaultUrl;
        }

        return previousUrl;
    }

//    @RequestMapping("/error/deny")
//    public String deniedException(Model model, HttpServletRequest request){
//        return "error/exception";
//    }

}

