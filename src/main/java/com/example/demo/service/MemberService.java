package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Member;
import com.example.demo.form.MemberForm;
import com.example.demo.mapper.MemberMapper;

@Service
@Transactional
public class MemberService {
	
	@Autowired
	private MemberMapper memberMapper;	
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	/**
	 * 仮ユーザー登録を行うメソッドです.
	 * Bcryptアルゴリズムで入力されたパスワードを暗号化します.
	 * @param form
	 */
	public void insertProvisionalMember(MemberForm form, String uuid) {
		String encodePassword = passwordEncoder.encode(form.getPassword());
		Member member = new Member();
		//formの値をコピー
		BeanUtils.copyProperties(form, member);
		member.setAuthority(Integer.parseInt(form.getAuthority()));
		//Bcryptアルゴリズムで暗号化されたパスワードをドメインにセット
		member.setPassword(encodePassword);
		//controllerで作成された仮登録のランダムトークンをドメインにセット
		member.setUuid(uuid);
		//登録した日付をドメインにセット（削除用）
		LocalDate date = LocalDate.now();
		member.setRegisterDate(Date.valueOf(date));
		memberMapper.insertProvisionalMember(member);
	}
	
	public void insertMember(String uuid) {
		memberMapper.insertMember(uuid);
	}
	
	/**
	 * ユーザー名からユーザー情報を取得します.
	 * 
	 * @param mailAddress
	 * @return
	 */
	public Member findByName(String mailAddress) {
		return memberMapper.findByMailAddress(mailAddress);
	}
	
	
	/**
	 * 本登録後、仮ユーザーを自動的に削除します.
	 * 
	 * @param uuid
	 */
	public void deleteByProvisinalUser(String uuid) {
		memberMapper.deleteByProvisionalUser(uuid);
	}
	
	/**
	 * ユーザー情報を更新するメソッドです.
	 * 
	 * @param form
	 * @param secret
	 * @param loginMember
	 */
	public void updateMember(MemberForm form, String secret, String loginMember) {
		String encodePassword = passwordEncoder.encode(form.getPassword());
		Member member = new Member();
		BeanUtils.copyProperties(form, member);
		member.setPassword(encodePassword);
		member.setVerificationCode(secret);
		
		memberMapper.updateUser(member,loginMember);
	}
}
