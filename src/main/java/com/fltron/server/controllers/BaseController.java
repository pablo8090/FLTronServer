package com.fltron.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.entities.User;
import com.fltron.server.repository.UserRepository;
import com.fltron.server.utils.GenericUtils;
import com.fltron.server.utils.RestConstants;

public class BaseController {
	
	@Autowired
	UserRepository userRepository;
	
	
	protected void fillGenericErrorResponse (ResponseDTO response) {
		GenericUtils.fillResponse(response, RestConstants.REST_GENERIC_ERROR, 
				RestConstants.REST_GENERIC_ERROR_DESC);
	}
	
	protected void fillAuthErrorResponse (ResponseDTO response) {
		GenericUtils.fillResponse(response, RestConstants.REST_GENERIC_AUTH_ERROR, 
				RestConstants.REST_GENERIC_AUTH_ERROR_DESC);
	}
	
	protected User getSessionUser(String email) {
		List<User> users = userRepository.findByEmail(email);
    	User user = null;
    	if (users.size() == 1) {
    		user = users.get(0);
    	}
    	return user;
	}
}
