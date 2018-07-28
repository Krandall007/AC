package com.rockyrunstream.ac.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    ObjectMapper objectMapper() {
        return jackson2ObjectMapperBuilder().build();
    }


    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());

        return Jackson2ObjectMapperBuilder
                .json()
                .modulesToInstall(module)
                .autoDetectFields(true)
                .serializationInclusion(JsonInclude.Include.NON_DEFAULT)
                .defaultViewInclusion(false)
                .failOnUnknownProperties(true)
                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //Remove default MappingJackson2HttpMessageConverter
        for (Iterator<HttpMessageConverter<?>> i = converters.iterator(); i.hasNext(); ) {
            HttpMessageConverter<?> c = i.next();
            if (c instanceof MappingJackson2HttpMessageConverter) {
                i.remove();
            }
        }

        converters.add(stringConverter());

        ObjectMapper objectMapper = jackson2ObjectMapperBuilder().build();

        converters.add(new ResourceHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }

    @Bean
    public StringHttpMessageConverter stringConverter() {
        final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_HTML,
                MediaType.APPLICATION_XML,
                MediaType.APPLICATION_JSON));
        return stringConverter;
    }

}
