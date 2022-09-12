package com.tenerity.nordic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tenerity.nordic.service", "com.tenerity.nordic.controller", "com.tenerity.nordic.client", "com.tenerity.nordic.security"})
@EntityScan(basePackages = {"com.tenerity.nordic.entity"})
@EnableJpaRepositories(basePackages = {"com.tenerity.nordic.repository"})
public class DocumentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentApplication.class, args);
	}

	@Bean
	public ITemplateResolver thymeleafTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("mail-templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine thymeleafTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(thymeleafTemplateResolver());
		return templateEngine;
	}
}
