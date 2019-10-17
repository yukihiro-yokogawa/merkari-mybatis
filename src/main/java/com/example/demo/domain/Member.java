package com.example.demo.domain;

import java.sql.Date;

import lombok.Data;

/**
 * ユーザー情報のEntityクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Data
public class Member {
	
	/**	メールアドレス */
	String mailAddress;
	
	/**	パスワード */
	String password;
	
	/**	仮登録認証トークン */
	String uuid;
	
	/**	登録日 */
	Date registerDate;
	
	/**	ワンタイムパスワードの認証キー */
	String verificationCode;
	
	/**	権限 */
	Integer authority;
	
	public boolean isAdmin() {
		if(getAuthority() == -1) {
			return true;
		}
		return false;
	}
	
	public boolean isUsingOTP() {
		if(getVerificationCode() == null) {
			return false;
		}
		return true;
	}
	
}
