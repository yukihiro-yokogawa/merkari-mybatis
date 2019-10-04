package com.example.demo.domain;

import lombok.Data;

/**
 * ページング用のリクエストパラメータを受け取るEntityクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Data
public class Paging {
	
	/**	検索したいページ */
	Integer page;
	
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
	
}
