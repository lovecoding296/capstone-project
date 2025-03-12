package funix.tca.config;


import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class LocaleConfig implements WebMvcConfigurer{

	@Bean
	LocaleResolver localeResolver() {
		System.out.println("localeResolver");
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(new Locale("vn"));
		resolver.setDefaultTimeZone(TimeZone.getTimeZone("UTC"));
		return resolver;
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		System.out.println("localeChangeInterceptor");
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();		
		interceptor.setParamName("lang"); // Change language via URL param		
		return interceptor;
	}
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
		System.out.println("addInterceptors");
        registry.addInterceptor(localeChangeInterceptor());
    }
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}