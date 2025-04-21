package org.corella.springboot.model.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class QuestionTypeReadConverter implements Converter<String, QuestionType> {
    @Override
    public QuestionType convert(String source) {
        return QuestionType.fromCode(source);
    }
}

