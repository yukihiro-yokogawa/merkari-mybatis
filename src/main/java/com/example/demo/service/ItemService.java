package com.example.demo.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Item;
import com.example.demo.domain.SearchCategory;
import com.example.demo.form.ItemForm;
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
	 * 商品を検索するメソッドです.
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
	 * 商品の件数を取得するメソッドです.
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
	
	/**
	 * 特定商品の詳細を取得するメソッドです.
	 * 
	 * @param itemId
	 * @param detail
	 * @return
	 */
	public Item itemDetail(Integer itemId,String detail) {

		Item itemDetail = itemMapper.selectItem(itemId, detail);
		
		return itemDetail;
	}
	
	/**
	 * 特定商品の詳細を更新するメソッドです.
	 * 
	 * @param itemDetail 商品詳細
	 */
	public void updateItem(ItemForm form) {
		
		Item itemDetail = new Item();
		//フォームの情報をItemオブジェクトにセット
		BeanUtils.copyProperties(form, itemDetail);
		itemDetail.setCondition(Integer.parseInt(form.getCondition()));
		if(!form.getGrandChildId().equals("0")) {
			itemDetail.setGrandChildId(Integer.parseInt(form.getGrandChildId()));
		}
		itemDetail.setPrice(Integer.parseInt(form.getPrice()));

		itemMapper.updateItem(itemDetail);
	}
	
	public void insertItem(ItemForm form) {
		Item itemDetail = new Item();
		BeanUtils.copyProperties(form, itemDetail);
		
		itemDetail.setCondition(Integer.parseInt(form.getCondition()));
		itemDetail.setParentId(Integer.parseInt(form.getParentId()));
		itemDetail.setChildId(Integer.parseInt(form.getChildId()));
		itemDetail.setGrandChildId(Integer.parseInt(form.getGrandChildId()));
		itemDetail.setPrice(Integer.parseInt(form.getPrice()));
		
		itemMapper.insertItem(itemDetail);
	}

}
