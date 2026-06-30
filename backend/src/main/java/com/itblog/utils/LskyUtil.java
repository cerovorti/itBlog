package com.itblog.utils;

import com.itblog.config.LskyConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * Lsky Pro 图床工具类
 * 启动时自动获取 Token，上传/删除图片，Token 过期自动刷新
 */
@Slf4j
@Component
public class LskyUtil {

    private final RestTemplate restTemplate;
    private final LskyConfig lskyConfig;

    private String token;
    private String baseUrl;

    public LskyUtil(RestTemplate restTemplate, LskyConfig lskyConfig) {
        this.restTemplate = restTemplate;
        this.lskyConfig = lskyConfig;
    }

    @PostConstruct
    public void init() {
        this.baseUrl = lskyConfig.getBaseUrl();
        acquireToken();
    }

    // ==================== Token 管理 ====================

    /** 获取/刷新 Token */
    private synchronized void acquireToken() {
        try {
            String url = baseUrl + "/tokens";
            Map<String, String> body = Map.of(
                    "email", lskyConfig.getEmail(),
                    "password", lskyConfig.getPassword()
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> resp = response.getBody();
            if (resp != null && Boolean.TRUE.equals(resp.get("status"))) {
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                this.token = (String) data.get("token");
                log.info("Lsky Token 获取成功");
            } else {
                log.error("Lsky Token 获取失败: {}", resp);
            }
        } catch (Exception e) {
            log.error("Lsky Token 获取异常: {}", e.getMessage());
        }
    }

    /** 获取 Bearer Token（自动处理过期），返回 "Bearer xxx" */
    private String getAuthHeader() {
        if (token == null) acquireToken();
        return "Bearer " + token;
    }

    // ==================== 图片操作 ====================

    /**
     * 上传图片（字节数组形式）
     * @param fileBytes 文件字节
     * @param filename 文件名
     * @return LskyUploadResult
     */
    public LskyUploadResult upload(byte[] fileBytes, String filename) {
        if (token == null) acquireToken();
        if (token == null) throw new RuntimeException("Lsky Token 未就绪");

        String url = baseUrl + "/upload";

        try {
            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() { return filename; }
            };

            // 尝试带 album_id 上传（部分 Lsky 版本支持）
            // 先查相册 ID，如果查不到则不带 album_id
            Integer albumId = findAlbumId(lskyConfig.getAlbumName());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            if (albumId != null) body.add("album_id", albumId.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", getAuthHeader());
            headers.set("Accept", "application/json");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> resp = response.getBody();

            if (resp == null || !Boolean.TRUE.equals(resp.get("status"))) {
                // 可能 Token 过期，刷新后重试一次
                acquireToken();
                headers.set("Authorization", getAuthHeader());
                request = new HttpEntity<>(body, headers);
                response = restTemplate.postForEntity(url, request, Map.class);
                resp = response.getBody();
            }

            if (resp != null && Boolean.TRUE.equals(resp.get("status"))) {
                return parseUploadResult((Map<String, Object>) resp.get("data"));
            } else {
                throw new RuntimeException("Lsky 上传失败: " + resp);
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            acquireToken();
            // 重试
            return upload(fileBytes, filename);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // 限流：等 2 秒后重试
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            return upload(fileBytes, filename);
        }
    }

    /**
     * 删除图片
     */
    public boolean delete(String lskyKey) {
        if (token == null) acquireToken();
        if (token == null) return false;

        String url = baseUrl + "/images/" + lskyKey;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getAuthHeader());
            headers.set("Accept", "application/json");
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Map.class);
            return true;
        } catch (Exception e) {
            log.warn("Lsky 删除图片失败 key={}: {}", lskyKey, e.getMessage());
            return false;
        }
    }

    /**
     * 查找相册 ID（按名称）
     */
    private Integer findAlbumId(String albumName) {
        if (albumName == null || albumName.isBlank()) return null;
        try {
            String url = baseUrl + "/albums?keyword=" + albumName;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getAuthHeader());
            headers.set("Accept", "application/json");
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map<String, Object> resp = response.getBody();
            if (resp != null && Boolean.TRUE.equals(resp.get("status"))) {
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                java.util.List<Map<String, Object>> albums = (java.util.List<Map<String, Object>>) data.get("data");
                if (albums != null) {
                    for (Map<String, Object> album : albums) {
                        if (albumName.equals(album.get("name"))) {
                            return ((Number) album.get("id")).intValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("查询 Lsky 相册失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析上传返回结果
     */
    private LskyUploadResult parseUploadResult(Map<String, Object> data) {
        LskyUploadResult r = new LskyUploadResult();
        r.key = (String) data.get("key");
        r.name = (String) data.get("name");
        r.originName = (String) data.get("origin_name");
        r.size = data.get("size") != null ? ((Number) data.get("size")).doubleValue() : 0;
        r.mimeType = (String) data.get("mimetype");
        r.extension = (String) data.get("extension");
        r.width = data.get("width") != null ? ((Number) data.get("width")).intValue() : null;
        r.height = data.get("height") != null ? ((Number) data.get("height")).intValue() : null;

        Map<String, Object> links = (Map<String, Object>) data.get("links");
        if (links != null) {
            r.url = (String) links.get("url");
            r.thumbnailUrl = (String) links.get("thumbnail_url");
            r.markdown = (String) links.get("markdown");
        }

        return r;
    }

    // ==================== 内部类 ====================

    /** Lsky 上传返回结果 */
    public static class LskyUploadResult {
        public String key;
        public String name;
        public String originName;
        public double size;
        public String mimeType;
        public String extension;
        public Integer width;
        public Integer height;
        public String url;
        public String thumbnailUrl;
        public String markdown;
    }
}