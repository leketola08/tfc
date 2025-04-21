package org.corella.springboot.services;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Questionnaire;

import java.util.List;
import java.util.Optional;

public interface QuestionnaireService {

    List<Questionnaire> findAll();

    Optional<Questionnaire> findById(ObjectId id);

    Questionnaire saveQuestionnaire(Questionnaire questionnaire);
}
