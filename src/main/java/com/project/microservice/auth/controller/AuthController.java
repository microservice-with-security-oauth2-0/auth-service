package com.project.microservice.auth.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.microservice.auth.modal.User;
import com.project.microservice.common.config.JWTConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	JWTConfig jwtConfig;

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	UserDetailsService userDetailsService;

	@PostMapping
	public User authenticateUser(@RequestBody User user) throws AuthenticationException {

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUserName(),
				user.getPassword());
		Authentication auth = this.authManager.authenticate(token);

		Long now = System.currentTimeMillis();
		String jwtToken = Jwts.builder().setSubject(auth.getName())
				// Convert to list of strings.
				// This is important because it affects the way we get them back in the Gateway.
				.claim("authorities",
						auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(now)).setExpiration(new Date(now + jwtConfig.getExpiration() * 1000)) // in
																											// milliseconds
				.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes()).compact();

		user.setToken(jwtToken);
		user.setTokenType(jwtConfig.getPrefix());

		return user;
	}

	@ExceptionHandler(value = { AuthenticationException.class })
	public HashMap<String, String> handleAuthenticationException(AuthenticationException ex) {
		HashMap<String, String> errorDetails = new HashMap<>();

		errorDetails.put("errorCode", HttpStatus.UNAUTHORIZED.name());
		errorDetails.put("message", ex.getMessage());
		return errorDetails;
	}

}
