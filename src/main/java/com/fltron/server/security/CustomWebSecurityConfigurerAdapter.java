package com.fltron.server.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CustomWebSecurityConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://dev-9267515.okta.com/oauth2/default");
    }
	
	@Autowired
	private  CustomUserDetailsService customUserDetailsService;

	public CustomWebSecurityConfigurerAdapter(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		auth.userDetailsService(customUserDetailsService).passwordEncoder(encoder);
		
	}
	
	

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
			@Value("${resource-server.cors.allowed-origins:}#{T(java.util.Collections).emptyList()}") 
			List<String> allowedOrigins) throws Exception {

		return httpSecurity
				
				.csrf(csrf -> csrf.disable())
				.httpBasic(Customizer.withDefaults())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/**").permitAll()
						.anyRequest().authenticated()
						)
				.authenticationProvider(authenticationProvider) 
				//.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				//.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				
//				.cors(cors -> {
//		            if (allowedOrigins.isEmpty()) {
//		                cors.disable();
//		            } else {
//		                cors.configurationSource(corsConfig(allowedOrigins));
//		            }
//		        })
				.formLogin((form) -> form
						.loginPage("/login")
						//.successHandler(new CustomAuthenticationSuccessHandler())
						//.failureHandler(new CustomAuthenticationSuccessHandler())
						.permitAll()
					)
				//.logout((logout) -> logout.permitAll())
				.build();

	}

//	  CorsConfigurationSource corsConfig(List<String> allowedOrigins) {
//	        final var source = new UrlBasedCorsConfigurationSource();
//
//	        final var configuration = new CorsConfiguration();
//	        configuration.setAllowedOrigins(allowedOrigins);
//	        configuration.setAllowedMethods(List.of("*"));
//	        configuration.setAllowedHeaders(List.of("*"));
//	        configuration.setExposedHeaders(List.of("*"));
//
//	        source.registerCorsConfiguration("/**", configuration);
//	        return source;
//	    }
//	@Bean
//	public HttpSessionEventPublisher httpSessionEventPublisher() {
//		return new HttpSessionEventPublisher();
//	}
	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//	    return new BCryptPasswordEncoder();
//	}

//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//		UserDetails user =
//				User
//				.withDefaultPasswordEncoder()
//				.username("user")
//				.password(encoder.encode("password")) // 
//				.roles("USER")
//				.build();
//
//		return new InMemoryUserDetailsManager(user);
//	}
	
}
