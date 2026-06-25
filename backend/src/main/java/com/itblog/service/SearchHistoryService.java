package com.itblog.service;

import com.itblog.vo.SearchHistoryVO;
import java.util.List;

public interface SearchHistoryService {
    void recordSearch(String keyword, Long userId);
    List<SearchHistoryVO> getUserHistory(Long userId);
    void clearHistory(Long userId);
    void deleteHistoryItem(Long id, Long userId);
}