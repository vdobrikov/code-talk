package com.codetalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class CodeTalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeTalkApplication.class, args);
	}

}
