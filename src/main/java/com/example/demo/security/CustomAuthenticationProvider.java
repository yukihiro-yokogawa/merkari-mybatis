package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.example.demo.domain.Member;
import com.example.demo.mapper.MemberMapper;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider{
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException{
		//htmlのname=mailAddressをセットしたauthenticationでDBからユーザー情報を取り出す.
		Member member = memberMapper.findByMailAddress(auth.getName());
		//情報がなければエラーを返す.
		if(member == null) {
			throw new BadCredentialsException("Invalid username or password");
		}
		
		int verificationCode = 0;
		//OTPが設定されていればOTPの認証を行う
		if(member.isUsingOTP()) {
			if(!((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode().isEmpty()) {
				verificationCode = Integer.parseInt(((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode());
			}
			//googleの提供するOTPのシークレットキーとOTPがマッチするか検証
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			boolean isCodeValid = gAuth.authorize(member.getVerificationCode(), verificationCode);
			if(!isCodeValid) {
				throw new BadCredentialsException("Invalid OTP");
			}
		}
		//resultにauthenticationのデータをセット
		Authentication result = super.authenticate(auth);
		//principal（ユーザー情報）、パスワード、権限をセットする
		return new UsernamePasswordAuthenticationToken(member, result.getCredentials(),result.getAuthorities());
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
