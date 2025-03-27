package org.corella.springboot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Cuestionario")
public class Questionnaire {
    @Id
    private String id;

    private String title;
    private String description;
    private List<Question> questions;
    private String creationDate;
    private List<String> sections;

    public Questionnaire(String id, String title, String description, List<Question> questions, String creationDate, List<String> sections) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.questions = questions;
        this.creationDate = creationDate;
        this.sections = sections;
    }
}
