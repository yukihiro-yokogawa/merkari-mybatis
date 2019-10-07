package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.demo.domain.Item;
import com.example.demo.form.ItemForm;
import com.example.demo.form.SearchCategoryForm;
import com.example.demo.service.ItemService;
import com.google.common.collect.Iterables;

@Controller
@RequestMapping("/item")
@SessionAttributes(types = {SearchCategoryForm.class,ItemForm.class,Item.class})
public class ItemController {

	@Autowired
	ItemService itemService;
	
	@ModelAttribute
	public ItemForm setUpItemForm() {
		return new ItemForm();
	}
	
	/**
	 * トップページのview
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/top")
	public String test(Model model, HttpSession session) {

		//トップページに戻った際SessionAttributeを初期化する.
		SearchCategoryForm searchCategoryForm = new SearchCategoryForm();
		searchCategoryForm.setParentId("0");
		searchCategoryForm.setChildId("0");
		searchCategoryForm.setGrandChildId("0");
		
		// ページに表示する商品リスト
		List<Item> itemList = itemService.selectItem(1, "select", searchCategoryForm);
		model.addAttribute("itemList", itemList);
		// ページの最後の商品のIDを取得
		searchCategoryForm.setMaxId(Iterables.getLast(itemList).getId());
		// ページの最初の商品のIDを取得（null対策）
		searchCategoryForm.setMinId(itemList.get(0).getId());

		// 商品の全件数を検索
		Item countItem = itemService.selectItem("count", searchCategoryForm);
		Integer maxPage = (countItem.getCount() / 30) + 1;
		searchCategoryForm.setMaxPage(maxPage);

		// トップページなのでページ数1を指定する
		searchCategoryForm.setPage(1);
		
		model.addAttribute("searchCategoryForm",searchCategoryForm);
		return "list";
	}

	/**
	 * 検索用のview
	 * 
	 * @param rs
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("/searchItem")
	public String searchItem(@Validated SearchCategoryForm searchCategoryForm, BindingResult rs, Model model) {

		Integer nextId = searchCategoryForm.getMaxId() + 1;
		Integer prevId = searchCategoryForm.getMinId() - 1;
		Integer maxPage = searchCategoryForm.getMaxPage();
		List<Item> itemList = new ArrayList<>();
	
		if (searchCategoryForm.getType().equals("next")) {
			// nextが押されたとき
			itemList = itemService.selectItem(nextId, searchCategoryForm.getType(), searchCategoryForm);
		} else if (searchCategoryForm.getType().equals("prev")) {
			// prevが押されたとき
			itemList = itemService.selectItem(prevId, searchCategoryForm.getType(), searchCategoryForm);
			//DESCで呼び出してきたものをASCに並び替える
			Collections.reverse(itemList);
		} else if (searchCategoryForm.getType().equals("search")) {
			// ページ検索されたとき
			if (searchCategoryForm.getPage() == 1) {
				//検索直後必ず1ページ目に遷移させる、フォームに1を入れてもこちらがはしる
				Item countItem = itemService.selectItem("searchCount", searchCategoryForm);
				itemList = itemService.selectItem(countItem.getMinId(), "searchItem", searchCategoryForm);
			} else {
				//入力欄に数字を入れて検索するとき
				Integer offset = ((searchCategoryForm.getPage() - 1) * 30);
				itemList = itemService.selectItem(offset, searchCategoryForm.getType(), searchCategoryForm);
			}
		} else {
			//検索したい商品の総数とそれ全体の最小のIDを取得（検索条件を絞りSQLを高速化するため）
			Item countItem = itemService.selectItem("searchCount", searchCategoryForm);
			itemList = itemService.selectItem(countItem.getMinId(), searchCategoryForm.getType(), searchCategoryForm);
			maxPage = (countItem.getCount() / 30) + 1;
			//null対策
			searchCategoryForm.setPage(1);
		}

		//表示された商品の中の最大のID（ページング用）
		searchCategoryForm.setMaxId(Iterables.getLast(itemList).getId());
		//表示された商品の中の最小のID（ページング用）
		searchCategoryForm.setMinId(itemList.get(0).getId());		
		model.addAttribute("itemList", itemList);
		model.addAttribute("searchCategoryForm",searchCategoryForm);
		
		searchCategoryForm.setMaxPage(maxPage);
			
		return "list";
	}
	
	/**
	 * 商品詳細画面を表示させるViewメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("/detail")
	public String itemDetail(SearchCategoryForm searchCategoryForm, ItemForm itemForm, Model model) {
		
		Item itemDetail = itemService.itemDetail(Integer.parseInt(itemForm.getId()), searchCategoryForm.getType());
		if(itemDetail.getParentName() == null) {
			itemDetail.setParentName("no-category");
			itemDetail.setChildName("no-category");
			itemDetail.setGrandChildName("no-category");
		}
		
		Map<String,String> detailMap = new LinkedHashMap<>();
		detailMap.put("ID", String.valueOf(itemDetail.getId()));
		detailMap.put("name", itemDetail.getName());
		detailMap.put("price", String.valueOf(itemDetail.getPrice()));
		if(!itemDetail.getParentName() .equals("no-category")) {
			detailMap.put("category", itemDetail.getParentName() + "/" + itemDetail.getChildName() +"/" + itemDetail.getGrandChildName());
		}else {
			detailMap.put("category","no-category");
		}
		detailMap.put("brand", itemDetail.getBrand());
		detailMap.put("condition", String.valueOf(itemDetail.getCondition()));
		detailMap.put("description", itemDetail.getDescription());
		
		model.addAttribute("itemForm",itemForm);
		model.addAttribute("detailMap",detailMap);
		model.addAttribute("itemDetail",itemDetail);
		return "detail";
	}
	
	@RequestMapping("/edit")
	public String itemEdit(Item itemDetail, Model model) {
		return "edit";
	}

}
