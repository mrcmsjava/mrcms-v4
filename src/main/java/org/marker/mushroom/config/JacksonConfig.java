package org.marker.mushroom.config;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");



    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        // 使用较新的Jackson2ObjectMapperBuilderCustomizer接口
        return builder -> {


            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            builder
//                .serializerByType(Long.class, ToStringSerializer.instance)
//                .serializerByType(LocalDateTime.class, 	 LocalDateTimeSerializer.INSTANCE)
//                .serializerByType(LocalDate.class, 		new LocalDateSerializer(DATE_FORMATTER))
//                .serializerByType(LocalTime.class, 		new LocalTimeSerializer(TIME_FORMATTER))
//                .serializerByType(LocalTime.class, 		new LocalTimeSerializer(LOCAL_TIME_FORMATTER))
////                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER))
//                .deserializerByType(LocalDate.class, 	 new LocalDateDeserializer(DATE_FORMATTER))
//                .deserializerByType(LocalTime.class, 	 new LocalTimeDeserializer(TIME_FORMATTER))
//                .deserializerByType(LocalTime.class, 	 new LocalTimeDeserializer(LOCAL_TIME_FORMATTER));
        };
    }
}
