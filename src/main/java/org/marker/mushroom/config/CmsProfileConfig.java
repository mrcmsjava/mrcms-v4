package org.marker.mushroom.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.Properties;


/**
 * CMS Profile 配置
 *
 * @author marker
 * @create 2025-01-23 11:26
 **/
@Configuration
public class CmsProfileConfig {



//    @Bean
//    public PropertiesFactoryBean propertiesFactoryBean() {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocations(new ClassPathResource("/config.properties"), new FileSystemResource("/etc/mrcms/config.properties"));
//        return propertiesFactoryBean;
//    }

//
    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer()     {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setLocations(new ClassPathResource("/config.properties") );
        return configurer;
    }
    @Bean
    public PropertySourcesPlaceholderConfigurer preferencesPlaceholderConfigurer()     {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setLocations(new ClassPathResource("/config.properties") );
        return configurer;
    }

    @Bean("configProperties")
    public Properties configProperties() throws IOException {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setIgnoreResourceNotFound(true);
        factoryBean.setLocations(new ClassPathResource("/config.properties"), new FileSystemResource("/etc/mrcms/config.properties"));
        // 调用 getObject 方法获取 Properties 对象
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
