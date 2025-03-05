package com.example.demo;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;


@SpringBootApplication
public class CapstoneProjectApplication {

	public static void main(String[] args) {
		System.out.println("Running main...");
		SpringApplication.run(CapstoneProjectApplication.class, args);
	}

}
