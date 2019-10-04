package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
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
import com.example.demo.domain.Paging;
import com.example.demo.form.SearchCategoryForm;
import com.example.demo.service.ItemService;
import com.google.common.collect.Iterables;

@Controller
@RequestMapping("/item")
@SessionAttributes(value = "searchCategoryForm")
public class ItemController {

	@Autowired
	ItemService itemService;

	//商品検索した値を保持する.
	@ModelAttribute("searchCategoryForm")
	public SearchCategoryForm searchCategoryForm() {
		return new SearchCategoryForm();
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
		session.setAttribute("maxId", Iterables.getLast(itemList).getId());

		// 商品の全件数を検索
		Item countItem = itemService.selectItem("count", searchCategoryForm);
		Integer maxPage = (countItem.getCount() / 30) + 1;
		session.setAttribute("maxPage", maxPage);

		// トップページなのでページ数1を指定する
		model.addAttribute("page", 1);
		
		model.addAttribute("searchCategoryForm",searchCategoryForm);
		return "list";
	}

	/**
	 * 検索用のview
	 * 
	 * @param paging
	 * @param rs
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("/searchItem")
	public String searchItem(@Validated Paging paging, BindingResult rs, Model model, HttpSession session,SearchCategoryForm searchCategoryForm) {
		// 変数を初期化
		Integer nextId = 1;
		Integer prevId = 1;
		Integer maxPage = 1;
		List<Item> itemList = new ArrayList<>();
		
		if(session.getAttribute("maxPage") != null){
			//nullチェック
			maxPage = Integer.parseInt(String.valueOf(session.getAttribute("maxPage")));
		}

		if (session.getAttribute("maxId") != null) {
			// nullチェック ページング next用
			nextId = Integer.parseInt(String.valueOf(session.getAttribute("maxId"))) + 1;
		}

		if (session.getAttribute("minId") != null) {
			// nullチェック ページング prev用
			prevId = Integer.parseInt(String.valueOf(session.getAttribute("minId"))) - 1;
		}

		if (paging.getType().equals("next")) {
			// nextが押されたとき
			itemList = itemService.selectItem(nextId, paging.getType(), searchCategoryForm);
		} else if (paging.getType().equals("prev")) {
			// prevが押されたとき
			itemList = itemService.selectItem(prevId, paging.getType(), searchCategoryForm);
			Collections.reverse(itemList);
		} else if (paging.getType().equals("search")) {
			// ページ検索されたとき
			if (paging.getPage() == 1) {
				//検索直後必ず1ページ目に遷移させる、フォームに1を入れてもこちらがはしる
				Item countItem = itemService.selectItem("searchCount", searchCategoryForm);
				itemList = itemService.selectItem(countItem.getMinId(), "searchItem", searchCategoryForm);
			} else {
				//入力欄に数字を入れて検索するとき
				Integer offset = ((paging.getPage() - 1) * 30);
				itemList = itemService.selectItem(offset, paging.getType(), searchCategoryForm);
			}
		} else {
			//検索したい商品の総数とそれ全体の最小のIDを取得（検索条件を絞りSQLを高速化するため）
			Item countItem = itemService.selectItem("searchCount", searchCategoryForm);
			itemList = itemService.selectItem(countItem.getMinId(), paging.getType(), searchCategoryForm);
			maxPage = (countItem.getCount() / 30) + 1;
			//null対策
			paging.setPage(1);
		}

		//表示された商品の中の最大のID（ページング用）
		session.setAttribute("maxId", Iterables.getLast(itemList).getId());
		//表示された商品の中の最小のID（ページング用）
		session.setAttribute("minId", itemList.get(0).getId());
		
		model.addAttribute("itemList", itemList);
		session.setAttribute("maxPage", maxPage);
		
		//現在のページ数を送信
		model.addAttribute("page", paging.getPage());
		
		model.addAttribute("searchCategoryForm",searchCategoryForm);
		return "list";
	}
	
	/**
	 * 商品詳細画面を表示させるViewメソッドです.
	 * 
	 * @return
	 */
	@RequestMapping("/detail")
	public String itemDetail(int itemId, String type, Model model, SearchCategoryForm searchCategoryForm) {
		
		Map<String,String> detailMap = itemService.itemDetail(itemId, type);
		
		//SessionAttributesを再送信
		model.addAttribute("searchCategoryForm",searchCategoryForm);
		model.addAttribute("detailMap",detailMap);
		return "detail";
	}

}
