package com.example.demo.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Item;
import com.example.demo.domain.SearchCategory;
import com.example.demo.form.SearchCategoryForm;
import com.example.demo.mapper.ItemMapper;

/**
 * 商品に関するフォームが受け取った情報を操作するサービスクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Service
public class ItemService {

	@Autowired
	ItemMapper itemMapper;

	/**
	 * 商品の検索
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<Item> selectItem(int id, String type, SearchCategoryForm searchCategoryForm) {
		SearchCategory searchCategory = new SearchCategory();
		BeanUtils.copyProperties(searchCategoryForm, searchCategory);

		searchCategory.setParentId(Integer.parseInt(searchCategoryForm.getParentId()));
		searchCategory.setChildId(Integer.parseInt(searchCategoryForm.getChildId()));
		searchCategory.setGrandChildId(Integer.parseInt(searchCategoryForm.getGrandChildId()));
		List<Item> itemList = itemMapper.selectItem(id, type, searchCategory);
		return itemList;
	}

	/**
	 * 商品の件数
	 * 
	 * @param count
	 * @return
	 */
	public Item selectItem(String count, SearchCategoryForm searchCategoryForm) {
		SearchCategory searchCategory = new SearchCategory();
		BeanUtils.copyProperties(searchCategoryForm, searchCategory);
		searchCategory.setParentId(Integer.parseInt(searchCategoryForm.getParentId()));
		searchCategory.setChildId(Integer.parseInt(searchCategoryForm.getChildId()));
		searchCategory.setGrandChildId(Integer.parseInt(searchCategoryForm.getGrandChildId()));
		Item itemCount = itemMapper.selectItem(count, searchCategory);
		return itemCount;
	}
	
	public Map<String,String> itemDetail(int itemId,String detail) {
		Item itemDetail = itemMapper.selectItem(itemId, detail);
		
		Map<String,String> detailMap = new LinkedHashMap<>();
		detailMap.put("ID", String.valueOf(itemDetail.getId()));
		detailMap.put("name", itemDetail.getName());
		detailMap.put("price", String.valueOf(itemDetail.getPrice()));
		if(itemDetail.getParentName() != null) {
			detailMap.put("category", itemDetail.getParentName() + "/" + itemDetail.getChildName() +"/" + itemDetail.getGrandChildName());
		} else {
			detailMap.put("category", "");			
		}
		detailMap.put("brand", itemDetail.getBrand());
		detailMap.put("condition", String.valueOf(itemDetail.getCondition()));
		detailMap.put("description", itemDetail.getDescription());
		
		
		return detailMap;
	}

}
