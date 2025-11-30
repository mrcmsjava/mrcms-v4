package org.marker.mushroom.config;

import org.apache.commons.lang.StringUtils;
import org.marker.mushroom.spring.ProfileConfig;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public PropertySourcesPlaceholderConfigurer preferencesPlaceholderConfigurer() throws IOException {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setProperties(configProperties());
//        configurer.setLocations(new ClassPathResource("/config.properties") );
        return configurer;
    }

    @Bean("configProperties")
    public Properties configProperties() throws IOException {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setIgnoreResourceNotFound(true);

        List<Resource> resources = new ArrayList();
        resources.add(new ClassPathResource("/config.properties"));
        resources.add(new FileSystemResource("/etc/mrcms/config.properties"));
        String outConfigFile = profileConfig().getConfig();
        if(StringUtils.isNotBlank(outConfigFile)){
            resources.add(new FileSystemResource(outConfigFile));
        }
        factoryBean.setLocations(resources.toArray(new Resource[0]));
        // 调用 getObject 方法获取 Properties 对象
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }


    @Bean
    public ProfileConfig profileConfig() {
        return new ProfileConfig();
    }
}
