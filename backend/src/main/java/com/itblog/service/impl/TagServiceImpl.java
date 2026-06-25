package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itblog.common.BusinessException;
import com.itblog.entity.Article;
import com.itblog.entity.ArticleTag;
import com.itblog.entity.Tag;
import com.itblog.mapper.ArticleMapper;
import com.itblog.mapper.ArticleTagMapper;
import com.itblog.mapper.TagMapper;
import com.itblog.service.TagService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleMapper articleMapper;

    public TagServiceImpl(TagMapper tagMapper, ArticleTagMapper articleTagMapper, ArticleMapper articleMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public List<Tag> getTagList() {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .orderByAsc(Tag::getName));
    }

    @Override
    public List<Tag> getTagsByCategory(Long categoryId) {
        if (categoryId == null) return getTagList();

        LambdaQueryWrapper<Article> aw = new LambdaQueryWrapper<>();
        aw.eq(Article::getCategoryId, categoryId);
        List<Long> articleIds = articleMapper.selectList(aw).stream()
                .map(Article::getId).collect(Collectors.toList());

        if (articleIds.isEmpty()) return new ArrayList<>();

        LambdaQueryWrapper<ArticleTag> atw = new LambdaQueryWrapper<>();
        atw.in(ArticleTag::getArticleId, articleIds);
        List<Long> tagIds = articleTagMapper.selectList(atw).stream()
                .map(ArticleTag::getTagId).distinct().collect(Collectors.toList());

        if (tagIds.isEmpty()) return new ArrayList<>();

        return tagMapper.selectBatchIds(tagIds);
    }

    @Override
    public Tag createTag(String name) {
        Tag existing = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, name));
        if (existing != null) return existing;
        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getTagCloud(int limit) {
        // 只统计已发布且未删除的文章
        LambdaQueryWrapper<Article> aw = new LambdaQueryWrapper<>();
        aw.eq(Article::getStatus, 1);
        aw.eq(Article::getIsDeleted, 0);
        List<Long> publishedIds = articleMapper.selectList(aw).stream()
                .map(Article::getId).collect(Collectors.toList());

        // 统计每个标签在这些文章中的出现次数
        Map<Long, Long> countMap = new HashMap<>();
        if (!publishedIds.isEmpty()) {
            LambdaQueryWrapper<ArticleTag> atw = new LambdaQueryWrapper<>();
            atw.in(ArticleTag::getArticleId, publishedIds);
            List<ArticleTag> atList = articleTagMapper.selectList(atw);
            for (ArticleTag at : atList) {
                countMap.merge(at.getTagId(), 1L, Long::sum);
            }
        }

        // 获取所有标签
        List<Tag> allTags = tagMapper.selectList(null);

        // 按文章数量降序排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tag tag : allTags) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", tag.getId());
            item.put("name", tag.getName());
            item.put("articleCount", countMap.getOrDefault(tag.getId(), 0L));
            result.add(item);
        }
        result.sort((a, b) -> Long.compare(
                (Long) b.get("articleCount"), (Long) a.get("articleCount")));

        if (limit > 0 && result.size() > limit) {
            return result.subList(0, limit);
        }
        return result;
    }
}
