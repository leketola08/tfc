package org.corella.springboot.services;

import org.springframework.core.io.Resource;

public interface OllamaService {
    String getQuestionPoolFromText(String questionPool);
    String getQuestionPoolFromText(Resource resource, int questionNum);
    String getQuestionPoolFromResource(Resource resource, int questionNum);
    String getOllamaResponseQuestionQuestion(String question);
}
