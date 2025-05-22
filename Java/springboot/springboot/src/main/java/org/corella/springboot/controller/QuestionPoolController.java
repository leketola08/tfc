package org.corella.springboot.controller;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.services.QuestionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class QuestionPoolController {
    @Autowired
    private QuestionPoolService questionPoolService;

    @GetMapping("/questionpools")
    public String questionPoolList(Model model) {
        List<QuestionPool> questionPoolList = questionPoolService.findAll();
        model.addAttribute(questionPoolList);
        return "questionpools";
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
        model.addAttribute("questionPool", questionPool);
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

    @RequestMapping("questionpool/edit/{id}")
    public String editQuestionnaire(Model model, @PathVariable String id) {
        ObjectId poolId;
        try {
            poolId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido " + id);
            return "error";
        }
        Optional<QuestionPool> questionPool = questionPoolService.findById(poolId);
        questionPool.ifPresent(value -> model.addAttribute("questionPool", value));
        model.addAttribute("questionTypes", Arrays.stream(QuestionType.values())
                .map(q -> Map.of("code", q.getCode(), "description", q.getDescription()))
                .toList());
        model.addAttribute("newQuestionPool", false);
        return "formNewQuestionPool";
    }

    @RequestMapping("questionpool/delete/{id}")
    public String deleteQuestionnaire(Model model, @PathVariable String id) {
        ObjectId poolId;
        try {
            poolId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido " + id);
            return "error";
        }
        questionPoolService.deleteQuestionPool(poolId);
        return "redirect:/questionpools";
    }
}
