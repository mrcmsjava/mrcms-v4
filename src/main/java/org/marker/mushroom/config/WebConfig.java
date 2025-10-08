package org.marker.mushroom.config;

import org.marker.mushroom.listener.SessionCounter;
import org.marker.mushroom.servlet.CmsDispatcherServlet;
import org.marker.mushroom.servlet.FetchServlet;
import org.marker.mushroom.servlet.SecurityCodeServlet;
import org.marker.mushroom.servlet.WeixinServlet;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;



@AutoConfigureOrder(Integer.MIN_VALUE)
@AutoConfiguration(
        before = {DispatcherServletAutoConfiguration.class}
)
@ConditionalOnWebApplication(
        type = ConditionalOnWebApplication.Type.SERVLET
)
@ConditionalOnClass({DispatcherServlet.class})
public class WebConfig         {

    // 欢迎页面配置
//    @Bean
//    public WelcomePageHandlerMapping welcomePageHandlerMapping() {
//        return new WelcomePageHandlerMapping("/cms");
//    }

    // Spring 上下文监听器
//    @Bean
//    public ContextLoaderListener contextLoaderListener() {
//        return new ContextLoaderListener();
//    }

    // Logback 配置监听器
//    @Bean
//    public LogbackConfigListener logbackConfigListener() {
//        return new LogbackConfigListener();
//    }

    // 在线人数统计监听器
    @Bean
    public SessionCounter sessionCounter() {
        return new SessionCounter();
    }
//    /**
//     * 关键：配置支持异步的过滤器
//     */
//    @Bean
//    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
//        FilterRegistrationBean<RequestContextFilter> registrationBean =
//                new FilterRegistrationBean<>();
//        registrationBean.setFilter(new RequestContextFilter());
//        registrationBean.setAsyncSupported(true);
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return registrationBean;
//    }


    // 其他 Servlet 配置
    @Bean
    public ServletRegistrationBean<CmsDispatcherServlet> indexServlet() {
        return new ServletRegistrationBean<>( new CmsDispatcherServlet(), "/cms");
    }

    @Bean
    public ServletRegistrationBean<WeixinServlet> weixinServlet() {
        return new ServletRegistrationBean<>(
            new WeixinServlet(), "/api/open/wechat/callback");
    }

//    @Bean
//    public ServletRegistrationBean<SecurityCodeServlet> securityCodeServlet() {
//        return new ServletRegistrationBean<>(
//            new SecurityCodeServlet(), "/SecurityCode");
//    }

    @Bean
    public ServletRegistrationBean<FetchServlet> fetchServlet() {
        return new ServletRegistrationBean<>(
            new FetchServlet(), "/fetch");
    }



    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
        return new ContentNegotiatingViewResolver();
    }


    @Bean
    public FreeMarkerViewResolver viewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(false);
        resolver.setPrefix("");
        resolver.setSuffix(".html");
        resolver.setContentType("text/html; charset=utf-8");
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setExposeRequestAttributes(true);
        resolver.setExposeSessionAttributes(true);
        resolver.setRequestContextAttribute("req");
        return resolver;
    }

}
