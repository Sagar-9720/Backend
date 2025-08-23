package com.travelmate.tripservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Explicitly disable type information for HTTP responses
        // This ensures no @class properties appear in API responses

        return mapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // DO NOT enable default typing here
    }
}