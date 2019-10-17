package com.example.demo.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class ItemForm {

	/** 商品ID */
	String id;

	/**	商品名 */
	@NotBlank(message="Please enter a name.")
	String name;

	/** 商品状況 */
	@NotBlank(message="Please checked a condition.")
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
	@NotBlank(message="Please enter a price.")
	@Pattern(regexp="[0-9]+", message="Please enter a half-width digit.")
	String price;

	/** 配送方法 */
	String shipping;

	/** 商品概要 */
	@NotBlank(message="Please enter a description.")
	String description;
	
}
