package com.itblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itblog.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
