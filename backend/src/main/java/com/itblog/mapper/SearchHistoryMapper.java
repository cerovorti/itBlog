package com.itblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itblog.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}