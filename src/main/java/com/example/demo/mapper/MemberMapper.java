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
	
	void deleteByProvisionalUser(@Param("mailAddress") String mailAddress);
	
	void updateUser(@Param("member") Member member);
	
	void updateLocker(@Param("member") Member member);
	
}
