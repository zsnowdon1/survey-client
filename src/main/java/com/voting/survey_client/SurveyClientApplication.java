package com.voting.survey_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SurveyClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveyClientApplication.class, args);
	}

}
