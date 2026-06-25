<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-col">
        <h4>IT 技术博客</h4>
        <p>一个分享技术知识的社区平台<br/>记录成长，传递价值 ✨</p>
      </div>
      <div class="footer-col">
        <h4>技术栈</h4>
        <div class="tech-tags">
          <span>Spring Boot 3</span>
          <span>Vue 3</span>
          <span>TypeScript</span>
          <span>MySQL 8</span>
          <span>MyBatis-Plus</span>
          <span>Element Plus</span>
        </div>
      </div>
      <div class="footer-col">
        <h4>链接</h4>
        <router-link to="/">首页</router-link>
        <router-link to="/editor">写文章</router-link>
        <a href="https://github.com" target="_blank">GitHub</a>
      </div>
    </div>
    <div class="footer-bottom">
      <p>© 2026 IT技术博客 · 实训项目 · Made with ❤️</p>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  background: var(--card-bg);
  border-top: 1px solid var(--card-border);
  margin-top: 40px;
}

.footer-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 40px 20px 24px;
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr;
  gap: 32px;
}

.footer-col h4 {
  font-family: var(--font-heading);
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 12px;
  color: var(--text);
}
.footer-col p {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.8;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tech-tags span {
  padding: 3px 10px;
  font-size: 12px;
  color: var(--primary);
  background: rgba(255, 107, 107, 0.08);
  border-radius: 12px;
}

.footer-col a {
  display: block;
  font-size: 13px;
  color: var(--text-secondary);
  text-decoration: none;
  padding: 4px 0;
  transition: color var(--transition-fast);
}
.footer-col a:hover { color: var(--primary); }

.footer-bottom {
  text-align: center;
  padding: 16px 20px;
  border-top: 1px solid var(--card-border);
}
.footer-bottom p {
  font-size: 12px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .footer-inner { grid-template-columns: 1fr; }
}
</style>
