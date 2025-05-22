package org.corella.springboot.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.corella.springboot.model.enums.QuestionType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    private String id;

    private String text;

    private QuestionType type;
    private List<String> answers;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String correctAnswer;

    @JsonGetter("correctAnswer")
    public String getCorrectAnswer() {
        return (type == QuestionType.MULTIPLEOPTION || type == QuestionType.BOOLEAN) ? correctAnswer : null; // Si tiene opciones a elegir devuelve la correcta
    }
}
