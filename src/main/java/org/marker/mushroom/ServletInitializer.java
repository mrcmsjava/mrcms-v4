package org.marker.mushroom;

import jakarta.servlet.Registration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.marker.mushroom.servlet.CmsDispatcherServlet;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		return application.sources(MrcmsApplication.class);


	}
}
