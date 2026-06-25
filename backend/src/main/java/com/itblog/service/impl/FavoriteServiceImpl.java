package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itblog.common.BusinessException;
import com.itblog.entity.*;
import com.itblog.mapper.*;
import com.itblog.service.FavoriteService;
import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, ArticleMapper articleMapper,
                               UserMapper userMapper, CategoryMapper categoryMapper,
                               ArticleTagMapper articleTagMapper, TagMapper tagMapper) {
        this.favoriteMapper = favoriteMapper;
        this.articleMapper = articleMapper;
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.articleTagMapper = articleTagMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public void toggleFavorite(Long articleId, Long userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException("Article not found");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setArticleId(articleId);

        try {
            favoriteMapper.insert(favorite);
        } catch (DuplicateKeyException e) {
            // 已收藏，取消收藏
            LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Favorite::getUserId, userId);
            wrapper.eq(Favorite::getArticleId, articleId);
            favoriteMapper.delete(wrapper);
        }
    }

    @Override
    public boolean isFavorited(Long articleId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getArticleId, articleId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }

    @Override
    public PageVO<ArticleVO> getFavoriteArticles(int page, int size, Long userId) {
        Page<Favorite> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.orderByDesc(Favorite::getCreateTime);
        Page<Favorite> favPage = favoriteMapper.selectPage(pageParam, wrapper);

        List<ArticleVO> voList = new ArrayList<>();
        for (Favorite fav : favPage.getRecords()) {
            Article article = articleMapper.selectById(fav.getArticleId());
            if (article != null && article.getIsDeleted() == 0) {
                voList.add(toArticleVO(article));
            }
        }
        return new PageVO<>(voList, favPage.getTotal(), page, size);
    }

    private ArticleVO toArticleVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setCoverImg(article.getCoverImg());
        vo.setAuthorId(article.getAuthorId());
        vo.setCategoryId(article.getCategoryId());
        vo.setViewCount(article.getViewCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setCommentCount(article.getCommentCount());
        vo.setStatus(article.getStatus());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());

        User author = userMapper.selectById(article.getAuthorId());
        if (author != null) {
            vo.setAuthorName(author.getUsername());
            vo.setAuthorAvatar(author.getAvatar());
        }

        Category cat = categoryMapper.selectById(article.getCategoryId());
        if (cat != null) vo.setCategoryName(cat.getName());

        LambdaQueryWrapper<ArticleTag> atw = new LambdaQueryWrapper<>();
        atw.eq(ArticleTag::getArticleId, article.getId());
        List<ArticleTag> atList = articleTagMapper.selectList(atw);
        if (!atList.isEmpty()) {
            List<Long> tagIds = new ArrayList<>();
            for (ArticleTag at : atList) { tagIds.add(at.getTagId()); }
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            List<String> tagNames = new ArrayList<>();
            for (Tag t : tags) { tagNames.add(t.getName()); }
            vo.setTags(tagNames);
        }

        return vo;
    }
}
