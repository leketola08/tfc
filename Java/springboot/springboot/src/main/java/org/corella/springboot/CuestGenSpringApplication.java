package org.corella.springboot;

import org.corella.springboot.model.Question;
import org.corella.springboot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CuestGenSpringApplication {

	@Autowired
	QuestionRepository questionRepository;

	List<Question> questionList = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(CuestGenSpringApplication.class, args);
	}

}
