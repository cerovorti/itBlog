package com.itblog.vo;

import java.time.LocalDateTime;
import java.util.List;

public class CommentVO {
    private Long id;
    private Long articleId;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private String content;
    private LocalDateTime createTime;
    private List<CommentVO> children;

    public CommentVO() {}

    // Getters and Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getArticleId() { return articleId; } public void setArticleId(Long v) { this.articleId = v; }
    public Long getUserId() { return userId; } public void setUserId(Long v) { this.userId = v; }
    public String getUsername() { return username; } public void setUsername(String v) { this.username = v; }
    public String getUserAvatar() { return userAvatar; } public void setUserAvatar(String v) { this.userAvatar = v; }
    public Long getParentId() { return parentId; } public void setParentId(Long v) { this.parentId = v; }
    public Long getReplyToUserId() { return replyToUserId; } public void setReplyToUserId(Long v) { this.replyToUserId = v; }
    public String getReplyToUsername() { return replyToUsername; } public void setReplyToUsername(String v) { this.replyToUsername = v; }
    public String getContent() { return content; } public void setContent(String v) { this.content = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { this.createTime = v; }
    public List<CommentVO> getChildren() { return children; } public void setChildren(List<CommentVO> v) { this.children = v; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final CommentVO vo = new CommentVO();
        public Builder id(Long v) { vo.id = v; return this; }
        public Builder articleId(Long v) { vo.articleId = v; return this; }
        public Builder userId(Long v) { vo.userId = v; return this; }
        public Builder username(String v) { vo.username = v; return this; }
        public Builder userAvatar(String v) { vo.userAvatar = v; return this; }
        public Builder parentId(Long v) { vo.parentId = v; return this; }
        public Builder replyToUserId(Long v) { vo.replyToUserId = v; return this; }
        public Builder replyToUsername(String v) { vo.replyToUsername = v; return this; }
        public Builder content(String v) { vo.content = v; return this; }
        public Builder createTime(LocalDateTime v) { vo.createTime = v; return this; }
        public Builder children(List<CommentVO> v) { vo.children = v; return this; }
        public CommentVO build() { return vo; }
    }
}
