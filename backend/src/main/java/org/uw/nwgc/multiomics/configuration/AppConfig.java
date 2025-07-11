package org.uw.nwgc.multiomics.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${spring.mvc.servlet.path:/}")
    private String servletPath;

    public String getServletPath() {
        return "/".equals(servletPath) ? "" : servletPath;
    }
}