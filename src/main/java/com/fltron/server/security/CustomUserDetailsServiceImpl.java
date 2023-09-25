package com.fltron.server.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fltron.server.entities.User;
import com.fltron.server.repository.UserRepository;
import com.fltron.server.utils.GenericConstants;
import com.fltron.server.utils.GenericUtils;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
	
	@Autowired
	UserRepository userRepository;
	
    
    
    @Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
         // get from request parameter
		String password = null;
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            password = values[1];
        }
        
        if (password == null) {
        	throw new UsernameNotFoundException("Password blank: " + username);
        }
		List<User> users = userRepository.findByEmail(username);
    	User user = null;
    	if (users.size() == 1) {
    		user = users.get(0);
    	}
    	if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    		
    	String encryptedPassword = GenericUtils.get_SHA_512_SecurePassword(password, GenericConstants.SALT);
		if (user.getPassword().equals(encryptedPassword)) {
			org.springframework.security.core.userdetails.User usersDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), 
					encoder.encode(user.getPassword()), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
			return usersDetails;
			
		} else {
			 throw new UsernameNotFoundException("Invalid password " + username);
		}
    	
    	
	}
}
