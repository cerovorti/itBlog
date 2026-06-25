package com.itblog.service;

import com.itblog.entity.Category;
import com.itblog.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    List<Category> getCategoryTree();
    List<CategoryVO> getCategoryList();
    void addCategory(String name, Long parentId);
    void deleteCategory(Long id);
}
