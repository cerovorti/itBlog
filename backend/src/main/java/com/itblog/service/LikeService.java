package com.itblog.service;

import java.util.Map;

public interface LikeService {
    Map<String, Object> toggleLike(Long articleId, Long userId);
}
