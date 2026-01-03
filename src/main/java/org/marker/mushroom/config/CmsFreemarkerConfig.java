package org.marker.mushroom.config;

import freemarker.cache.SoftCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.marker.mushroom.core.config.impl.SystemConfig;
import org.marker.mushroom.ext.plugin.freemarker.EmbedDirectiveInvokeTag;
import org.marker.mushroom.freemarker.*;
import org.marker.mushroom.freemarker.config.WebFreeMarkerConfigurer;
import org.marker.mushroom.freemarker.wrapper.CustomObjectWrapper;
import org.marker.mushroom.utils.SpringUtils;
import org.marker.urlrewrite.freemarker.FrontURLRewriteMethodModel;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
public class CmsFreemarkerConfig {

    @Bean
    public StringTemplateLoader stringTemplateLoader() {
        return new StringTemplateLoader();
    }

    @Resource
    private SpringUtils springUtils;


    @Bean("webFrontConfiguration")
    public WebFreeMarkerConfigurer webFrontConfiguration() throws TemplateException, IOException {
        WebFreeMarkerConfigurer configurer = new WebFreeMarkerConfigurer();

        String pluginsPath = SystemConfig.getInstance().getPluginsPath();
        String adminTemplatePath = "classpath:/templates/content/";
        if (SpringUtils.isDev()) {
            adminTemplatePath = new File("").getAbsolutePath() + File.separator + "src/main/resources/templates/content/";
            log.debug("adminTemplatePath: {}", adminTemplatePath);
        }
        String themesPath = "classpath:/themes";

        // 设置模板加载路径
        configurer.setTemplateLoaderPaths(themesPath, adminTemplatePath, "classpath:/modules/", pluginsPath);

        // 设置预加载器
        configurer.setPreTemplateLoaders(stringTemplateLoader());

        // 设置 FreeMarker 配置
        Properties settings = new Properties();
        settings.setProperty("defaultEncoding", "UTF-8");
        settings.setProperty("template_update_delay", "0");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        settings.setProperty("number_format", "0.##");
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("classic_compatible", "true");
//        settings.setProperty("auto_import", "common/spring.ftl as spring");
        configurer.setFreemarkerSettings(settings);

        // 设置 FreeMarker 变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("load", new LoadDirective());
        variables.put("Bootstrap3Nav", new BootStrap3NavDirective());
        variables.put("Boostrap3Nav", new BootStrap3NavDirective());
        variables.put("Bootstrap3NavHome", new Bootstrap3NavHomeDirective());
        variables.put("HuaXiSiYuanNav", new HuaxiSiYuanNavDirective());
        variables.put("HuaXiSiYuanPCNav", new HuaxiSiYuanPCNavDirective());
        variables.put("Nav", new NavDirective());
        variables.put("NavChild", new NavChildDirective());
        variables.put("encoder", new FrontURLRewriteMethodModel());
        variables.put("Plugin", new EmbedDirectiveInvokeTag());
        variables.put("Page", new PageDirective());
        variables.put("Json", new JSONPaserDirective());
        variables.put("Dangjian", new DangjianDirective());
        configurer.setFreemarkerVariables(variables);
        configurer.afterPropertiesSet();

//        configurer.setLazyInit(true);
        return configurer;
    }


    /**
     * 后台管理系统的FreeMarker配置
     *
     * @return FreeMarkerConfigurer
     */

    // 后台Freemarker模版引擎配置
    @Bean
    @Primary
    public FreeMarkerConfigurer freemarkerConfig() throws TemplateException, IOException {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        String pluginsPath = SystemConfig.getInstance().getPluginsPath();
        configurer.setTemplateLoaderPaths("classpath:/templates/content/", pluginsPath);

        Properties settings = new Properties();
        settings.setProperty("defaultEncoding", "UTF-8");
        settings.setProperty("template_update_delay", "0");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        settings.setProperty("number_format", "0.##");
        settings.setProperty("locale", "zh_CN");
        // 自动获取spring.ftl宏，此处路径是上面templateLoaderPath的相对路径
        settings.setProperty("auto_import", "common/spring.ftl as spring");
        settings.setProperty("classic_compatible", "true");
        configurer.setFreemarkerSettings(settings);
        // freemarker变量注入
        configurer.setFreemarkerVariables(java.util.Collections.singletonMap("encoder", new FrontURLRewriteMethodModel()));
        configurer.afterPropertiesSet();
        freemarker.template.Configuration configuration = configurer.getConfiguration();

        configuration.setObjectWrapper(new CustomObjectWrapper());
        configuration.setTemplateUpdateDelayMilliseconds(1000L);// 1秒检查一次更新
        configuration.setCacheStorage(new SoftCacheStorage()); // 使用软引用缓存
        return configurer;
    }
}
