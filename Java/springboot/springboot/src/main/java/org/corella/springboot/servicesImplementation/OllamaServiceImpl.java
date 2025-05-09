package org.corella.springboot.servicesImplementation;

import org.corella.springboot.services.EmbeddingService;
import org.corella.springboot.services.OllamaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ai.chat.messages.UserMessage;
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

    public String getQuestionPoolFromText(String questionPool) {
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
        return answer;
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

    public String getQuestionPoolFromResource(Resource resource, int questionNum) {
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
        return getQuestionPoolFromText(answer);
    }

    /**
     * Función de prueba para obtener una respuesta de Ollama para una única pregunta
     * @param question
     * @return respuesta en formato JSON de Ollama
     */
    public String getOllamaResponseQuestionQuestion(String question) {
        String structure = "Give the output using this json structure {\"text\": \"Question Text\"," +
                "\"type\": \"type\"," +
                "\"answers\": " +
                "[\"Answer1\", \"Answer2\", \"Answer3\"]," +
                "\"correctAnswer\": \"CorrectAnswer)\"}";
        String promptString = structure + "Given a question extract theses properies: In text get only the question, answers have their own property specified later" +
                "In types, multiple option is MO, a boolean is BOOL, Open long answer is LA, Open short answer is SA," +
                " and ordered is OR. Types MO, BOOL AND OR Have a correct answer, if not specified leave it blank, on " +
                "other types remove the property entirely. Each answer can be marked with a letter or like a number like" +
                " a), a., 1), or 1. Just extract the answer text do no include the order marker, Put all answers into the" +
                " answers array The correct answer is marked with -> a) for example. You must put the entire answer " +
                "without including the order marker. Each corresponds to array answers" +
                " And here is the question: " + question;
        Prompt prompt = new Prompt(List.of(new UserMessage(promptString)));
        ChatResponse response = chatModel.call(prompt);
        String answer = response.getResult().getOutput().getText();
        return answer;
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
