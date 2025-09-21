//package org.marker.mushroom.processor;
//
//import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.env.EnvironmentPostProcessor;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.PropertiesPropertySource;
//import org.springframework.core.env.PropertySource;
//import org.springframework.core.io.Resource;
//
//import java.util.Properties;
//
//public class MyEnvironmentPostProcessor  implements EnvironmentPostProcessor {
//
//    String[] profiles = {
//            "test.properties",
//            "bussiness.properties",
//            "blog.yml"
//    };
//
//
//    @Override
//    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        environment.getPropertySources().addLast(loadProfiles(resource));
//    }
//
//    private PropertySource<?> loadProfiles(Resource resource) {
//        if (!resource.exists()) {
//            throw new IllegalArgumentException("资源" + resource + "不存在");
//        }
//        if(resource.getFilename().contains(".yml")){
//            return loadYaml(resource);
//        } else {
//            return loadProperty(resource);
//        }
//    }
//
//
//    private PropertySource loadProperty(Resource resource){
//        try {
//
//            Properties properties = new Properties();
//            properties.load(resource.getInputStream());
//            return new PropertiesPropertySource(resource.getFilename(), properties);
//        }catch (Exception ex) {
//            throw new IllegalStateException("加载配置文件失败" + resource, ex);
//        }
//    }
//
//
//    private PropertySource loadYaml(Resource resource){
//        try {
//            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
//            factory.setResources(resource);
//
//            Properties properties = factory.getObject();
//            return new PropertiesPropertySource(resource.getFilename(), properties);
//        }catch (Exception ex) {
//            throw new IllegalStateException("加载配置文件失败" + resource, ex);
//        }
//    }
//}
