package com.fltron.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		 SecurityAutoConfiguration.class}, scanBasePackages = "org.springframework.security.oauth2.jwt")
@EnableJpaRepositories(basePackages = {"com.fltron.server.repository", "com.fltron.server.entities"})
@EnableAutoConfiguration
@ComponentScan
public class FlTronServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlTronServerApplication.class, args);
	}

}
