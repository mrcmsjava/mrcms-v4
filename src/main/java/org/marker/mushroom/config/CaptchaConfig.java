package org.marker.mushroom.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @SneakyThrows
    @Bean
    public Producer kaptchaProducer() {
        // 强制加载AWT颜色类
        Class.forName("java.awt.Color");

        Properties props = new Properties();
        props.put("kaptcha.textproducer.font.color", "black");
        props.put("kaptcha.textproducer.char.length", "4");
        Config config = new Config(props);
        return config.getProducerImpl();
    }
}
