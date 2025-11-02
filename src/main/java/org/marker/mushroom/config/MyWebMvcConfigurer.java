package org.marker.mushroom.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.marker.mushroom.interceptor.RequestParamsInterceptor;
import org.marker.mushroom.interceptor.SignInterceptor;
import org.marker.urlrewrite.freemarker.FrontURLRewriteMethodModel;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullNumberAsZero;

@EnableWebMvc
@EnableAsync
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.freeMarker().cache(true);
        registry.freeMarker().prefix("");
        registry.freeMarker().suffix(".html");

    }


    ///受理.do请求，不发生302重定向
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .parameterName("format")
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("html", MediaType.TEXT_HTML)
                .mediaType("txt", MediaType.TEXT_PLAIN)
                .mediaType(".do", MediaType.ALL);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor());
        registry.addInterceptor(new SignInterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(new RequestParamsInterceptor()).addPathPatterns("/**");


        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                // 获取原始请求URI
                String originalUri = request.getRequestURI();
                // 修改URI，添加后缀
                String modifiedUri = originalUri.replace(".do", "")  ;
                // 重写请求URI（注意：这种方式实际上不会改变原始请求的URI，只是改变了后续处理的逻辑）
                request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, modifiedUri);
                return true;
            }
        });

    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("/upload/");
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/robots.txt").addResourceLocations("classpath:/static/robots.txt");
        registry.addResourceHandler("/install/**").addResourceLocations("classpath:/templates/content/");

    }

//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
//        converters.add(mappingJackson2HttpMessageConverter);
//    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML));
        fastJsonHttpMessageConverter.setFeatures(WriteMapNullValue, WriteNullNumberAsZero);
        converters.add(fastJsonHttpMessageConverter);

        converters.add(new StringHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
    }

    /**
     * -设置url后缀模式匹配规则
     * -该设置匹配所有的后缀，使用.do或.action都可以
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true)    //设置是否是后缀模式匹配,即:/test.*
                .setUseTrailingSlashMatch(true)     //设置是否自动后缀路径模式匹配,即：/test/
                .setUseRegisteredSuffixPatternMatch(true);  //开启路径后缀匹配
    }

    /**
     * -该设置严格指定匹配后缀*.do或.action，但有风险
     * 会对url访问产生较大的影响，不建议使用；
     *
     * @param dispatcherServlet servlet调度器
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> servletServletRegistrationBean = new ServletRegistrationBean<>(dispatcherServlet);
        servletServletRegistrationBean.addUrlMappings("*.do", "*.action");
        return servletServletRegistrationBean;
    }
    @Bean
    public ResourceBundleMessageSource messageSource( ) {
        ResourceBundleMessageSource  messageSource = new ResourceBundleMessageSource()  ;
        messageSource.setDefaultEncoding("utf-8");
        messageSource.setBasenames("config/international/messages","config/validation/validation");
        return messageSource;
    }


    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置异步支持，例如超时时间等
//        configurer.setDefaultTimeout(5000);
    }
}
