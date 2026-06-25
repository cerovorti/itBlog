package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.Tag;
import com.itblog.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list")
    public Result<List<Tag>> list(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return Result.ok(tagService.getTagsByCategory(categoryId));
        }
        return Result.ok(tagService.getTagList());
    }

    @GetMapping("/cloud")
    public Result<List<Map<String, Object>>> cloud() {
        return Result.ok(tagService.getTagCloud(20));
    }

    @PostMapping
    public Result<Tag> create(@RequestBody Map<String, String> body) {
        return Result.ok(tagService.createTag(body.get("name")));
    }
}
