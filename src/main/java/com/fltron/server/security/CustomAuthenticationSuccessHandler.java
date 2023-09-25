package com.fltron.server.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // Set a 200 OK status code for successful login
        response.setStatus(HttpServletResponse.SC_OK);
        // You can also send a custom response message if needed
         response.getWriter().write("Authentication successful");
    }

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		/// Set a 200 OK status code for successful login
        response.setStatus(HttpServletResponse.SC_OK);
        // You can also send a custom response message if needed
         response.getWriter().write("Authentication successful111");
		
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// Set a 200 OK status code for successful login
        response.setStatus(HttpServletResponse.SC_OK);
        // You can also send a custom response message if needed
         response.getWriter().write("Authentication successful2222");
		
	}
}