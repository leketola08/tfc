package org.corella.springboot.repository;

import org.corella.springboot.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    @Query("{text:'?0'}")
    Question findItemByText(String text);

    @Query(value = "{type: '?0'}", fields = "{'text' :  1, 'sectionType':  1}")
    List<Question> findAll(String type);

    public long count();
}
