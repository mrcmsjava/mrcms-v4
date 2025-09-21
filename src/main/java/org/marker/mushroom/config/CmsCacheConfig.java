package org.marker.mushroom.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableCaching
@Configuration(proxyBeanMethods = false)
public class CmsCacheConfig {


    /**
     * 配置 EhCacheManagerFactoryBean，用于创建 EhCache 的 CacheManager
     * @return EhCacheManagerFactoryBean 实例
     */
//    @Bean
//    public EhCacheManagerFactoryBean ehcacheManagerFactoryBean() {
//        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
//        factoryBean.setConfigLocation(new ClassPathResource("config/cache/ehcache.xml"));
//        factoryBean.setShared(true);
//        return factoryBean;
//    }
//
    /**
     * 配置 EhCacheCacheManager，使用 ehcache 方法创建的 CacheManager
     * @return EhCacheCacheManager 实例
     */
//    @Bean
//    public EhCacheCacheManager cacheManager2() {
//        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
//        cacheManager.setCacheManager(ehcacheManagerFactoryBean().getObject());
//        return cacheManager;
//    }
}
