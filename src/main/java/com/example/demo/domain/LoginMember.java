package com.example.demo.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class LoginMember extends User{

	private static final long serialVersionUID = 1L;
	private final Member member;
	
	public LoginMember(Member member, Collection<GrantedAuthority> authorityList, boolean accountNonLocked) {
		super(member.getMailAddress(),member.getPassword(),true,true,true,accountNonLocked,authorityList);
		this.member = member;
	}
	
	public Member getMember() {
		return member;
	}
	
}
