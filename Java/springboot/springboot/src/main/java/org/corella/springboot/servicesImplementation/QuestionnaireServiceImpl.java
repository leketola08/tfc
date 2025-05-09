package org.corella.springboot.servicesImplementation;

import org.bson.types.ObjectId;
import org.corella.springboot.model.Question;
import org.corella.springboot.model.QuestionPool;
import org.corella.springboot.model.Questionnaire;
import org.corella.springboot.model.enums.QuestionType;
import org.corella.springboot.repository.QuestionnaireRepository;
import org.corella.springboot.services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
    @Autowired
    QuestionnaireRepository questionnaireRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Questionnaire generateQuestionnaire(QuestionPool questionPool, int numQuestions) {
        List<Question> questions = questionPool.getQuestions();
        if (numQuestions > questions.size()) {
            throw new IllegalArgumentException("El número de preguntas pedida no puede ser superior al número de preguntas en el banco de preguntas");
        }
        // Algoritmo de Fisher Yates para "barajar" de forma aleatoria la lista, también existe Collections.shuffle
        Deque<Question> randomizedQuestions = randomize(questions);
        while (randomizedQuestions.size() > numQuestions)
            randomizedQuestions.removeLast();
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setCreationDate(LocalDate.now().toString());
        questionnaire.setTitle(questionPool.getTitle() + LocalDateTime.now());
        questionnaire.setDescription("Cuestionario nº" + (questionPool.getQuestionnaires().size() + 1) + " " +questionPool.getDescription());
        questionnaire.setQuestionPoolId(questionPool.getId());
        questionnaire.setQuestions(new ArrayList<>(randomizedQuestions));
        questionnaire.setSectioned(false);
        return questionnaire;
    }

    @Override
    public Optional<Questionnaire> findById(ObjectId id) {
        return questionnaireRepository.findById(id);
    }

    private Deque<Question> randomize(List<Question> questionList) {
        Random r = new Random();
        Question[] questionArr = questionList.toArray(new Question[0]);
        List<Thread> threads = new ArrayList<>();
        for (int i = questionList.size() - 1; i > 0; i--) {
            int j = r.nextInt(i+1);

            Question aux = questionArr[i];
            questionArr[i] = questionArr[j];
            int finalI = i;
            if (questionArr[finalI].getType() == QuestionType.MULTIPLEOPTION) {
                Thread t = new Thread(() -> randomizeAnswerOrder(questionArr[finalI]));
                threads.add(t);
                t.start();
            }
            questionArr[j] = aux;
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new ArrayDeque<>(List.of(questionArr));
    }

    private void randomizeAnswerOrder(Question question) {
        Random r = new Random();
        String[] answerArr = question.getAnswers().toArray(new String[0]);

        for (int i = question.getAnswers().size() - 1; i > 0; i--) {
            int j = r.nextInt(i+1);

            String aux = answerArr[i];
            answerArr[i] = answerArr[j];
            answerArr[j] = aux;
        }

        question.setAnswers(new ArrayList<>(List.of(answerArr)));
    }

    @Override
    public Questionnaire saveQuestionnaire(Questionnaire questionnaire, ObjectId poolId) {
        if (questionnaire == null)
            return new Questionnaire();
        Questionnaire insertedQuestionnaire = questionnaireRepository.insert(questionnaire);
        mongoTemplate.update(QuestionPool.class)
                .matching(Criteria.where("id")
                .is(poolId))
                .apply(new Update().push("questionnairesIds")
                .value(insertedQuestionnaire)).first();
        return insertedQuestionnaire;
    }
}
