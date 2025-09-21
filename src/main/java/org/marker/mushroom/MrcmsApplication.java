package org.marker.mushroom;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.marker.mushroom.servlet.CmsDispatcherServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication( )
public class MrcmsApplication     {

	public static void main(String[] args) {
		SpringApplication.run(MrcmsApplication.class, args);
	}


	@Bean
	public DispatcherServlet dispatcherServlet() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		return dispatcherServlet;
	}


	// DispatcherServlet 配置
	@Bean
	public ServletRegistrationBean<DispatcherServlet> mushroomServlet() {
		ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(
				dispatcherServlet(), "*.do" , "/themes/*");
		registration.setName("mushroomAdmin");
		registration.setLoadOnStartup(2);
		registration.setAsyncSupported(true);
		return registration;
	}


}
