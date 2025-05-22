package org.corella.springboot.services;

import org.corella.springboot.model.QuestionPool;
import org.springframework.core.io.Resource;

public interface OllamaService {
    QuestionPool getQuestionPoolFromText(String questionPool);

    String getQuestionPoolFromText(Resource resource, int questionNum);

    QuestionPool getQuestionPoolFromResource(Resource resource, int questionNum);
}
