package com.project.microservice.auth.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.microservice.auth.modal.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// hard coding the users. All passwords must be encoded.
		final List<User> users = Arrays.asList(new User("user", encoder.encode("12345"), "USER"),
				new User("admin", encoder.encode("12345"), "ADMIN"));

		return users.stream().filter(user -> user.getUserName().equals(username)).map(user -> {
			// Remember that Spring needs roles to be in this format: "ROLE_" + userRole
			// (i.e. "ROLE_ADMIN")
			// So, we need to set it to that format, so we can verify and compare roles
			// (i.e. hasRole("ADMIN")).
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils
					.commaSeparatedStringToAuthorityList("ROLE_" + user.getRole());

			// The "User" class is provided by Spring and represents a model class for user
			// to be returned by UserDetailsService
			// And used by auth manager to verify and check user authentication.
			return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
					grantedAuthorities);

		}).findAny().orElseThrow(() -> { // If user not found. Throw this exception.
			return new UsernameNotFoundException("Username: " + username + " not found");
		});

	}

}
