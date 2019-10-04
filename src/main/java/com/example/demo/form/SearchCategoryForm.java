package com.example.demo.form;

import lombok.Data;

/**
 * 商品検索フォームのリクエストパラメータを受け取るフォームクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Data
public class SearchCategoryForm {

	/** 検索名 */
	String name;
	
	/** 親ID */
	String parentId;
	
	/** 子ID */
	String childId;
	
	/** 孫ID */
	String grandChildId;
	
	/** ブランド名 */
	String brand;
	
}
