package com.itblog.vo;

import java.time.LocalDateTime;

public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private String skills;
    private Integer role;
    private Integer isRecommended;
    private Integer status;
    private LocalDateTime banUntil;
    private LocalDateTime createTime;
    private Long totalLikes;

    public UserVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public Integer getIsRecommended() { return isRecommended; }
    public void setIsRecommended(Integer isRecommended) { this.isRecommended = isRecommended; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getBanUntil() { return banUntil; }
    public void setBanUntil(LocalDateTime banUntil) { this.banUntil = banUntil; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Long getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Long totalLikes) { this.totalLikes = totalLikes; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final UserVO vo = new UserVO();
        public Builder id(Long id) { vo.id = id; return this; }
        public Builder username(String v) { vo.username = v; return this; }
        public Builder email(String v) { vo.email = v; return this; }
        public Builder avatar(String v) { vo.avatar = v; return this; }
        public Builder bio(String v) { vo.bio = v; return this; }
        public Builder skills(String v) { vo.skills = v; return this; }
        public Builder role(Integer v) { vo.role = v; return this; }
        public Builder isRecommended(Integer v) { vo.isRecommended = v; return this; }
        public Builder status(Integer v) { vo.status = v; return this; }
        public Builder banUntil(LocalDateTime v) { vo.banUntil = v; return this; }
        public Builder createTime(LocalDateTime v) { vo.createTime = v; return this; }
        public Builder totalLikes(Long v) { vo.totalLikes = v; return this; }
        public UserVO build() { return vo; }
    }
}
