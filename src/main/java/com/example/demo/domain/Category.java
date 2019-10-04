package com.example.demo.domain;

import java.util.List;

import lombok.Data;

@Data
public class Category {
	
	Integer id;
	
	String name;
	
	List<Category> childCategoryList;
}
