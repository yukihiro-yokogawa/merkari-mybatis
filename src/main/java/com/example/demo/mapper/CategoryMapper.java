package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.Category;

@Mapper
public interface CategoryMapper {

	List<Category> selectCategory();
	
}
