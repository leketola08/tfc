package org.corella.springboot.controller;

import org.bson.types.ObjectId;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.services.QuestionPoolService;
import org.corella.springboot.services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class QuestionnaireController {

    @Autowired
    QuestionnaireService questionnaireService;
    @Autowired
    QuestionPoolService questionPoolService;

    @PostMapping("/questionnaire/generate")
    public String generateQuestionnaire(@RequestParam("poolId") String poolIdStr,
                                        @RequestParam("questionCount") int numQuestions,
                                        Model model){
        ObjectId poolId;
        try {
            poolId = new ObjectId(poolIdStr);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido");
            return "error";
        }
        Optional<QuestionPool> questionPool = questionPoolService.findById(poolId);
        if (questionPool.isEmpty()) {
            model.addAttribute("message", "No se encontró el banco de preguntas");
            return "error";
        }
        Questionnaire questionnaire = questionnaireService.generateQuestionnaire(questionPool.get(), numQuestions);
        System.out.println(questionnaire.toString());
        Questionnaire insertedQuestionnaire = questionnaireService.saveQuestionnaire(questionnaire, poolId);
        if (!insertedQuestionnaire.toString().isEmpty())
            return "redirect:/questionnaire/" + insertedQuestionnaire.getId().toString();
        return "redirect:/questionpool/list";
    }

    @PostMapping("/questionnaire/save")
    public String saveQuestionnaire(@ModelAttribute Questionnaire questionnaire, @ModelAttribute String poolIdStr, Model model) {
        ObjectId poolId;
        try {
            poolId = new ObjectId(poolIdStr);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido");
            return "error";
        }
        Questionnaire insertedQuestionnaire = questionnaireService.saveQuestionnaire(questionnaire, poolId);
        if (!insertedQuestionnaire.toString().isEmpty())
            return "redirect:/questionnaire/" + insertedQuestionnaire.getId().toString();
        return "redirect:/questionpool/list";
    }

    @GetMapping("/questionnaire/{id}")
    public String questionnaireDetails(Model model, @PathVariable String id) {
        ObjectId poolId;
        try {
            poolId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido " + id);
            return "error";
        }
        Optional<Questionnaire> questionnaire = questionnaireService.findById(poolId);
        questionnaire.ifPresent(value -> model.addAttribute("questionnaire", value));
        return "questionnaireShow";
    }



}
