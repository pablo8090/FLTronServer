package com.fltron.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fltron.server.dto.responses.ResponseDTO;
import com.fltron.server.dto.responses.RoomResponseDTO;
import com.fltron.server.entities.User;
import com.fltron.server.repository.UserRepository;
import com.fltron.server.utils.GenericConstants;
import com.fltron.server.utils.GenericUtils;
import com.fltron.server.utils.RestConstants;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
	
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/login")
	public ResponseDTO login (@RequestParam String email, 
			@RequestParam String password) {
		ResponseDTO response = new ResponseDTO();
		try {
			List<User> users = userRepository.findByEmail(email);
	    	User user = null;
	    	if (users.size() == 1) {
	    		user = users.get(0);
	    	}
	    	
	    	if (user != null && user.getPassword().equals(password)) {
	    		response.setResultCode(RestConstants.REST_GENERIC_SERVICE_OK);
	    		response.setResultDescription(RestConstants.REST_GENERIC_SERVICE_OK_DESC);
	    		return response;
	        }
	    	
	    	fillAuthErrorResponse(response);
		} catch (Exception e) {
			fillGenericErrorResponse(response);
			e.printStackTrace();
		}
		
		return response;
	}
}
