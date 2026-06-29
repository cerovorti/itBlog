<template>
  <div class="admin-layout">
    <nav class="admin-side glass">
      <div class="admin-logo">⚙️ 管理后台</div>
      <router-link to="/admin" exact-active-class="active" class="admin-nav-item">📊 仪表盘</router-link>
      <router-link to="/admin/articles" active-class="active" class="admin-nav-item">📝 文章审核</router-link>
      <router-link to="/admin/categories" active-class="active" class="admin-nav-item">📁 分类管理</router-link>
      <router-link to="/admin/tags" active-class="active" class="admin-nav-item">🏷️ 标签管理</router-link>
      <router-link to="/admin/users" active-class="active" class="admin-nav-item">👥 用户管理</router-link>
      <div class="admin-nav-div"></div>
      <router-link to="/" class="admin-nav-item back">← 返回前台</router-link>
    </nav>
    <div class="admin-main">
      <h2 class="page-title">📁 分类管理</h2>
      <div class="add-row"><el-input v-model="newName" placeholder="新分类名称" size="default" style="width:200px" /><el-button type="primary" @click="addCat" style="margin-left:8px">添加</el-button></div>
      <el-table :data="allCats" style="margin-top:16px" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column label="层级" width="80"><template #default="{ row }">{{ row.parentId === 0 ? '一级' : '二级' }}</template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="{ row }"><el-button size="small" type="danger" @click="delCat(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'; import { ElMessage } from 'element-plus'
import { getCategoryList, type Category } from '../../api/category'
import { addAdminCategory, deleteAdminCategory } from '../../api/admin'

const allCats = ref<Category[]>([]); const loading = ref(true)
const newName = ref('')

async function fetch() { loading.value = true; try { allCats.value = (await getCategoryList()).data || [] } finally { loading.value = false } }
async function addCat() { if (!newName.value.trim()) return; try { await addAdminCategory(newName.value, 0); newName.value = ''; ElMessage.success('添加成功'); fetch() } catch {} }
async function delCat(id: number) { try { await deleteAdminCategory(id); ElMessage.success('已删除'); fetch() } catch {} }
onMounted(fetch)
</script>

<style scoped>
.admin-layout { display: flex; gap: 24px; max-width: 1200px; margin: 0 auto; }
.admin-side { width: 220px; flex-shrink: 0; padding: 20px; display: flex; flex-direction: column; gap: 4px; border-radius: var(--radius-lg); position: sticky; top: 80px; align-self: flex-start; height: fit-content; }
.admin-logo { font-weight: 800; font-size: 16px; padding: 0 12px 16px; color: var(--text); }
.admin-nav-item { padding: 10px 14px; border-radius: var(--radius-sm); font-size: 14px; color: var(--text-secondary); text-decoration: none; transition: all var(--transition-fast); }
.admin-nav-item:hover { background: var(--primary-bg); color: var(--primary); }
.admin-nav-item.active { background: var(--primary-gradient); color: #fff; font-weight: 600; }
.admin-nav-item.back { margin-top: 8px; font-size: 13px; opacity: 0.7; }
.admin-nav-div { height: 1px; background: var(--card-border); margin: 8px 0; }
.admin-main { flex: 1; min-width: 0; }
.page-title { font-size: 22px; font-weight: 800; margin-bottom: 20px; color: var(--text); }
.add-row { display: flex; align-items: center; }
@media (max-width: 768px) { .admin-layout { flex-direction: column; } .admin-side { width: 100%; position: static; flex-direction: row; flex-wrap: wrap; gap: 6px; } }
</style>
