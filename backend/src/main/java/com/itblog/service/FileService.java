package com.itblog.service;

import com.itblog.entity.FileRecord;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileRecord uploadImage(MultipartFile file, Long uploaderId);
    FileRecord getFile(Long fileId);
}
