package com.cm.cmoj.controller;

import com.cm.cmoj.model.entity.Category;
import com.cm.cmoj.model.vo.discuss.Result;
import com.cm.cmoj.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 分类
 * @Author: Naccl
 * @Date: 2020-08-19
 */
@RestController
public class CategoryController {
	@Autowired
	CategoryService categoryService;
	@GetMapping("/getAllCategory")
	public Result getAllCategory() {
		List<Category> categoryList = categoryService.getCategoryList();
		return Result.ok("请求成功", categoryList);
	}
}
