package org.corella.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document("questionPool")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionPool {
    @Id
    private ObjectId id;
    private String title;
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Question> questions;
    private String creationDate;

    @DocumentReference
    private List<Questionnaire> questionnaires;
}
