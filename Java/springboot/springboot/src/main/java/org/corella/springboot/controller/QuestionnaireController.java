package org.corella.springboot.controller;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.repository.QuestionnaireRepository;
import org.corella.springboot.servicesImplementation.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;


@Controller
public class QuestionnaireController {
    @Autowired
    private QuestionnaireService questionnaireService;

    @GetMapping("/questionnaires")
    public String questionnaireList(Model model) {
        List<Questionnaire> questionnaireList = questionnaireService.findAll();
        model.addAttribute(questionnaireList);
        return "questionnaires";
    }

    @GetMapping("/questionnaires/{id}")
    public String questionnaireDetails(Model model, @PathVariable ObjectId id) {
        Optional<Questionnaire> questionnaire = questionnaireService.findById(id);
        questionnaire.ifPresent(value -> model.addAttribute("questionnaire", value));
        return "questionnaire";
    }
}
