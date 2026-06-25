package com.itblog.service;

import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;

public interface FavoriteService {
    void toggleFavorite(Long articleId, Long userId);
    boolean isFavorited(Long articleId, Long userId);
    PageVO<ArticleVO> getFavoriteArticles(int page, int size, Long userId);
}
