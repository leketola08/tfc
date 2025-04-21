package org.corella.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.services.QuestionnaireService;
import org.corella.springboot.servicesImplementation.OllamaServiceImpl;
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
    OllamaServiceImpl ollamaService;

    @Autowired
    QuestionnaireService questionnaireService;

    public AIController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/ai/questionnaireText")
    public String questionnaireText(Model model) {
        return "formQuestionnaireAI";
    }

    @RequestMapping("/ai/questionnaireText/load")
    public String questionnaireTextLoad(@RequestParam String prompt) {
        System.out.println(ollamaService.getQuestionnaireFromText(prompt));
        return "redirect:/";
    }

    @GetMapping("/pdf")
    public String pdfToText() {
        return "pdfToText";
    }

    @PostMapping("/pdf")
    public String uploadPDF(@RequestParam("file")MultipartFile file, Model model) {
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

            String questionnaireJSON = ollamaService.getQuestionnaireQuestionsFromText(resource, 10);
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(questionnaireJSON);
            Questionnaire questionnaire = objectMapper.readValue(questionnaireJSON, Questionnaire.class);
            Questionnaire insertedQuestionnaire = questionnaireService.saveQuestionnaire(questionnaire);
            if (!insertedQuestionnaire.toString().isEmpty())
                return "redirect:/questionnaires/" + insertedQuestionnaire.getId().toString();
            model.addAttribute("message", "Ocurrió un error al procesar el archivo");
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Ocurrió un error al procesar el archivo");
            return "error";
        }

    }


}
