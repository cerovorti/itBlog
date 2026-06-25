package com.itblog.service;

import com.itblog.entity.Tag;

import java.util.List;
import java.util.Map;

public interface TagService {
    List<Tag> getTagList();
    List<Tag> getTagsByCategory(Long categoryId);
    Tag createTag(String name);
    void deleteTag(Long id);
    List<Map<String, Object>> getTagCloud(int limit);
}
