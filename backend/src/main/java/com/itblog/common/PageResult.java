package com.itblog.common;

import com.itblog.vo.PageVO;

/**
 * 统一分页响应结果
 */
public class PageResult {

    public static <T> Result<PageVO<T>> ok(PageVO<T> page) {
        return new Result<>(200, "success", page);
    }
}
