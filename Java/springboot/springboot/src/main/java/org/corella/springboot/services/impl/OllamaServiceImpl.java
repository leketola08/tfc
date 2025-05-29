package org.corella.springboot.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.services.EmbeddingService;
import org.corella.springboot.services.OllamaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaServiceImpl implements OllamaService {
    private static final String OLLAMA_URL = "http://localhost:11434";
    private static final String MODEL = "gemma3";

    private final OllamaApi ollamaApi = new OllamaApi(OLLAMA_URL);

    @Autowired
    EmbeddingService embeddingService;

    private OllamaOptions options = OllamaOptions.builder()
            .model(MODEL)
            .format("json")
            .temperature(0.0)
            .build();

    private OllamaChatModel chatModel = OllamaChatModel.builder().
            ollamaApi(ollamaApi)
            .defaultOptions(options)
            .build();

    private OllamaOptions optionsForText = OllamaOptions.builder()
            .model(MODEL)
            .temperature(0.8)
            .build();

    private OllamaChatModel chatModelForText = OllamaChatModel.builder().
            ollamaApi(ollamaApi)
            .defaultOptions(optionsForText)
            .build();

    @Value("classpath:/prompts/PromptQuestionPoolFromText.txt")
    private Resource promptQuestionPoolFromText;

    @Value("classpath:/prompts/PromptQuestionPoolFromFile.txt")
    private Resource promptQuestionPoolFromFile;

    @Value("classpath:/prompts/PromptQuestionPoolQuestionsFromFile.txt")
    private Resource promptQuestionPoolQuestionsFromFile;

    public QuestionPool getQuestionPoolFromText(String questionPool) {
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("datetoday", LocalDate.now().toString());
        promptParameters.put("questionPool", questionPool);
        promptParameters.put("datetimetoday", LocalDateTime.now().toString());

        promptParameters.put("structure", jsonStructure().toString(2));

        String templateString;
        try (InputStream inputStream = promptQuestionPoolFromText.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            templateString = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading prompt template file.", e);
        }
        PromptTemplate promptTemplate = new PromptTemplate(templateString);
        Prompt prompt = promptTemplate.create(promptParameters);
        ChatResponse response = chatModel.call(prompt);
        String answer = response.getResult().getOutput().getText();
        // System.out.println("RAW\n" + answer);
        if (!answer.trim().startsWith("{") || !answer.trim().endsWith("}")) {
            throw new RuntimeException("Ollama output is not valid JSON:\n" + answer);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(answer, QuestionPool.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Ollama response into QuestionPool", e);
        }
    }


    public String getQuestionPoolFromText(Resource resource, int questionNum) {
        embeddingService.embedAndStore(resource);
        SimpleVectorStore vectorStore = embeddingService.getVectorStore();
        List<Document> similarDocuments = vectorStore.similaritySearch("Contenido del tema sin ejemplos");
        StringBuilder context = new StringBuilder();
        for (Document document : similarDocuments) {
            context.append(document.getFormattedContent()).append("\n");
        }
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("datetoday", LocalDate.now().toString());
        promptParameters.put("numquestions", questionNum);
        promptParameters.put("datetimetoday", LocalDateTime.now().toString());
        promptParameters.put("structure", jsonStructure().toString(2));
        promptParameters.put("context", context.toString());

        String templateString;
        try (InputStream inputStream = promptQuestionPoolFromFile.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            templateString = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading prompt template file.", e);
        }

        PromptTemplate promptTemplate = new PromptTemplate(templateString);
        Prompt prompt = promptTemplate.create(promptParameters);
        ChatResponse response = chatModel.call(prompt);
        String answer = response.getResult().getOutput().getText();
        return answer;
    }

    public QuestionPool getQuestionPoolFromResource(Resource resource, int questionNum) {
        embeddingService.embedAndStore(resource);
        SimpleVectorStore vectorStore = embeddingService.getVectorStore();
        List<Document> similarDocuments = vectorStore.similaritySearch("Contenido del tema sin ejemplos");
        StringBuilder context = new StringBuilder();
        for (Document document : similarDocuments) {
            context.append(document.getFormattedContent()).append("\n");
        }
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("datetoday", LocalDate.now().toString());
        promptParameters.put("numquestions", questionNum);
        promptParameters.put("datetimetoday", LocalDateTime.now().toString());
        promptParameters.put("structure", jsonStructure().toString(2));
        promptParameters.put("context", context.toString());

        String templateString;
        try (InputStream inputStream = promptQuestionPoolQuestionsFromFile.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            templateString = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading prompt template file.", e);
        }

        PromptTemplate promptTemplate = new PromptTemplate(templateString);
        Prompt prompt = promptTemplate.create(promptParameters);
        ChatResponse response = chatModelForText.call(prompt);
        String answer = response.getResult().getOutput().getText();
        System.out.println("Preguntas:\n" + answer);
        System.out.println("Preguntas generadas");
        return getQuestionPoolFromText(answer);
    }

    private JSONObject jsonStructure() {
        JSONObject jsonStructure = new JSONObject();
        jsonStructure.put("title", "Title text");
        jsonStructure.put("description", "Description");
        JSONArray questionsArray = new JSONArray();
        JSONObject questionObject = new JSONObject();
        questionObject.put("text", "Question Text");
        questionObject.put("type", "type");
        JSONArray answersArray = new JSONArray();
        answersArray.put("Answer1");
        answersArray.put("Answer2");
        answersArray.put("Answer3");
        questionObject.put("answers", answersArray);
        questionObject.put("correctAnswer", "CorrectAnswer");
        questionsArray.put(questionObject);
        jsonStructure.put("questions", questionsArray);
        jsonStructure.put("creationDate", LocalDate.now().toString());

        return jsonStructure;
    }
}
