<template>
  <div class="not-found">
    <div class="nf-card glass">
      <div class="nf-illustration"><span class="nf-emoji">🔭</span><div class="nf-orbits"><span class="orbit o1"></span><span class="orbit o2"></span><span class="orbit o3"></span></div></div>
      <h1>404</h1>
      <p>抱歉，这个页面走丢了</p>
      <p class="nf-sub">它可能已被删除，或者从未存在过</p>
      <router-link to="/" class="nf-btn">🏠 返回首页</router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found { display: flex; align-items: center; justify-content: center; min-height: 60vh; }
.nf-card { padding: 60px 50px; border-radius: var(--radius-xl); text-align: center; }
.nf-illustration { position: relative; display: inline-block; margin-bottom: 20px; }
.nf-emoji { font-size: 80px; position: relative; z-index: 2; }
.nf-orbits { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.orbit { position: absolute; border-radius: 50%; border: 2px dashed var(--card-border); animation: spin 4s linear infinite; }
.o1 { width: 120px; height: 120px; top: -60px; left: -60px; animation-duration: 3s; }
.o2 { width: 160px; height: 160px; top: -80px; left: -80px; animation-duration: 5s; animation-direction: reverse; }
.o3 { width: 200px; height: 200px; top: -100px; left: -100px; animation-duration: 7s; }
@keyframes spin { to { transform: rotate(360deg); } }
.nf-card h1 { font-size: 80px; font-weight: 900; background: var(--primary-gradient); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.nf-card p { font-size: 18px; color: var(--text-secondary); margin: 4px 0; }
.nf-sub { font-size: 14px !important; color: var(--text-muted) !important; margin-bottom: 28px !important; }
.nf-btn { display: inline-block; padding: 12px 32px; border-radius: var(--radius-md); background: var(--primary-gradient); color: #fff; text-decoration: none; font-weight: 600; font-size: 15px; transition: all var(--transition-normal); }
.nf-btn:hover { transform: translateY(-2px); box-shadow: var(--shadow-float); }
</style>
