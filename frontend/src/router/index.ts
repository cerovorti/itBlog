import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: () => import('../views/HomePage.vue') },
    { path: '/article/:id', name: 'article-detail', component: () => import('../views/ArticleDetail.vue') },
    { path: '/editor', name: 'editor-new', component: () => import('../views/ArticleEditor.vue'), meta: { requiresAuth: true } },
    { path: '/editor/:id', name: 'editor-edit', component: () => import('../views/ArticleEditor.vue'), meta: { requiresAuth: true } },
    { path: '/login', name: 'login', component: () => import('../views/LoginPage.vue') },
    { path: '/register', name: 'register', component: () => import('../views/RegisterPage.vue') },
    { path: '/profile', name: 'my-profile', component: () => import('../views/ProfilePage.vue'), meta: { requiresAuth: true } },
    { path: '/profile/:userId', name: 'profile', component: () => import('../views/ProfilePage.vue') },
    { path: '/search', name: 'search', component: () => import('../views/SearchPage.vue') },
    { path: '/admin', name: 'admin-dashboard', component: () => import('../views/admin/AdminDashboard.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/articles', name: 'admin-articles', component: () => import('../views/admin/AdminArticles.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/categories', name: 'admin-categories', component: () => import('../views/admin/AdminCategories.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/tags', name: 'admin-tags', component: () => import('../views/admin/AdminTags.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/users', name: 'admin-users', component: () => import('../views/admin/AdminUsers.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('../views/NotFound.vue') },
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()

  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login')
    return
  }

  // 需要管理员的页面：如果用户信息还没加载，先放行（App.vue 会异步加载）
  if (to.meta.requiresAdmin && !userStore.isAdmin()) {
    // 如果 token 存在但 user 信息还没加载，先放行等异步加载
    if (userStore.isLoggedIn() && !userStore.user) {
      next()
      return
    }
    next('/')
    return
  }

  next()
})

export default router
