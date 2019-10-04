package com.example.demo.domain;

import lombok.Data;

/**
 * 検索用のEntityクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Data
public class SearchCategory {

	/** 検索名 */
	String name;
	
	/** 親ID */
	Integer parentId;
	
	/** 子ID */
	Integer childId;
	
	/** 孫ID */
	Integer grandChildId;
	
	/** ブランド名 */
	String brand;
	
}
