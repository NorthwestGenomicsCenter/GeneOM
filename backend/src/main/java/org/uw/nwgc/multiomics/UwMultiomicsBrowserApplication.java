package org.uw.nwgc.multiomics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class UwMultiomicsBrowserApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(UwMultiomicsBrowserApplication.class, args);
	}

	// Needed to start the application in a war file
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(UwMultiomicsBrowserApplication.class);
	}
}
