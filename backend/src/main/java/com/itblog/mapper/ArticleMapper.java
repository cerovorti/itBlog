package com.itblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itblog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT author_id AS authorId, SUM(like_count) AS totalLikes FROM busi_article WHERE status = 1 AND is_deleted = 0 GROUP BY author_id")
    List<Map<String, Object>> sumLikesByAuthor();
}
