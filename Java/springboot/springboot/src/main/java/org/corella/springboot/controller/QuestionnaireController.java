package org.corella.springboot.controller;

import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.repository.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class QuestionnaireController {
    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    // Create a new questionnaire
    @PostMapping
    public Questionnaire createQuestionnaire(@RequestBody Questionnaire questionnaire) {
        return questionnaireRepository.save(questionnaire);
    }

    // Get all questionnaires
    @GetMapping
    public List<Questionnaire> getAllQuestionnaires() {
        return questionnaireRepository.findAll();
    }

    // Get a specific questionnaire by ID
    @GetMapping("/{id}")
    public Questionnaire getQuestionnaireById(@PathVariable String id) {
        return questionnaireRepository.findById(id).orElse(null);
    }
}
