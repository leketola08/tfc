package org.corella.springboot.config;

import org.corella.springboot.model.enums.QuestionTypeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

/**
 * Esta clase es llamada de forma automática por el configurador automático de
 * MongoAutoConfiguration que ha sido llamado por el método SpringApplication.run().
 * Esta clase se encarga de configurar los convertidores de MongoDB para que
 * se puedan usar en la aplicación.
 */
@Configuration
public class MongoConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                Arrays.asList(new QuestionTypeConverter())
        );
    }
}