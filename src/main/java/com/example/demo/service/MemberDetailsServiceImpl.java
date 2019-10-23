package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.LoginMember;
import com.example.demo.domain.Member;
import com.example.demo.mapper.MemberMapper;

@Service
public class MemberDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private MemberMapper userMapper;
	
	@Override
	public UserDetails loadUserByUsername(String mailAddress) throws UsernameNotFoundException{
		Member member = userMapper.findByMailAddress(mailAddress);
		boolean accountNonLocked = true;
		if(member == null) {
			throw new UsernameNotFoundException("そのEmailは登録されていません。");
		}
		Collection<GrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
		if(member.isUsingOTP()) {
			authorityList.add(new SimpleGrantedAuthority("ROLE_OTPUSER"));
		}else {
			authorityList.add(new SimpleGrantedAuthority("ROLE_NONOTPUSER"));
		}
		if(member.isAdmin()) {
			authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		if(member.isLocked == true) {
			accountNonLocked = false;
		}
		return new LoginMember(member,authorityList,accountNonLocked);
	}
	
}
