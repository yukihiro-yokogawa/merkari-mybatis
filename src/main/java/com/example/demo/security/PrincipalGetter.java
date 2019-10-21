package com.example.demo.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.domain.Member;

/**
 * 
 * springsecurityのprincipalからユーザー情報を取り出すメソッドです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Component
public class PrincipalGetter {

	public Member getMember() {
		Member member = new Member();
		if(!SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
	      member = (Member)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	      return member;	
		}
		member.setMailAddress(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
		return member;
	}
	
}
