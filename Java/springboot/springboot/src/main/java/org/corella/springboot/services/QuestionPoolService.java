package org.corella.springboot.services;

import org.bson.types.ObjectId;
import org.corella.springboot.model.QuestionPool;


import java.util.List;
import java.util.Optional;

public interface QuestionPoolService {

    List<QuestionPool> findAll();

    Optional<QuestionPool> findById(ObjectId id);

    QuestionPool saveQuestionPool(QuestionPool questionPool);
}
