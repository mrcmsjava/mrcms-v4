package org.marker.mushroom.config;

import org.apache.commons.lang.StringUtils;
import org.marker.mushroom.core.config.impl.SystemBaseConfig;
import org.marker.mushroom.utils.PathUtils;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

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

//   提供该propertyConfigurer bean支持把properties文件中的信息读取到配置文件的表达式中
//    @Bean
//    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer()     {
//        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
//        configurer.setIgnoreResourceNotFound(true);
//        configurer.setLocations(new ClassPathResource("/config.properties") );
//        return configurer;
//    }

    @Bean
    public PropertySourcesPlaceholderConfigurer preferencesPlaceholderConfigurer() throws IOException {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setProperties(configProperties());
        return configurer;
    }

    @Bean("configProperties")
    public Properties configProperties() throws IOException {
        return propertiesFactoryBean().getObject();
    }

    /**
     * 系统基本配置
     * @return
     * @throws IOException
     */
    @Bean("propertiesFactoryBean")
    public PropertiesFactoryBean propertiesFactoryBean() throws IOException {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setIgnoreResourceNotFound(true); // 忽略配置不存在

        List<Resource> resources = new ArrayList();
        resources.add(0, new ClassPathResource("/config.properties"));
        // 此处加载与Config引擎加载有所区别，基于spring PropertiesFactoryBean加载的
        String customConfigFile = SystemBaseConfig.getCustomConfigFile();
        if (StringUtils.isNotBlank(customConfigFile)) {
            resources.add(1, new FileSystemResource(customConfigFile)); // 优先级最高
        }
        factoryBean.setLocations(resources.toArray(new Resource[0]));
        // 调用 getObject 方法获取 Properties 对象
        factoryBean.afterPropertiesSet();
        return factoryBean ;
    }



}
