package com.example.demo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private MemberService memberService;

	private String forwardUrl;

	public void ForwardAuthenticationFailureHandler(String forwardUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(forwardUrl), "'" + forwardUrl + "' is not a valid forward URL");
		this.forwardUrl = forwardUrl;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Member member = new Member();
        request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
        request.getRequestDispatcher(forwardUrl).forward(request, response);
        System.out.println(request.getParameter("mailAddress"));
        System.out.println(request.getParameter("mailAddress") instanceof String);
        if(!request.getParameter("mailAddress") .isEmpty()){
        	member = memberService.findByMailAddress(request.getParameter("mailAddress"));
        	if(member != null && member.getLocked() <= 5) {
        		memberService.updateLocker(member.getMailAddress(),member.getLocked()+1);
        	} else if(member.getLocked() > 5){
        		memberService.updateLocker(member.getMailAddress(),member.getLocked()+1);
        	}
        }
	}

}
