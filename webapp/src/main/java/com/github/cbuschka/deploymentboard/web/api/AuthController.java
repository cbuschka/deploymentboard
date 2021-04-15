package com.github.cbuschka.deploymentboard.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController
{
	@Autowired
	private AuthenticationManager authenticationManagerBean;

	@PostMapping(value = "/api/auth/login")
	public ResponseEntity<?> login(@RequestParam("username") String username, @RequestParam("password") String password) throws Exception
	{
		Authentication authentication = this.authenticationManagerBean.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return ResponseEntity.ok(new LoginResponse(authentication.getName()));
	}
}
