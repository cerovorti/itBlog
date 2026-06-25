package com.itblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itblog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
