package org.corella.springboot.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.corella.springboot.model.enums.QuestionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("question")
public class Question {
    @Id
    private ObjectId id;


    private String text;

    @JsonProperty("type")
    private QuestionType type;
    private List<String> answers;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("sectionType")
    private String sectionType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String correctAnswer;

    public Question(String text, QuestionType type, List<String> answers, String correctAnswer) { // Constructor sin ID autogenerado de MongoDB
        this.text = text;
        this.type = type;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    @JsonGetter("correctAnswer")
    public String getCorrectAnswer() {
        return (type  == QuestionType.MULTIPLEOPTION || type == QuestionType.BOOLEAN || type == QuestionType.ORDER) ? correctAnswer : null; // Si tiene opciones a elegir devuelve la correcta
    }
}
