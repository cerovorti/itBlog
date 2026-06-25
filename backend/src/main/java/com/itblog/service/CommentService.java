package com.itblog.service;

import com.itblog.dto.CommentRequest;
import com.itblog.vo.CommentVO;
import com.itblog.vo.PageVO;

import java.util.List;

public interface CommentService {
    PageVO<CommentVO> getTopComments(int page, int size, Long articleId);
    List<CommentVO> getReplies(Long parentId);
    Long addComment(CommentRequest request, Long userId);
    void deleteComment(Long commentId, Long userId, boolean isAdmin);
}
