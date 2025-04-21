package org.corella.springboot.repository;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Questionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends MongoRepository<Questionnaire, Integer> {
    @Query("{title:'?0'}")
    Questionnaire findItemByTitle(String title);

    Optional<Questionnaire> findById(ObjectId id);
}
