package com.fltron.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class CustomWebSecurityConfigurerAdapter {
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
       
	    return httpSecurity
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/**").permitAll()
	                .anyRequest().authenticated()
	            ).build();
//	    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
//        .httpBasic(Customizer.withDefaults())
//        .build();
    }
}
