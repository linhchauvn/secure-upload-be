package com.tenerity.nordic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan("com.tenerity.nordic.util")
@ComponentScan(basePackages = {"com.tenerity.nordic.service", "com.tenerity.nordic.controller", "com.tenerity.nordic.client", "com.tenerity.nordic.security"})
@EntityScan(basePackages = {"com.tenerity.nordic.entity"})
@EnableJpaRepositories(basePackages = {"com.tenerity.nordic.repository"})
public class CaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaseApplication.class, args);
	}

}
