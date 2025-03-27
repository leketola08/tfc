package org.corella.springboot.repository;

import org.corella.springboot.model.Questionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface QuestionnaireRepository extends MongoRepository<Questionnaire, String> {
    @Query("{title:'?0'}")
    Questionnaire findItemByTitle(String title);

    public long count();
}
