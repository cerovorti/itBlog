package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itblog.entity.SearchHistory;
import com.itblog.mapper.SearchHistoryMapper;
import com.itblog.service.SearchHistoryService;
import com.itblog.vo.SearchHistoryVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private final SearchHistoryMapper searchHistoryMapper;

    public SearchHistoryServiceImpl(SearchHistoryMapper searchHistoryMapper) {
        this.searchHistoryMapper = searchHistoryMapper;
    }

    @Override
    public void recordSearch(String keyword, Long userId) {
        if (keyword == null || keyword.trim().isEmpty()) return;

        // 检查是否已存在相同关键词的记录，若存在则仅更新时间
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        wrapper.eq(SearchHistory::getKeyword, keyword.trim());
        SearchHistory existing = searchHistoryMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setCreateTime(LocalDateTime.now());
            searchHistoryMapper.updateById(existing);
        } else {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setKeyword(keyword.trim());
            history.setCreateTime(LocalDateTime.now());
            searchHistoryMapper.insert(history);
        }
    }

    @Override
    public List<SearchHistoryVO> getUserHistory(Long userId) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        wrapper.orderByDesc(SearchHistory::getCreateTime);
        wrapper.last("LIMIT 10");
        List<SearchHistory> list = searchHistoryMapper.selectList(wrapper);
        return list.stream()
                .map(h -> new SearchHistoryVO(h.getId(), h.getKeyword()))
                .collect(Collectors.toList());
    }

    @Override
    public void clearHistory(Long userId) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        searchHistoryMapper.delete(wrapper);
    }

    @Override
    public void deleteHistoryItem(Long id, Long userId) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getId, id);
        wrapper.eq(SearchHistory::getUserId, userId);
        searchHistoryMapper.delete(wrapper);
    }
}