package org.corella.springboot.servicesImplementation;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.repository.QuestionnaireRepository;
import org.corella.springboot.services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
    @Autowired
    QuestionnaireRepository questionnaireRepository;

    @Override
    public List<Questionnaire> findAll() {
        return questionnaireRepository.findAll();
    }

    @Override
    public Optional<Questionnaire> findById(ObjectId id) {
        return questionnaireRepository.findById(id);
    }

    @Override
    public Questionnaire saveQuestionnaire(Questionnaire questionnaire) {
        if(questionnaire != null) return questionnaireRepository.save(questionnaire);
        else return new Questionnaire();
    }
}
