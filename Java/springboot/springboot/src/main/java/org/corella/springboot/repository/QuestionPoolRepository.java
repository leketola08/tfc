package org.corella.springboot.repository;

import org.bson.types.ObjectId;
import org.corella.springboot.model.QuestionPool;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionPoolRepository extends MongoRepository<QuestionPool, ObjectId> {
    @Query("{title:'?0'}")
    QuestionPool findItemByTitle(String title);
}
