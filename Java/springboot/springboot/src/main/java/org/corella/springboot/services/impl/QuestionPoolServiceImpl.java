package org.corella.springboot.services.impl;

import org.bson.types.ObjectId;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.repository.QuestionPoolRepository;
import org.corella.springboot.services.QuestionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionPoolServiceImpl implements QuestionPoolService {
    @Autowired
    QuestionPoolRepository questionPoolRepository;

    @Override
    public List<QuestionPool> findAll() {
        return questionPoolRepository.findAll();
    }

    @Override
    public Optional<QuestionPool> findById(ObjectId id) {
        return questionPoolRepository.findById(id);
    }

    @Override
    public QuestionPool saveQuestionPool(QuestionPool questionPool) {
        if (questionPool != null) {
            questionPool.getQuestions().removeIf(q -> q.getText() == null || q.getText().trim().isEmpty());
            questionPool.getQuestions().forEach(q -> {
                if (q.getAnswers() != null)
                    q.getAnswers().removeIf(a -> a == null || a.trim().isEmpty());
                if (q.getType() == QuestionType.LONGANSWER || q.getType() == QuestionType.SHORTANSWER) {
                    q.setAnswers(null);
                    q.setCorrectAnswer(null);
                }
            });
            return questionPoolRepository.save(questionPool);
        } else return new QuestionPool();
    }

    @Override
    public String deleteQuestionPool(ObjectId id) {
        questionPoolRepository.deleteById(id);
        return "redirect:/questionpool/list";
    }
}
