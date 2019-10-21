package com.example.demo.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class LoginMember extends User{

	private static final long serialVersionUID = 1L;
	private final Member member;
	
	public LoginMember(Member member, Collection<GrantedAuthority> authorityList) {
		super(member.getMailAddress(),member.getPassword(),authorityList);
		System.out.println(member);
		this.member = member;
	}
	
	public Member getMember() {
		return member;
	}
	
}
