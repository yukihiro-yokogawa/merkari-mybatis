package com.example.demo.domain;

import lombok.Data;

/**
 * 商品情報を受け取るクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Data
public class Item {

	/** 商品ID */
	Integer id;

	/**	商品名 */
	String name;

	/** 商品状況 */
	Integer condition;

	/**	親カテゴリID */
	Integer parentId;
	
	/**	親カテゴリ名 */
	String parentName;
	
	/**	子カテゴリID */
	Integer childId;
	
	/** 子カテゴリ名 */
	String childName;
	
	/**	孫カテゴリID */
	Integer grandChildId;
	
	/**	孫カテゴリ名 */
	String grandChildName;

	/** ブランド名 */
	String brand;

	/** 値段 */
	Integer price;

	/** 配送方法 */
	Integer shipping;

	/** 商品概要 */
	String description;
	
	/** 商品数 */
	Integer count;
	
	/**	ページ内の最小ID */
	Integer minId;

}
