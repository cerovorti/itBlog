package com.itblog.service;

import com.itblog.dto.ArticlePublishRequest;
import com.itblog.vo.ArticleDetailVO;
import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;

public interface ArticleService {
    PageVO<ArticleVO> getArticleList(int page, int size, String sort, Long categoryId, Long tagId, boolean publishedOnly);
    PageVO<ArticleVO> getMyArticles(int page, int size, Long authorId, boolean includePending);
    PageVO<ArticleVO> getDrafts(int page, int size, Long authorId);
    PageVO<ArticleVO> searchArticles(int page, int size, String keyword);
    ArticleDetailVO getArticleDetail(Long articleId, Long currentUserId);
    Long publishArticle(ArticlePublishRequest request, Long authorId);
    void updateArticle(Long articleId, ArticlePublishRequest request, Long userId);
    void deleteArticle(Long articleId, Long userId, boolean isAdmin);
    void updateStatus(Long articleId, Integer status);
    PageVO<ArticleVO> getPendingArticles(int page, int size);
    void approveArticle(Long articleId);
    void rejectArticle(Long articleId);
}