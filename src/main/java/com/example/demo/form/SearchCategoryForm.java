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
	
	/**	検索したいページ */
	Integer page;

	/**	最大ページ */
	Integer maxPage;
	
	/**
	 * ページの検索タイプ
	 * viewで
	 * topページを表示したときはselect
	 * nextを押した場合next
	 * prevを押した場合prev
	 * ページ検索した場合search
	 * 商品検索をした場合searchItem
	 * brand名を押して検索したときはsearchBrand
	 * 商品詳細のページに遷移した際はitemDetail
	 * 
	 */
	String type;
	
	Integer maxId;
	
	Integer minId;
	
}
