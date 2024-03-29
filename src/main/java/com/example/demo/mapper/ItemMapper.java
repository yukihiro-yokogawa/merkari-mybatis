package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.Item;
import com.example.demo.domain.SearchCategory;

/**
 * 商品情報を操作するインターフェース.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Mapper
public interface ItemMapper {
	
	/**
	 * 商品の全件検索
	 * 
	 * @param id
	 * @param select
	 * @return
	 */
	List<Item> selectItem(@Param("id") int id,@Param("type") String type,@Param("searchCategory") SearchCategory searchCategory);

	/**
	 * 商品の全件数と最小ID
	 * 
	 * @param count
	 * @return
	 */
	Item selectItem(@Param("type") String count,@Param("searchCategory") SearchCategory searchCategory);
	
	Item selectItem(@Param("id") int itemId,@Param("type") String detail);
	
}
