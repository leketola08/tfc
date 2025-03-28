package org.corella.springboot.servicesImplementation;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository  questionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Question createQuestion(String text, QuestionType type, List<String> options, String correctAnswer, ObjectId questionnaireId) {
        Question question = questionRepository.insert(new Question(text, type, options, correctAnswer));
        mongoTemplate.update(Questionnaire.class)
                .matching(Criteria.where("_id").is(questionnaireId))
                .apply(new Update().push("questions").value(question))
                .first();
        return question;
    }

}
