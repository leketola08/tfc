package org.corella.springboot.services;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.enums.QuestionType;

import java.util.List;

public interface QuestionService {
    Question createQuestion(String text, QuestionType type, List<String> options, String correctAnswer, ObjectId questionnaireId);

    Question saveQuestion(Question question);
}
