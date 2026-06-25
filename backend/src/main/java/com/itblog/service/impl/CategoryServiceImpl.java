package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itblog.common.BusinessException;
import com.itblog.entity.Category;
import com.itblog.mapper.CategoryMapper;
import com.itblog.service.CategoryService;
import com.itblog.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getCategoryTree() {
        return categoryMapper.selectList(null);
    }

    @Override
    public List<CategoryVO> getCategoryList() {
        List<Map<String, Object>> rows = categoryMapper.listWithArticleCount();
        List<CategoryVO> list = new ArrayList<>();
        // 按 (name, parentId) 去重，保留最小 ID 的那条
        Map<String, CategoryVO> dedup = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long id = ((Number) row.get("id")).longValue();
            String name = (String) row.get("name");
            Long parentId = row.get("parentId") != null ? ((Number) row.get("parentId")).longValue() : 0L;
            Long count = ((Number) row.get("articleCount")).longValue();
            String key = name + "|" + parentId;
            CategoryVO existing = dedup.get(key);
            if (existing == null || id < existing.getId()) {
                CategoryVO vo = new CategoryVO();
                vo.setId(id);
                vo.setName(name);
                vo.setParentId(parentId == 0 ? null : parentId);
                vo.setArticleCount(count);
                dedup.put(key, vo);
            }
        }
        return new ArrayList<>(dedup.values());
    }

    @Override
    public void addCategory(String name, Long parentId) {
        Category cat = new Category();
        cat.setName(name);
        cat.setParentId(parentId != null ? parentId : 0L);
        categoryMapper.insert(cat);
    }

    @Override
    public void deleteCategory(Long id) {
        LambdaQueryWrapper<Category> w = new LambdaQueryWrapper<>();
        w.eq(Category::getParentId, id);
        if (categoryMapper.selectCount(w) > 0) {
            throw new BusinessException("Category has children, cannot delete");
        }
        categoryMapper.deleteById(id);
    }
}
