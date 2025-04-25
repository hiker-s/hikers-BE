package com.hikers.hikemate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // SNAKE_CASE 적용
                .registerModule(new JavaTimeModule())  // LocalDateTime 처리
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)  // ISO 형식으로 출력
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 년-월-일 포맷 설정
    }
}
