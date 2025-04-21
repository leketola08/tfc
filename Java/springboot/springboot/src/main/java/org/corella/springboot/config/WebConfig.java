package org.corella.springboot.config;

import org.corella.springboot.model.enums.QuestionTypeReadConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private QuestionTypeReadConverter questionTypeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(questionTypeConverter);
    }
}
