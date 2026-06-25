package com.itblog.vo;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String coverImg;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public ArticleVO() {}

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public String getSummary() { return summary; } public void setSummary(String v) { summary = v; }
    public String getCoverImg() { return coverImg; } public void setCoverImg(String v) { coverImg = v; }
    public Long getAuthorId() { return authorId; } public void setAuthorId(Long v) { authorId = v; }
    public String getAuthorName() { return authorName; } public void setAuthorName(String v) { authorName = v; }
    public String getAuthorAvatar() { return authorAvatar; } public void setAuthorAvatar(String v) { authorAvatar = v; }
    public Long getCategoryId() { return categoryId; } public void setCategoryId(Long v) { categoryId = v; }
    public String getCategoryName() { return categoryName; } public void setCategoryName(String v) { categoryName = v; }
    public List<String> getTags() { return tags; } public void setTags(List<String> v) { tags = v; }
    public Integer getViewCount() { return viewCount; } public void setViewCount(Integer v) { viewCount = v; }
    public Integer getLikeCount() { return likeCount; } public void setLikeCount(Integer v) { likeCount = v; }
    public Integer getCommentCount() { return commentCount; } public void setCommentCount(Integer v) { commentCount = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { status = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { createTime = v; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime v) { updateTime = v; }
}
