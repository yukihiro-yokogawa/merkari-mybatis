package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.Member;

@Mapper
public interface MemberMapper {
	
	void insertProvisionalMember(@Param("member") Member member);

	void insertMember(@Param("uuid") String uuid);
	
	Member findByMailAddress(@Param("mailAddress") String mailAddress);
	
	Member findByProvisionalMember(@Param("uuid") String uuid);
	
	void deleteByProvisionalUser(@Param("uuid") String uuid);
	
	void updateUser(@Param("member") Member member,@Param("loginUser") String loginUser);
	
	void updateLocker(@Param("mailAddress") String mailAddress ,@Param("count") Integer lockCount);
	
}
