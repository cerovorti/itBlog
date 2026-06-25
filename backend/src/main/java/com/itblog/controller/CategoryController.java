package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.Category;
import com.itblog.service.CategoryService;
import com.itblog.vo.CategoryVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/tree")
    public Result<List<Category>> tree() {
        return Result.ok(categoryService.getCategoryTree());
    }

    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        return Result.ok(categoryService.getCategoryList());
    }
}
