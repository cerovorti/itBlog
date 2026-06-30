package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.Media;
import com.itblog.entity.User;
import com.itblog.service.FileService;
import com.itblog.service.FileService.MediaWithData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 上传控制器 —— 图片上传到 Lsky Pro 图床，支持三类图片：
 * 1. 文章内容图片（Markdown 编辑器）
 * 2. 用户头像
 * 3. 文章封面图
 */
@RestController
@RequestMapping("/api")
public class UploadController {

    private final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传文章内容图片（Markdown 编辑器使用），返回 Lsky 直链
     */
    @PostMapping("/upload/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file,
                                                    HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        Long uploaderId = user != null ? user.getId() : null;
        Media media = fileService.uploadImage(file, uploaderId);
        return Result.ok(Map.of(
                "url", media.getFileUrl(),
                "id", media.getId(),
                "filename", media.getOriginalName(),
                "thumbnailUrl", media.getThumbnailUrl() != null ? media.getThumbnailUrl() : ""
        ));
    }

    /**
     * 上传用户头像，返回 Lsky 直链
     */
    @PostMapping("/upload/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                     HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        Media media = fileService.uploadAvatar(file, user.getId());
        return Result.ok(Map.of(
                "url", media.getFileUrl(),
                "id", media.getId(),
                "thumbnailUrl", media.getThumbnailUrl() != null ? media.getThumbnailUrl() : ""
        ));
    }

    /**
     * 上传文章封面图，返回 Lsky 直链
     */
    @PostMapping("/upload/cover")
    public Result<Map<String, Object>> uploadCover(@RequestParam("file") MultipartFile file,
                                                    HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        Media media = fileService.uploadCover(file, user.getId());
        return Result.ok(Map.of(
                "url", media.getFileUrl(),
                "id", media.getId(),
                "thumbnailUrl", media.getThumbnailUrl() != null ? media.getThumbnailUrl() : ""
        ));
    }

    /**
     * 通过 mediaId 返回文件字节流（兼容旧接口 /api/file/image/{id}）
     */
    @GetMapping("/file/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        FileService.MediaWithData mwd = fileService.getFileData(id);
        MediaType mediaType = MediaType.parseMediaType(mwd.getContentType());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(mwd.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(mwd.getOriginalName()).build().toString())
                .body(mwd.getData());
    }

    /**
     * 重定向到 Lsky 直链，快速访问不经过后端
     */
    @GetMapping("/file/redirect/{id}")
    public ResponseEntity<Void> redirectToLsky(@PathVariable Long id) {
        String url = fileService.getFileUrl(id);
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }
}