package org.corella.springboot.servicesImplementation;

import org.bson.types.ObjectId;
import org.corella.springboot.model.QuestionPool;
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
        if (questionPool != null) return questionPoolRepository.save(questionPool);
        else return new QuestionPool();
    }
}
