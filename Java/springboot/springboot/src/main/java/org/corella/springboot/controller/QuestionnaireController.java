package org.corella.springboot.controller;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.services.QuestionPoolService;
import org.corella.springboot.services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class QuestionnaireController {

    @Autowired
    QuestionnaireService questionnaireService;
    @Autowired
    QuestionPoolService questionPoolService;

    @GetMapping("/questionnaires") // No utilizado
    public String questionnaireList(Model model) {
        List<Questionnaire> questionnaires = questionnaireService.findAll();
        model.addAttribute("questionnaires", questionnaires);
        return "questionnaires";
    }

    @GetMapping("/questionnaires/{id}")
    public String questionnaireListFromPool(Model model, @PathVariable String id) {
        ObjectId poolId;
        try {
            poolId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido " + id);
            return "error";
        }
        Optional<QuestionPool> questionPool = questionPoolService.findById(poolId);
        if (questionPool.isEmpty()) {
            model.addAttribute("message", "No se encontró el banco de preguntas");
            return "error";
        }
        List<Questionnaire> questionnaires = questionPool.get().getQuestionnaires();
        model.addAttribute("questionnaires", questionnaires);
        return "questionnaires";
    }

    @PostMapping("/questionnaire/generate")
    public String generateQuestionnaire(@RequestParam("poolIdStr") String poolIdStr,
                                        @RequestParam("questionCount") int numQuestions,
                                        Model model) {
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
        Questionnaire insertedQuestionnaire = questionnaireService.saveQuestionnaire(questionnaire, poolId);
        if (!insertedQuestionnaire.toString().isEmpty())
            return "redirect:/questionnaire/" + insertedQuestionnaire.getId().toString();
        return "redirect:/questionpools";
    }

    @PostMapping("/questionnaire/save")
    public String saveQuestionnaire(@ModelAttribute Questionnaire questionnaire, Model model) {
        ObjectId poolId = questionnaire.getQuestionPoolId();
        Questionnaire insertedQuestionnaire = questionnaireService.saveQuestionnaire(questionnaire, poolId);
        if (!insertedQuestionnaire.toString().isEmpty())
            return "redirect:/questionnaire/" + insertedQuestionnaire.getId().toString();
        return "redirect:/questionnaire/" + questionnaire.getId().toString();
    }

    @PostMapping("/questionnaire/{id}")
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
        model.addAttribute("questionTypes", Arrays.stream(QuestionType.values())
                .map(q -> Map.of("code", q.getCode(), "description", q.getDescription()))
                .toList());
        return "formEditQuestionnaire";
    }

    @GetMapping("/questionnaire/{id}")
    public String questionnaireDetail(Model model, @PathVariable String id) {
        ObjectId poolId;
        try {
            poolId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido " + id);
            return "error";
        }
        Optional<Questionnaire> questionnaire = questionnaireService.findById(poolId);
        questionnaire.ifPresent(value -> model.addAttribute("questionnaire", value));
        model.addAttribute("questionTypes", Arrays.stream(QuestionType.values())
                .map(q -> Map.of("code", q.getCode(), "description", q.getDescription()))
                .toList());
        return "formEditQuestionnaire";
    }

    @GetMapping("/questionnaire/export/{id}/{sol}")
    public String exportQuestionnaire(Model model, @PathVariable String id, HttpServletResponse response, @PathVariable boolean sol) {
        ObjectId poolId;
        System.out.println("Exportando");
        try {
            poolId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido " + id);
            return "error";
        }

        Optional<Questionnaire> questionnaireO = questionnaireService.findById(poolId);
        if (questionnaireO.isEmpty()) {
            model.addAttribute("message", "No se encontró el cuestionario");
            return "error";
        }
        Questionnaire questionnaire = questionnaireO.get();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + questionnaire.getTitle() + ".pdf\"");
        try {
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            PdfFont font = PdfFontFactory.createFont("src/main/resources/templates/fonts/font.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            document.add(new Paragraph(questionnaire.getTitle())
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));
            if (questionnaire.getDescription() != null) {
                document.add(new Paragraph(questionnaire.getDescription()).setItalic());
            }

            document.add(new Paragraph("\nPreguntas:").setBold().setFontSize(14));
            List<Question> questions = questionnaire.getQuestions();
            int qNum = 1;
            for (Question q : questions) {
                document.add(new Paragraph(qNum + ". " + q.getText()).setBold());

                if (q.getType() == QuestionType.MULTIPLEOPTION || q.getType() == QuestionType.BOOLEAN) {
                    int index = 0;
                    for (String answer : q.getAnswers()) {
                        char optionLabel = (char) ('A' + index++);
                        document.add(new Paragraph("   \t" + optionLabel + ") " + answer));
                    }
                } else {
                    UnitValue height = UnitValue.createPointValue(0f);
                    if (q.getType() == QuestionType.SHORTANSWER) {
                        height = UnitValue.createPointValue(20f);
                    } else if (q.getType() == QuestionType.LONGANSWER) {
                        height = UnitValue.createPointValue(200f);
                    } else {
                        document.add(new Paragraph("   \tError"));
                    }
                    float pageWidth = pdf.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();

                    Table box = new Table(1);
                    box.setWidth(UnitValue.createPointValue(pageWidth));

                    Cell squareCell = new Cell()
                            .setMinHeight(height)
                            .setBorder(new SolidBorder(1.5f));

                    box.addCell(squareCell);
                    document.add(box);
                }

                document.add(new Paragraph("\n"));
                qNum++;
            }
            if (sol) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                document.add(new Paragraph("Respuestas Correctas").setBold().setFontSize(14));

                int aNum = 1;


                for (Question q : questions) {
                    if (q.getCorrectAnswer() != null) {
                        document.add(new Paragraph(aNum + ". " + q.getCorrectAnswer()));
                    } else {
                        document.add(new Paragraph(aNum + ". [Abierta]"));
                    }
                    aNum++;
                }
            }
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @GetMapping("/questionnaire/delete/{id}")
    public String deleteQuestionnaire(Model model, @PathVariable String id) {
        ObjectId qId;
        try {
            qId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "ID inválido");
            return "error";
        }
        String result = questionnaireService.deleteQuestionnaire(qId);
        System.out.println(result);
        return "redirect:/questionnaires";
    }

}
