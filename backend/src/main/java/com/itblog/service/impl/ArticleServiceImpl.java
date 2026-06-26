package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itblog.common.BusinessException;
import com.itblog.dto.ArticlePublishRequest;
import com.itblog.entity.*;
import com.itblog.mapper.*;
import com.itblog.service.ArticleService;
import com.itblog.vo.ArticleDetailVO;
import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LikeMapper likeMapper;
    private final FavoriteMapper favoriteMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper,
                              TagMapper tagMapper, CategoryMapper categoryMapper,
                              UserMapper userMapper, LikeMapper likeMapper,
                              FavoriteMapper favoriteMapper) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
        this.tagMapper = tagMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.likeMapper = likeMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public PageVO<ArticleVO> getArticleList(int page, int size, String sort,
                                             Long categoryId, Long tagId, boolean publishedOnly) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getIsDeleted, 0);
        if (publishedOnly) {
            wrapper.eq(Article::getStatus, 1);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if ("latest".equals(sort)) {
            wrapper.orderByDesc(Article::getCreateTime);
        } else {
            wrapper.orderByDesc(Article::getLikeCount);
            wrapper.orderByDesc(Article::getViewCount);
        }

        if (tagId != null) {
            LambdaQueryWrapper<ArticleTag> atWrapper = new LambdaQueryWrapper<>();
            atWrapper.eq(ArticleTag::getTagId, tagId);
            List<ArticleTag> atList = articleTagMapper.selectList(atWrapper);
            if (atList.isEmpty()) return new PageVO<>(new ArrayList<>(), 0, page, size);
            List<Long> articleIds = new ArrayList<>();
            for (ArticleTag at : atList) { articleIds.add(at.getArticleId()); }
            wrapper.in(Article::getId, articleIds);
        }

        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : articlePage.getRecords()) {
            voList.add(toArticleVO(article));
        }
        return new PageVO<>(voList, articlePage.getTotal(), page, size);
    }

    @Override
    public PageVO<ArticleVO> getMyArticles(int page, int size, Long authorId) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getIsDeleted, 0);
        wrapper.eq(Article::getAuthorId, authorId);
        wrapper.eq(Article::getStatus, 1);
        wrapper.orderByDesc(Article::getCreateTime);
        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : articlePage.getRecords()) {
            voList.add(toArticleVO(article));
        }
        return new PageVO<>(voList, articlePage.getTotal(), page, size);
    }

    @Override
    public PageVO<ArticleVO> getDrafts(int page, int size, Long authorId) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getIsDeleted, 0);
        wrapper.eq(Article::getAuthorId, authorId);
        wrapper.eq(Article::getStatus, 0);
        wrapper.orderByDesc(Article::getUpdateTime);
        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : articlePage.getRecords()) {
            voList.add(toArticleVO(article));
        }
        return new PageVO<>(voList, articlePage.getTotal(), page, size);
    }

    @Override
    public PageVO<ArticleVO> searchArticles(int page, int size, String keyword) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getIsDeleted, 0);
        wrapper.eq(Article::getStatus, 1);
        // 搜索标题
        wrapper.like(Article::getTitle, keyword);

        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : articlePage.getRecords()) {
            voList.add(toArticleVO(article));
        }

        // 也搜索标签匹配的文章
        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.like(Tag::getName, keyword);
        List<Tag> matchedTags = tagMapper.selectList(tagWrapper);
        if (!matchedTags.isEmpty()) {
            Set<Long> existingIds = new HashSet<>();
            for (ArticleVO vo : voList) { existingIds.add(vo.getId()); }
            List<Long> tagIds = new ArrayList<>();
            for (Tag t : matchedTags) { tagIds.add(t.getId()); }
            LambdaQueryWrapper<ArticleTag> atWrapper = new LambdaQueryWrapper<>();
            atWrapper.in(ArticleTag::getTagId, tagIds);
            List<ArticleTag> atList = articleTagMapper.selectList(atWrapper);
            for (ArticleTag at : atList) {
                if (!existingIds.contains(at.getArticleId())) {
                    Article article = articleMapper.selectById(at.getArticleId());
                    if (article != null && article.getIsDeleted() == 0 && article.getStatus() == 1) {
                        voList.add(toArticleVO(article));
                        existingIds.add(at.getArticleId());
                    }
                }
            }
        }

        return new PageVO<>(voList, articlePage.getTotal(), page, size);
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long articleId, Long currentUserId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException("Article not found");
        }
        if (article.getStatus() != 1
                && (currentUserId == null || !currentUserId.equals(article.getAuthorId()))) {
            throw new BusinessException("Article not found");
        }

        if (currentUserId == null || !currentUserId.equals(article.getAuthorId())) {
            LambdaUpdateWrapper<Article> uw = new LambdaUpdateWrapper<>();
            uw.eq(Article::getId, articleId);
            uw.setSql("view_count = view_count + 1");
            articleMapper.update(null, uw);
            article.setViewCount(article.getViewCount() + 1);
        }

        ArticleDetailVO vo = new ArticleDetailVO();
        vo.setArticle(toArticleVO(article));
        vo.setContent(article.getContent());
        vo.setToc(parseToc(article.getContent()));

        if (currentUserId != null) {
            LambdaQueryWrapper<Like> lw = new LambdaQueryWrapper<>();
            lw.eq(Like::getUserId, currentUserId);
            lw.eq(Like::getArticleId, articleId);
            vo.setLiked(likeMapper.selectCount(lw) > 0);

            LambdaQueryWrapper<Favorite> fw = new LambdaQueryWrapper<>();
            fw.eq(Favorite::getUserId, currentUserId);
            fw.eq(Favorite::getArticleId, articleId);
            vo.setFavorited(favoriteMapper.selectCount(fw) > 0);
        }

        // 上一篇：发布时间早于当前文章的最新一篇
        LambdaQueryWrapper<Article> prevWrapper = new LambdaQueryWrapper<>();
        prevWrapper.eq(Article::getIsDeleted, 0);
        prevWrapper.eq(Article::getStatus, 1);
        prevWrapper.lt(Article::getCreateTime, article.getCreateTime());
        prevWrapper.orderByDesc(Article::getCreateTime);
        prevWrapper.last("LIMIT 1");
        Article prevArticle = articleMapper.selectOne(prevWrapper);
        if (prevArticle != null) {
            vo.setPrevArticle(toArticleVO(prevArticle));
        }

        // 下一篇：发布时间晚于当前文章的最早一篇
        LambdaQueryWrapper<Article> nextWrapper = new LambdaQueryWrapper<>();
        nextWrapper.eq(Article::getIsDeleted, 0);
        nextWrapper.eq(Article::getStatus, 1);
        nextWrapper.gt(Article::getCreateTime, article.getCreateTime());
        nextWrapper.orderByAsc(Article::getCreateTime);
        nextWrapper.last("LIMIT 1");
        Article nextArticle = articleMapper.selectOne(nextWrapper);
        if (nextArticle != null) {
            vo.setNextArticle(toArticleVO(nextArticle));
        }

        return vo;
    }

    private void validateRequest(ArticlePublishRequest request, Integer status) {
        // 草稿不校验
        if (status != null && status == 0) return;
        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new BusinessException("标题不能为空");
        if (request.getContent() == null || request.getContent().isBlank())
            throw new BusinessException("内容不能为空");
        if (request.getSummary() == null || request.getSummary().isBlank())
            throw new BusinessException("摘要不能为空");
        if (request.getCoverImg() == null || request.getCoverImg().isBlank())
            throw new BusinessException("封面图不能为空");
        if (request.getCategoryId() == null)
            throw new BusinessException("分类不能为空");
        if (request.getTagIds() == null || request.getTagIds().isEmpty())
            throw new BusinessException("至少需要选择一个标签");
    }

    @Override
    @Transactional
    public Long publishArticle(ArticlePublishRequest request, Long authorId) {
        validateRequest(request, request.getStatus());
        Article article = new Article();
        article.setTitle(request.getTitle() == null || request.getTitle().isBlank() ? "无标题" : request.getTitle());
        article.setContent(cleanMarkdown(request.getContent()));
        article.setSummary(cleanMarkdown(request.getSummary()));
        article.setCoverImg(request.getCoverImg());
        article.setAuthorId(authorId);
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        articleMapper.insert(article);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }

        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long articleId, ArticlePublishRequest request, Long userId) {
        validateRequest(request, request.getStatus());
        Article article = articleMapper.selectById(articleId);
        if (article == null) throw new BusinessException("Article not found");
        if (!article.getAuthorId().equals(userId)) throw new BusinessException(403, "Permission denied");

        article.setTitle(request.getTitle() == null || request.getTitle().isBlank() ? "无标题" : request.getTitle());
        article.setContent(cleanMarkdown(request.getContent()));
        article.setSummary(cleanMarkdown(request.getSummary()));
        article.setCoverImg(request.getCoverImg());
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus() != null ? request.getStatus() : article.getStatus());
        // 草稿重新发布时刷新发布时间，使其排到"最新"前列
        if (article.getStatus() == 1 && (request.getStatus() == null || request.getStatus() == 1)) {
            // 若原为草稿重新发布，视为新发布，重置创建时间
            Article old = articleMapper.selectById(articleId);
            if (old != null && old.getStatus() == 0) {
                article.setCreateTime(LocalDateTime.now());
            }
        }
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.updateById(article);

        LambdaQueryWrapper<ArticleTag> atDel = new LambdaQueryWrapper<>();
        atDel.eq(ArticleTag::getArticleId, articleId);
        articleTagMapper.delete(atDel);
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
    }

    @Override
    public void deleteArticle(Long articleId, Long userId, boolean isAdmin) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) throw new BusinessException("Article not found");
        if (!isAdmin && !article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "Permission denied");
        }
        // 软删除
        article.setIsDeleted(1);
        articleMapper.updateById(article);
    }

    @Override
    public void updateStatus(Long articleId, Integer status) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) throw new BusinessException("Article not found");
        article.setStatus(status);
        articleMapper.updateById(article);
    }

    // --- helpers ---

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

    private String cleanMarkdown(String markdown) {
        if (markdown == null) return null;
        Safelist safe = Safelist.basic()
                .addTags("code", "pre", "h1", "h2", "h3", "h4", "h5", "h6",
                         "table", "thead", "tbody", "tr", "th", "td",
                         "img", "hr", "br", "del", "input")
                .addAttributes("img", "src", "alt", "title")
                .addAttributes("input", "type", "checked", "disabled")
                .addAttributes("code", "class")
                .addAttributes("pre", "class")
                .addAttributes("a", "href", "title", "target")
                .addProtocols("img", "src", "http", "https", "data")
                .addProtocols("a", "href", "http", "https");
        return Jsoup.clean(markdown, safe);
    }

    private List<ArticleDetailVO.TocItem> parseToc(String markdown) {
        List<ArticleDetailVO.TocItem> toc = new ArrayList<>();
        if (markdown == null) return toc;

        Stack<ArticleDetailVO.TocItem> stack = new Stack<>();

        String[] lines = markdown.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#")) {
                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') level++;
                if (level > 6) continue;
                String title = line.substring(level).trim();
                title = title.replaceAll("!\\[.*?\\]\\(.*?\\)", "");
                title = title.replaceAll("\\[.*?\\]\\(.*?\\)", "");
                title = title.replaceAll("`[^`]*`", "");
                title = title.replaceAll("<[^>]*>", "");
                title = title.replaceAll("[*_~]", "").trim();
                if (title.isEmpty()) continue;

                String anchor = title.toLowerCase()
                        .replaceAll("[^a-z0-9\\u4e00-\\u9fff]+", "-")
                        .replaceAll("^-|-$", "");

                ArticleDetailVO.TocItem item = new ArticleDetailVO.TocItem();
                item.setLevel(level);
                item.setTitle(title);
                item.setAnchor(anchor);
                item.setChildren(new ArrayList<>());

                while (!stack.isEmpty() && stack.peek().getLevel() >= level) {
                    stack.pop();
                }
                if (stack.isEmpty()) {
                    toc.add(item);
                } else {
                    stack.peek().getChildren().add(item);
                }
                stack.push(item);
            }
        }

        return toc;
    }
}
