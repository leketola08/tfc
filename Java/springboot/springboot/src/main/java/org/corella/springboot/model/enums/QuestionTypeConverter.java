package org.corella.springboot.model.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class QuestionTypeConverter implements Converter<String, QuestionType> {
    @Override
    public QuestionType convert(String source) {
        return QuestionType.fromCode(source);
    }
}
