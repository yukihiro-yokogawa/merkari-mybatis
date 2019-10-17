package com.example.demo.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * SpringSecurityに独自のパラメータをセットするクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails{

	private static final long serialVersionUID = 1L;

	private String verificationCode;
	
	/**
	 * 独自のリクエストパラメータから値を取得するメソッドです.
	 * 
	 * @param request
	 */
	public CustomWebAuthenticationDetails(HttpServletRequest request) {
		super(request);
		verificationCode = request.getParameter("onetimePassword");
	}

	public String getVerificationCode() {
		return verificationCode;
	}
	
}
