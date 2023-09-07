package com.springboot.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import com.springboot.manager.entities.User;

@Configuration
@EnableWebSecurity
public class Config  {
	
	
	@Bean
	UserDetailsServiceImpl getUserDetailsServiceImpl()
	{
		return new UserDetailsServiceImpl();
	}
	@Bean
	BCryptPasswordEncoder getBCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	DaoAuthenticationProvider getauthenticationProvider(){
	DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
	authenticationProvider.setUserDetailsService(getUserDetailsServiceImpl());
	authenticationProvider.setPasswordEncoder(getBCryptPasswordEncoder());
	return authenticationProvider;
	}
	
	@Bean
	 SecurityFilterChain chain(HttpSecurity httpSecurity ) throws Exception {
		
		httpSecurity
		.authorizeHttpRequests()
		.requestMatchers("/admin/**")
		.hasRole("ADMIN")
		.requestMatchers("/user/**")
		.hasRole("USER")
		.requestMatchers("/**")
		.permitAll()
		.and()
		.formLogin()
		.loginPage("/login")
		.loginProcessingUrl("/login")
		.defaultSuccessUrl("/user/index")
//		.failureUrl("/signin")
		.and()
		.csrf()
		.disable();
		
		
		httpSecurity.authenticationProvider(getauthenticationProvider());
		DefaultSecurityFilterChain build = httpSecurity.build();
		return build;
	}
}
