package org.corella.springboot.controller;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.services.QuestionService;
import org.corella.springboot.services.QuestionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
public class QuestionPoolController {
    @Autowired
    private QuestionPoolService questionPoolService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/questionpools")
    public String questionPoolList(Model model) {
        List<QuestionPool> questionPoolList = questionPoolService.findAll();
        model.addAttribute(questionPoolList);
        return "questionPools";
    }

    @GetMapping("/questionpool/{id}")
    public String questionnaireDetails(Model model, @PathVariable ObjectId id) {
        Optional<QuestionPool> questionnaire = questionPoolService.findById(id);
        questionnaire.ifPresent(value -> model.addAttribute("questionPool", value));
        return "questionpool";
    }

    @RequestMapping("/questionpool/add")
    public String createQuestionnaire(Model model) {
        QuestionPool questionPool = new QuestionPool();
        questionPool.setQuestions(List.of(new Question()));
        model.addAttribute(questionPool);
        model.addAttribute("questionTypes", Arrays.stream(QuestionType.values())
                .map(q -> Map.of("code", q.getCode(), "description", q.getDescription()))
                .toList());
        model.addAttribute("newQuestionPool", true);
        return "formNewQuestionPool";
    }

    @PostMapping("/questionpool/save")
    public String saveQuestionnaire(@ModelAttribute QuestionPool questionPool) {
        QuestionPool insertedQuestionPool = questionPoolService.saveQuestionPool(questionPool);
        if (!insertedQuestionPool.toString().isEmpty())
            return "redirect:/questionpool/" + insertedQuestionPool.getId().toString();
        return "redirect:/questionpool/list";
    }


}
