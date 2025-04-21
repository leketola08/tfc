package org.corella.springboot.controller;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.services.QuestionService;
import org.corella.springboot.services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
public class QuestionnaireController {
    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionService questionService;

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

    @RequestMapping("/questionnaires/add")
    public String createQuestionnaire(Model model) {
        Questionnaire questionnaire = new Questionnaire();
        // List<String> questionsList = new ArrayList<>();
        questionnaire.setQuestions(List.of(new Question()));
        model.addAttribute(questionnaire);
        // model.addAttribute("questionsList", questionsList);
        model.addAttribute("questionTypes", Arrays.stream(QuestionType.values())
                .map(q -> Map.of("code", q.getCode(), "description", q.getDescription()))
                .toList());
        model.addAttribute("newQuestionnaire", true);
        return "formNewQuestionnaire";
    }

    @PostMapping("/questionnaire/save")
    public String saveQuestionnaire(@ModelAttribute Questionnaire questionnaire) {
        Questionnaire insertedQuestionnaire = questionnaireService.saveQuestionnaire(questionnaire);
        if (!insertedQuestionnaire.toString().isEmpty())
            return "redirect:/questionnaires/" + insertedQuestionnaire.getId().toString();
        return "redirect:/questionnaire/list";
    }
}
