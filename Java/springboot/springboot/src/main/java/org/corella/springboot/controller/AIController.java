package org.corella.springboot.controller;

import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.services.OllamaService;
import org.corella.springboot.services.QuestionPoolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AIController {

    private final ChatClient chatClient;

    @Autowired
    OllamaService ollamaService;

    @Autowired
    QuestionPoolService questionPoolService;

    public AIController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/ai/questionpooltext")
    public String questionnaireText(Model model) {
        return "formQuestionPoolAI";
    }

    @RequestMapping("/ai/questionpooltext/load")
    public String questionPoolTextLoad(@RequestParam String prompt) {
        QuestionPool questionPool = ollamaService.getQuestionPoolFromText(prompt);
        QuestionPool insertedQuestionPool = questionPoolService.saveQuestionPool(questionPool);
        if (!insertedQuestionPool.toString().isEmpty())
            return "redirect:/questionpool/" + insertedQuestionPool.getId().toString();
        return "redirect:/questionpool/list";
    }

    @GetMapping("/ai/pdf")
    public String pdfToText() {
        return "pdfToText";
    }

    @PostMapping("/ai/pdf")
    public String uploadPDF(@RequestParam("file") MultipartFile file, @RequestParam("questionCount") Integer numQuestion, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Archivo no subido");
            return "error";
        }

        if (!"application/pdf".equals(file.getContentType())) {
            model.addAttribute("message", "Esto no es un PDF");
            return "error";
        }

        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes());

            QuestionPool questionPool = ollamaService.getQuestionPoolFromResource(resource, numQuestion);
            QuestionPool insertedQuestionPool = questionPoolService.saveQuestionPool(questionPool);
            if (!insertedQuestionPool.toString().isEmpty())
                return "redirect:/questionpool/" + insertedQuestionPool.getId().toString();
            model.addAttribute("message", "Ocurrió un error al procesar el archivo");
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Ocurrió un error al procesar el archivo");
            return "error";
        }

    }


}
