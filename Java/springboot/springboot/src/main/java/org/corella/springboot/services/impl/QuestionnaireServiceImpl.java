package org.corella.springboot.services.impl;

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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Questionnaire> findAll() {
        return questionnaireRepository.findAll();
    }

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
        questionnaire.setDescription("Cuestionario nº" + (questionPool.getQuestionnaires().size() + 1) + " " + questionPool.getDescription());
        questionnaire.setQuestionPoolId(questionPool.getId());
        questionnaire.setQuestions(new ArrayList<>(randomizedQuestions));
        // questionnaire.setSectioned(false);
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
            int j = r.nextInt(i + 1);

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
        if (question.getAnswers() == null)
            return;
        String[] answerArr = question.getAnswers().toArray(new String[0]);

        for (int i = question.getAnswers().size() - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);

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
        questionnaire.getQuestions().removeIf(q -> q.getText() == null || q.getText().trim().isEmpty());
        questionnaire.getQuestions().forEach(q -> {
            if (q.getAnswers() != null)
                q.getAnswers().removeIf(a -> a == null || a.trim().isEmpty());
            if (q.getType() == QuestionType.LONGANSWER || q.getType() == QuestionType.SHORTANSWER) {
                q.setAnswers(null);
                q.setCorrectAnswer(null);
            }
        });
        questionnaire.setCreationDate(LocalDate.now().toString());
        Questionnaire insertedQuestionnaire = questionnaireRepository.save(questionnaire);
        mongoTemplate.update(QuestionPool.class)
                .matching(Criteria.where("id")
                        .is(poolId))
                .apply(new Update().push("questionnaires")
                        .value(insertedQuestionnaire)).first();
        return insertedQuestionnaire;
    }

    @Transactional
    public String deleteQuestionnaire(ObjectId id) {
        Optional<Questionnaire> questionnaireO = questionnaireRepository.findById(id);
        if (questionnaireO.isEmpty())
            return "No se encontró el cuestionario";
        mongoTemplate.update(QuestionPool.class)
                .matching(Criteria.where("questionnaires._id")
                        .is(id))
                .apply(new Update().pull("questionnaires",
                        Query.query(Criteria.where("_id").is(id)))).first();

        Questionnaire questionnaire = questionnaireO.get();
        questionnaireRepository.delete(questionnaire);
        return "Cuestionario eliminado";
    }
}
