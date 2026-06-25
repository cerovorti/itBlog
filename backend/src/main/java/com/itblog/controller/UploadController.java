package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.FileRecord;
import com.itblog.entity.User;
import com.itblog.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final FileService fileService;
    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file,
                                                    HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        Long uploaderId = user != null ? user.getId() : null;
        FileRecord record = fileService.uploadImage(file, uploaderId);
        String url = "/api/file/image/" + record.getId();
        return Result.ok(Map.of("url", url, "id", record.getId(), "filename", record.getFilename()));
    }

    @GetMapping("/file/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        FileRecord record = fileService.getFile(id);
        MediaType mediaType = MediaType.parseMediaType(record.getContentType());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(record.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(record.getOriginalName()).build().toString())
                .body(record.getFileData());
    }
}
