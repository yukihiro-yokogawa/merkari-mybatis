package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
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

/**
 * 商品の情報のView関係を扱うコントローラークラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Controller
@RequestMapping("/item")
@SessionAttributes(types = { SearchCategoryForm.class, ItemForm.class, Item.class })
public class ItemController {

	@Autowired
	ItemService itemService;

	@ModelAttribute
	public ItemForm setUpItemForm() {
		return new ItemForm();
	}

	@ModelAttribute
	public SearchCategoryForm setUpSearchCategoryForm() {
		return new SearchCategoryForm();
	}

	@ModelAttribute
	public Item setUpItem() {
		return new Item();
	}

	/**
	 * トップページのview
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/top")
	public String test(Model model) {

		// トップページに戻った際SessionAttributeを初期化する.
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
		searchCategoryForm.setMaxPage(String.valueOf(maxPage));

		// トップページなのでページ数1を指定する
		searchCategoryForm.setPage("1");

		model.addAttribute("searchCategoryForm", searchCategoryForm);
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
		
		//数字以外の文字がページ検索欄に入力されている場合
		if (rs.hasErrors()) {
			searchCategoryForm.setPage("1");
		}

		Integer nextId = searchCategoryForm.getMaxId() + 1;
		Integer prevId = searchCategoryForm.getMinId() - 1;
		Integer maxPage = Integer.parseInt(searchCategoryForm.getMaxPage());
		Integer page = Integer.parseInt(searchCategoryForm.getPage());
		List<Item> itemList = new ArrayList<>();

		if (page <= 0) {
			rs.rejectValue("page", "", "Out of range.");
			page = 1;
		} else if (page > maxPage) {
			rs.rejectValue("page", "", "Out of range.");
			page = Integer.parseInt(searchCategoryForm.getMaxPage());
		}

		if (searchCategoryForm.getType().equals("next")) {
			// nextが押されたとき
			itemList = itemService.selectItem(nextId, searchCategoryForm.getType(), searchCategoryForm);
		} else if (searchCategoryForm.getType().equals("prev")) {
			// prevが押されたとき
			itemList = itemService.selectItem(prevId, searchCategoryForm.getType(), searchCategoryForm);
			// DESCで呼び出してきたものをASCに並び替える
			Collections.reverse(itemList);
		} else if (searchCategoryForm.getType().equals("search")) {
			// ページ検索されたとき
			if (page == 1) {
				// 検索直後必ず1ページ目に遷移させる、フォームに1を入れてもこちらがはしる
				Item countItem = itemService.selectItem("searchCount", searchCategoryForm);
				itemList = itemService.selectItem(countItem.getMinId(), "searchItem", searchCategoryForm);
				maxPage = (countItem.getCount() / 30) + 1;
			} else {
				// 入力欄に数字を入れて検索するとき
				Integer offset = ((page - 1) * 30);
				itemList = itemService.selectItem(offset, searchCategoryForm.getType(), searchCategoryForm);
			}
		} else {
			// 検索したい商品の総数とそれ全体の最小のIDを取得（検索条件を絞りSQLを高速化するため）
			Item countItem = itemService.selectItem("searchCount", searchCategoryForm);
			// 検索したときに条件に一致した商品がなかった場合、最小IDに値が入らずnullになるのでエラーメッセージを付加してreturnする
			if (countItem.getMinId() == null) {
				searchCategoryForm.setMaxPage("1");
				rs.rejectValue("name", "", "Product not found. Please search again.");
				return "list";
			}
			itemList = itemService.selectItem(countItem.getMinId(), searchCategoryForm.getType(), searchCategoryForm);
			maxPage = (countItem.getCount() / 30) + 1;
			// null対策
			searchCategoryForm.setPage("1");
		}
		
		ItemForm itemForm = new ItemForm();

		// 表示された商品の中の最大のID（ページング用）
		searchCategoryForm.setMaxId(Iterables.getLast(itemList).getId());
		// 表示された商品の中の最小のID（ページング用）
		searchCategoryForm.setMinId(itemList.get(0).getId());
		model.addAttribute("itemList", itemList);
		model.addAttribute("itemForm",itemForm);
		model.addAttribute("searchCategoryForm", searchCategoryForm);

		searchCategoryForm.setMaxPage(String.valueOf(maxPage));

		return "list";
	}

	/**
	 * 商品詳細画面を表示させるViewメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("/detail")
	public String itemDetail(SearchCategoryForm searchCategoryForm,ItemForm itemForm, Model model) {
		//商品詳細を検索
		Item itemDetail = itemService.itemDetail(Integer.parseInt(itemForm.getId()), searchCategoryForm.getType());
		if (itemDetail.getParentName() == null) {
			itemDetail.setParentName("no-category");
			itemDetail.setChildName("no-category");
			itemDetail.setGrandChildName("no-category");
		}
		
		//商品詳細を表示させるため
		Map<String, String> detailMap = new LinkedHashMap<>();
		detailMap.put("ID", String.valueOf(itemDetail.getId()));
		detailMap.put("name", itemDetail.getName());
		detailMap.put("price", String.valueOf(itemDetail.getPrice()));
		if (!itemDetail.getParentName().equals("no-category")) {
			detailMap.put("category", itemDetail.getParentName() + "/" + itemDetail.getChildName() + "/" + itemDetail.getGrandChildName());
		}
		detailMap.put("brand", itemDetail.getBrand());
		detailMap.put("condition", String.valueOf(itemDetail.getCondition()));
		detailMap.put("description", itemDetail.getDescription());
		
		//フォームバインディング用（変更したい商品の情報を商品追加画面で入力フォームに入れておくため）
		BeanUtils.copyProperties(itemDetail, itemForm);
		itemForm.setCondition(String.valueOf(itemDetail.getCondition()));
		itemForm.setPrice(String.valueOf(itemDetail.getPrice()));
		itemForm.setShipping(String.valueOf(itemDetail.getShipping()));
		itemForm.setParentId(String.valueOf(itemDetail.getParentId()));
		itemForm.setChildId(String.valueOf(itemDetail.getChildId()));
		itemForm.setGrandChildId(String.valueOf(itemDetail.getGrandChildId()));
		
		model.addAttribute("detailMap", detailMap);
		model.addAttribute("itemForm" , itemForm);
		model.addAttribute("itemDetail", itemDetail);
		return "detail";
	}

	/**
	 * 商品詳細を更新フォームを表示させるViewメソッドです.
	 * 
	 * @param itemDetail
	 * @param model
	 * @return
	 */
	@RequestMapping("/edit")
	public String itemEdit(Model model) {
		//condition
		List<Integer> conditionList = new ArrayList<>();
		for(int i = 1; i <= 3; i++) {
			conditionList.add(i);
		}
		model.addAttribute("conditionList",conditionList);
		return "edit";
	}
	
	/**
	 * 商品詳細を更新するメソッドです.
	 * 
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping("/updateProcess")
	public String itemUpdateProcess(@Validated ItemForm form, BindingResult rs, Model model) {
		if(rs.hasErrors()) {
			return itemEdit(model);
		}
		itemService.updateItem(form);
		return "redirect:detail";
	}
	
	/**
	 * 商品の追加フォームのviewを表示させるメソッドです.
	 * 
	 * @return 商品追加フォーム
	 */
	@RequestMapping("/add")
	public String itemAdd(Model model) {
		List<Integer> conditionList = new ArrayList<>();
		for(int i = 1; i <= 3; i++) {
			conditionList.add(i);
		}
		model.addAttribute("conditionList",conditionList);
		return "add";
	}
	
	/**
	 * 新規商品を追加するメソッドです.
	 * 
	 * @param form 空のItemForm
	 * @param rs
	 * @param model
	 * @return 直前の商品一覧
	 */
	@RequestMapping("/addProcess")
	public String itemAddProcess(@Validated ItemForm form, BindingResult rs, Model model) {
		if(rs.hasErrors()) {
			return itemAdd(model);
		}
		itemService.insertItem(form);
		return "redirect:searchItem";
	}

}
