package com.petersavitsky.jobcoinmixer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableScheduling
public class JobCoinMixerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JobCoinMixerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(JobCoinMixerApplication.class);
	}

}
