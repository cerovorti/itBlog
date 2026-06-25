package com.itblog.dto;

import java.util.List;

public class ArticlePublishRequest {
    private String title;
    private String content;
    private String summary;
    private String coverImg;
    private Long categoryId;
    private List<Long> tagIds;
    private Integer status;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getCoverImg() { return coverImg; }
    public void setCoverImg(String coverImg) { this.coverImg = coverImg; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
