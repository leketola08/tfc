package org.corella.springboot.repository;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Questionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireRepository extends MongoRepository<Questionnaire, ObjectId> {
}
