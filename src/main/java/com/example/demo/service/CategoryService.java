package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Category;
import com.example.demo.mapper.CategoryMapper;

/**
 * Categoryテーブルから受け取ったデータを操作するサービスクラスです.
 * 
 * @author yukihiro.yokogawa
 *
 */
@Service
public class CategoryService {

	@Autowired
	private CategoryMapper categoryMapper;
	
	/**
	 * カテゴリの親子関係を保持したリストを返すメソッドです.
	 * 
	 * @return
	 */
	public List<Category> categoryList(){
		return categoryMapper.selectCategory();
	}
	
}
