package com.example.demo.domain;

import lombok.Data;

@Data
public class Mail {

	/** メールのタイトル */
	String title;
	
	/**	送信先 */
	String mailAddress;
	
	/**	送るページ */
	String html;
	
}
