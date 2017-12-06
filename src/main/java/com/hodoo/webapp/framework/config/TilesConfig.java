package com.hodoo.webapp.framework.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
public class TilesConfig {

    @Bean
    public TilesConfigurer tilesConfigurer() {
        final TilesConfigurer configurer = new TilesConfigurer();
        configurer.setDefinitions(new String[] {"WEB-INF/tiles/tiles.xml"});
        configurer.setCheckRefresh(true);
        return  configurer;
    }

    @Bean
    public TilesViewResolver tilesViewResolver() {
        final TilesViewResolver tilesViewResolver = new TilesViewResolver();
        Properties props = new Properties();
        tilesViewResolver.setAttributes(props);
        tilesViewResolver.setViewClass(TilesView.class);
        return tilesViewResolver;
    }

    @Bean
    public LiteDeviceDelegatingViewResolver liteDeviceDelegatingViewResolver() {
        LiteDeviceDelegatingViewResolver resolver = new LiteDeviceDelegatingViewResolver(this.tilesViewResolver());
        resolver.setMobilePrefix("mobile/");
        resolver.setNormalPrefix("normal/");
        resolver.setEnableFallback(true);
        return resolver;
    }

}
