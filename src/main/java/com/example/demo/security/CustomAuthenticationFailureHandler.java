package com.example.demo.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	MemberService userService;

	/**
	 * springsecurity:ログイン認証失敗時の処理（アカウントロック）
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Member member = new Member();
		RequestDispatcher dispatch = null;
		if(!request.getParameter("mailAddress") .isEmpty()){
			member = userService.findByMailAddress(request.getParameter("mailAddress"));
			if(member != null && member.getLocked() <= 5) {
				userService.updateLocker(member);
				dispatch = request.getRequestDispatcher("/user?error=true");
			} else if(member.getLocked() > 5){
				userService.updateLocker(member);
				dispatch = request.getRequestDispatcher("/user?accountLock=true?mailAddress=" + request.getParameter("mailAddress"));
			}
		}else {
			dispatch = request.getRequestDispatcher("/user?error=true");
		}
		dispatch.forward(request, response);
	}
}
