package com.hodoo.webapp.framework.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.hodoo.webapp.framework.interceptor.BrowserCompatibilityForceDisableInterceptor;
import com.hodoo.webapp.framework.interceptor.InjectCommonModelHandlerInterceptor;

//import com.hodoo.webapp.website.model.file.FileUploadPath;


@Configuration
@EnableWebMvc
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
//    private FileUploadPath fileUploadPath;
//
//    @Autowired
//    public void setFileUploadPath(FileUploadPath fileUploadPath) {
//        this.fileUploadPath = fileUploadPath;
//    }

//    private HandlerInterceptor injectCommonModelHandlerInterceptor;
//    private HandlerInterceptor browserCompatibilityForceDisableInterceptor;
//    private HandlerMethodArgumentResolver applicationAdminArgumentResolver;
//
//    @Autowired
//    @Qualifier("injectCommonModelHandlerInterceptor")
//    public void setInjectCommonModelHandlerInterceptor(final HandlerInterceptor injectCommonModelHandlerInterceptor) {
//        this.injectCommonModelHandlerInterceptor = injectCommonModelHandlerInterceptor;
//    }
//
//    @Autowired
//    @Qualifier("browserCompatibilityForceDisableInterceptor")
//    public void setBrowserCompatibilityForceDisableInterceptor(final HandlerInterceptor browserCompatibilityForceDisableInterceptor) {
//        this.browserCompatibilityForceDisableInterceptor = browserCompatibilityForceDisableInterceptor;
//    }
//
//    @Autowired
//    @Qualifier("applicationAdminArgumentResolver")
//    public void setApplicationAdminArgumentResolver(final HandlerMethodArgumentResolver applicationAdminArgumentResolver) {
//        this.applicationAdminArgumentResolver = applicationAdminArgumentResolver;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] staticResourcePatterns = {"/favicon.ico", "/assets/**", "**/*.json", "/ckeditor/**"};

        List<InterceptorRegistration> interceptorRegistrationList = new ArrayList<>();
        interceptorRegistrationList.add(registry.addInterceptor(injectCommonModelHandlerInterceptor()));
        interceptorRegistrationList.add(registry.addInterceptor(browserCompatibilityForceDisableInterceptor()));
        // 다국어
        interceptorRegistrationList.add(registry.addInterceptor(localeChangeInterceptor()));

        //정적 리소스에 대해서는 interceptor에서 제외
        for (InterceptorRegistration registration : interceptorRegistrationList) {
            registration.excludePathPatterns(staticResourcePatterns);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
        .addResourceLocations("/resources/favicon.ico")
        .resourceChain(true);

        registry.addResourceHandler("/robots.txt")
        .addResourceLocations("/robots.txt")
        .resourceChain(true);

        registry.addResourceHandler("/assets/**")
        .addResourceLocations("/assets/")
        .resourceChain(true);
//	Resource 접근 개선        
//        .addResolver(new GzipResourceResolver())
//        .addResolver(new PathResourceResolver());



//        registry.addResourceHandler(fileUploadPath.getRelativePath() + "/**")
//        .addResourceLocations("file:" + fileUploadPath.getAbsolutePath() + "/")
//        .resourceChain(true);

    }


      @Override
      public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//    	  super.addArgumentResolvers(argumentResolvers);
//          argumentResolvers.add(new ApplicationUserArgumentResolver());
      }
      
//    @Override
//    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
//        super.addArgumentResolvers(argumentResolvers);
//        argumentResolvers.add(applicationAdminArgumentResolver);
//    }
      
      
      // 다국어
      @Bean
      public LocaleResolver localeResolver() {
          SessionLocaleResolver slr = new SessionLocaleResolver();
          slr.setDefaultLocale(Locale.KOREAN);
          return slr;
      }
      
      // 다국어
      @Bean
      public LocaleChangeInterceptor localeChangeInterceptor() {
          LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
          lci.setParamName("lang");
          return lci;
      }
      
      /**
       * 요청에 대한 공통 정보 주입 빈
       *
       * @return {@link InjectCommonModelHandlerInterceptor} 객체
       */
      @Bean
      public HandlerInterceptor injectCommonModelHandlerInterceptor() {
          return new InjectCommonModelHandlerInterceptor();
      }
      
      /**
       * IE 브라우저 호환성 보기 무력화 빈
       *
       * @return {@link BrowserCompatibilityForceDisableInterceptor} 객체
       */
      @Bean
      public HandlerInterceptor browserCompatibilityForceDisableInterceptor() {
          return new BrowserCompatibilityForceDisableInterceptor();
      }    
         
}
