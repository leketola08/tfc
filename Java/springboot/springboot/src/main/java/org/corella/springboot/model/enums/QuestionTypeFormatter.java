package org.corella.springboot.model.enums;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class QuestionTypeFormatter implements Formatter<QuestionType> {

    @Override
    public QuestionType parse(String text, Locale locale) throws ParseException {
        return QuestionType.fromCode(text);
    }

    @Override
    public String print(QuestionType object, Locale locale) {
        return object.getCode();
    }
}

