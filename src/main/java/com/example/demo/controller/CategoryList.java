package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.domain.Category;
import com.example.demo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 検索用のカテゴリリストをJson形式で送るコントローラークラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Controller
public class CategoryList {

	@Autowired
	private CategoryService categoryService;
	
	/**
	 * 親子関係のカテゴリリストをJson形式でjsに送信.
	 * 
	 * @return Json形式のカテゴリリスト
	 */
	@RequestMapping("/categoryList")
	@ResponseBody
	public String categoryList(){
		List<Category> categoryList = new ArrayList<>();
		categoryList = categoryService.categoryList();
		String categoryJson = null;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			//ArrayListをJson形式に変換してString型に格納
			categoryJson = mapper.writeValueAsString(categoryList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return categoryJson;
	}
	
}
