package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itblog.common.BusinessException;
import com.itblog.dto.CommentRequest;
import com.itblog.entity.Article;
import com.itblog.entity.Comment;
import com.itblog.entity.User;
import com.itblog.mapper.ArticleMapper;
import com.itblog.mapper.CommentMapper;
import com.itblog.mapper.UserMapper;
import com.itblog.service.CommentService;
import com.itblog.vo.CommentVO;
import com.itblog.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;

    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper, ArticleMapper articleMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
    }

    /**
     * 清洗评论文本，去除所有 HTML 标签防 XSS 注入
     */
    private String cleanText(String text) {
        if (text == null) return null;
        return org.jsoup.Jsoup.clean(text, org.jsoup.safety.Safelist.none());
    }

    @Override
    public PageVO<CommentVO> getTopComments(int page, int size, Long articleId) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        wrapper.eq(Comment::getParentId, 0L);
        wrapper.eq(Comment::getIsDeleted, 0);
        wrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<CommentVO> voList = new ArrayList<>();
        Map<Long, User> userMap = new HashMap<>();
        for (Comment c : commentPage.getRecords()) {
            voList.add(toVO(c, userMap));
        }

        // 查询每条顶级评论的子孙回复总数（replyCount）
        for (CommentVO vo : voList) {
            vo.setReplyCount(countAllDescendants(vo.getId()));
            vo.setChildren(new ArrayList<>()); // 前端点击展开时通过 getReplies 加载
        }

        return new PageVO<>(voList, commentPage.getTotal(), page, size);
    }

    @Override
    public List<CommentVO> getReplies(Long parentId) {
        List<Comment> allDescendants = collectAllDescendants(parentId);
        Map<Long, User> userMap = new HashMap<>();
        List<CommentVO> voList = new ArrayList<>();
        for (Comment c : allDescendants) {
            voList.add(toVO(c, userMap));
        }
        return voList;
    }

    /**
     * 递归收集某 parentId 下的所有子孙评论（展平），按创建时间升序
     */
    private List<Comment> collectAllDescendants(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId);
        wrapper.eq(Comment::getIsDeleted, 0);
        wrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> direct = commentMapper.selectList(wrapper);
        List<Comment> result = new ArrayList<>(direct);
        for (Comment c : direct) {
            result.addAll(collectAllDescendants(c.getId()));
        }
        return result;
    }

    /**
     * 查询某评论的所有子孙回复总数
     */
    private int countAllDescendants(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId);
        wrapper.eq(Comment::getIsDeleted, 0);
        Long count = commentMapper.selectCount(wrapper);
        int total = count.intValue();
        // 递归统计子回复的子回复
        List<Comment> direct = commentMapper.selectList(wrapper);
        for (Comment c : direct) {
            total += countAllDescendants(c.getId());
        }
        return total;
    }

    @Override
    @Transactional
    public Long addComment(CommentRequest request, Long userId) {
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        comment.setReplyToUserId(request.getReplyToUserId() != null ? request.getReplyToUserId() : 0L);
        comment.setContent(cleanText(request.getContent()));
        commentMapper.insert(comment);

        LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
        uw.eq(Article::getId, request.getArticleId());
        uw.setSql("comment_count = comment_count + 1");
        articleMapper.update(null, uw);

        return comment.getId();
    }

    @Override
    public void deleteComment(Long commentId, Long userId, boolean isAdmin) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BusinessException("Comment not found");
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new BusinessException(403, "Permission denied");
        }
        // 软删除
        comment.setIsDeleted(1);
        commentMapper.updateById(comment);

        LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
        uw.eq(Article::getId, comment.getArticleId());
        uw.setSql("comment_count = GREATEST(comment_count - 1, 0)");
        articleMapper.update(null, uw);
    }

    private CommentVO toVO(Comment c, Map<Long, User> userMap) {
        User cu = userMap.computeIfAbsent(c.getUserId(),
                id -> userMapper.selectById(id));
        User ru = c.getReplyToUserId() > 0
                ? userMap.computeIfAbsent(c.getReplyToUserId(),
                        id -> userMapper.selectById(id))
                : null;

        return CommentVO.builder()
                .id(c.getId()).articleId(c.getArticleId())
                .userId(c.getUserId())
                .username(cu != null ? cu.getUsername() : "Unknown")
                .userAvatar(cu != null ? cu.getAvatar() : null)
                .parentId(c.getParentId())
                .replyToUserId(c.getReplyToUserId())
                .replyToUsername(ru != null ? ru.getUsername() : null)
                .content(c.getIsDeleted() == 1 ? "[该评论已被删除]" : c.getContent())
                .createTime(c.getCreateTime())
                .children(new ArrayList<>())
                .build();
    }
}