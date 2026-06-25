package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itblog.entity.Like;
import com.itblog.mapper.ArticleMapper;
import com.itblog.mapper.LikeMapper;
import com.itblog.service.LikeService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final ArticleMapper articleMapper;
    public LikeServiceImpl(LikeMapper likeMapper, ArticleMapper articleMapper) {
        this.likeMapper = likeMapper;
        this.articleMapper = articleMapper;
    }


    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        Like like = new Like();
        like.setUserId(userId);
        like.setArticleId(articleId);

        try {
            likeMapper.insert(like);
            articleMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.itblog.entity.Article>()
                            .eq(com.itblog.entity.Article::getId, articleId)
                            .setSql("like_count = like_count + 1"));
            return Map.of("liked", true);
        } catch (DuplicateKeyException e) {
            // 已点赞，取消点赞
            likeMapper.delete(new LambdaQueryWrapper<Like>()
                    .eq(Like::getUserId, userId)
                    .eq(Like::getArticleId, articleId));
            articleMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.itblog.entity.Article>()
                            .eq(com.itblog.entity.Article::getId, articleId)
                            .setSql("like_count = GREATEST(like_count - 1, 0)"));
            return Map.of("liked", false);
        }
    }
}

