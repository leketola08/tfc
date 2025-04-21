package org.corella.springboot.repository;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionRepository extends MongoRepository<Question, ObjectId> {

}
