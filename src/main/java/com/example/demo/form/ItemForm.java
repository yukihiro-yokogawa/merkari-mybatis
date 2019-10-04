package com.example.demo.form;

import lombok.Data;

@Data
public class ItemForm {

	/** 商品ID */
	String id;

	/**	商品名 */
	String name;

	/** 商品状況 */
	String condition;

	/**	親カテゴリID */
	String parentId;
	
	/**	親カテゴリ名 */
	String parentName;
	
	/**	子カテゴリID */
	String childId;
	
	/** 子カテゴリ名 */
	String childName;
	
	/**	孫カテゴリID */
	String grandChildId;
	
	/**	孫カテゴリ名 */
	String grandChildName;

	/** ブランド名 */
	String brand;

	/** 値段 */
	String price;

	/** 配送方法 */
	String shipping;

	/** 商品概要 */
	String description;
	
}
