package org.marker.mushroom.config;

import org.marker.mushroom.core.DataSourceProxy;
import org.marker.mushroom.core.config.impl.*;
import org.marker.mushroom.holder.InitBuilderHolder;
import org.marker.mushroom.holder.SpringContextHolder;
import org.marker.mushroom.holder.WebRealPathHolder;
import org.marker.mushroom.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import static org.marker.mushroom.core.DataSourceProxy.DATASOURCE_PROXY_BEAN_NAME;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class CmsCoreConfig {


    /**
     * 定义 SpringContextHolder Bean，通过该 Bean 可以获取 applicationContext 中的数据信息
     * @return SpringContextHolder 实例
     */
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }


    /**
     * 定义 WebRealPathHolder Bean
     * @return WebRealPathHolder 实例
     */
    @Bean
    public WebRealPathHolder webRealPathHolder() {
        return new WebRealPathHolder();
    }

    /**
     * 定义 InitBuilderHolder Bean
     * @return InitBuilderHolder 实例
     */
    @Bean
    public InitBuilderHolder mushRoomInitBuildHolder() {
        return new InitBuilderHolder();
    }


    @Bean(name = DATASOURCE_PROXY_BEAN_NAME)
    @Conditional(value = { DataSourceProxy.DataSourceProxyCondition.class})
//    @ConditionalOnProperty(value =   "mrcms.install", matchIfMissing = true )
    public DataSourceProxy dataSourceProxy() {
        return new DataSourceProxy(null);
    }




    /**
     * 定义 URL 重写规则引擎配置组件 Bean
     * 注意：按照规范将 id 从 'URLRewriteConfig' 改为 'urlRewriteConfig'
     * @return URLRewriteConfig 实例
     */
    @Bean
    public URLRewriteConfig urlRewriteConfig() {
        return new URLRewriteConfig();
    }

    /**
     * 系统基础配置
     * @return
     */
    @Bean
    public SystemBaseConfig systemBaseConfig() {
        return new SystemBaseConfig();
    }

    /**
     * 定义系统配置组件 Bean
     * @return SystemConfig 实例
     */
    @Bean
    public SystemConfig systemConfig(@Autowired SpringContextHolder springContextHolder) {
        return new SystemConfig();
    }

    /**
     * 定义存储配置组件 Bean
     * @return StorageConfig 实例
     */
    @Bean
    public StorageConfig storageConfig() {
        return new StorageConfig();
    }

    /**
     * 定义 OpenAI 配置组件 Bean
     * @return OpenAIConfig 实例
     */
    @Bean
    public OpenAIConfig openAiConfig() {
        return new OpenAIConfig();
    }

}
