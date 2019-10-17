package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.domain.LoginMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Controller
@CrossOrigin
public class OnetimePasswordGenerator {

	@RequestMapping("/passGenerator")
	@ResponseBody
	public String googleAuth(@AuthenticationPrincipal LoginMember loginMember,HttpSession session) {
		if (loginMember != null) {
			String serviceName = "Rakus_Items";
			String userId = loginMember.getUsername();
			String uriJson = null;

			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			GoogleAuthenticatorKey key = gAuth.createCredentials();
			String secret = key.getKey();
			String uri = "otpauth://totp/" + serviceName + ":" + userId + "?secret=" + secret + "&issuer=" + serviceName;
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			try {
				uriJson = mapper.writeValueAsString(uri);
			} catch(IOException e) {
				e.printStackTrace();
			}
			session.setAttribute("secret", secret);
			
			return uriJson;
		}
		return null;
	}

}
