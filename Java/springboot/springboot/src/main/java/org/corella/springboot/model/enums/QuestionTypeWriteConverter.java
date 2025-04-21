package org.corella.springboot.model.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class QuestionTypeWriteConverter implements Converter<QuestionType, String> {
    @Override
    public String convert(QuestionType source) {
        return source.getCode();
    }
}
