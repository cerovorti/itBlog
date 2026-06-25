package com.itblog.vo;

import java.util.List;

public class ArticleDetailVO {
    private ArticleVO article;
    private String content;
    private List<TocItem> toc;
    private Boolean liked;
    private Boolean favorited;
    private ArticleVO prevArticle;
    private ArticleVO nextArticle;

    public ArticleVO getArticle() { return article; } public void setArticle(ArticleVO v) { this.article = v; }
    public String getContent() { return content; } public void setContent(String v) { this.content = v; }
    public List<TocItem> getToc() { return toc; } public void setToc(List<TocItem> v) { this.toc = v; }
    public Boolean getLiked() { return liked; } public void setLiked(Boolean v) { this.liked = v; }
    public Boolean getFavorited() { return favorited; } public void setFavorited(Boolean v) { this.favorited = v; }
    public ArticleVO getPrevArticle() { return prevArticle; } public void setPrevArticle(ArticleVO v) { this.prevArticle = v; }
    public ArticleVO getNextArticle() { return nextArticle; } public void setNextArticle(ArticleVO v) { this.nextArticle = v; }

    public static class TocItem {
        private int level;
        private String title;
        private String anchor;
        private List<TocItem> children;

        public int getLevel() { return level; } public void setLevel(int v) { this.level = v; }
        public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
        public String getAnchor() { return anchor; } public void setAnchor(String v) { this.anchor = v; }
        public List<TocItem> getChildren() { return children; } public void setChildren(List<TocItem> v) { this.children = v; }
    }
}
