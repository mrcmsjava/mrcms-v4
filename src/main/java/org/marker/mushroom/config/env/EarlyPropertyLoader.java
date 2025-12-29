package org.marker.mushroom.config.env;

import org.marker.mushroom.core.config.impl.SystemBaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



/**
 * springboot底层配置自定义
 * 在springboot 条件判断前将配置加载至内存
 * @author marker
 *
 */
public class EarlyPropertyLoader implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment,
            SpringApplication application) {

        // 从外部文件加载配置
        loadFromExternalFile(environment);
    }

    private void loadFromExternalFile(ConfigurableEnvironment environment) {
        try {
            // 读取自定义 properties 文件
            String configFile = SystemBaseConfig.getCustomConfigFile();
            Resource resource = new FileSystemResource(configFile);
            if (resource.exists()) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);

                // 转换为 Spring 的 PropertySource
                Map<String, Object> map = new HashMap<>();
                properties.forEach((key, value) ->
                        map.put(key.toString(), value.toString()));

                environment.getPropertySources().addFirst(
                        new MapPropertySource("earlyProperties", map)
                );
                System.out.println("已加载早期配置: " + configFile);
            }
        } catch (IOException e) {
            // 静默处理，文件不存在时忽略
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;// 设置高优先级，确保最先执行
    }


}