package com.itblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itblog.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT c.id, c.name, IFNULL(c.parent_id, 0) AS parentId, " +
            "COALESCE((SELECT COUNT(*) FROM busi_article a WHERE a.status = 1 AND a.is_deleted = 0 " +
            "  AND a.category_id = c.id), 0) AS articleCount " +
            "FROM busi_category c ORDER BY c.id")
    List<Map<String, Object>> listWithArticleCount();
}
