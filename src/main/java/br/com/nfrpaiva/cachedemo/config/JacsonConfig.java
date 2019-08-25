package br.com.nfrpaiva.cachedemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacsonConfig {


    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder (){
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        return builder;
    }
    
}