package com.example.demo.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	@Autowired
	private MemberService memberService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Member member = new Member();
		RequestDispatcher dispatch = null;
		member = memberService.findByMailAddress(request.getParameter("mailAddress"));
		if(member.getLocked() > 0) {
			member.setLocked(-1);
			member.setUnlockedKey(null);
			member.setLockedDate(null);;
			memberService.updateLocker(member);
		}
		dispatch = request.getRequestDispatcher("/item/top");
		dispatch.forward(request, response);
	}

}
