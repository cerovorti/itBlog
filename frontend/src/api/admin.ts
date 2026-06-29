import request from './request'

export function getAdminDashboard() {
  return request.get('/admin/dashboard')
}

export function getAdminArticles(params: Record<string, any>) {
  return request.get('/admin/articles', { params })
}

export function updateArticleStatus(id: number, status: number) {
  return request.put(`/admin/article/${id}/status`, { status })
}

export function deleteAdminComment(id: number) {
  return request.delete(`/admin/comment/${id}`)
}

export function addAdminCategory(name: string, parentId: number) {
  return request.post('/admin/category', { name, parentId })
}

export function deleteAdminCategory(id: number) {
  return request.delete(`/admin/category/${id}`)
}

export function deleteAdminTag(id: number) {
  return request.delete(`/admin/tag/${id}`)
}

export function getAdminUsers(page = 1, size = 50) {
  return request.get('/admin/users', { params: { page, size } })
}

export function setUserRecommend(userId: number, recommended: boolean) {
  return request.put(`/admin/user/${userId}/recommend`, { recommended })
}

export function banUser(userId: number, ban: boolean, hours?: number, permanent?: boolean) {
  return request.put(`/admin/user/${userId}/ban`, { ban, hours, permanent })
}

export function adminResetPassword(userId: number, newPassword: string) {
  return request.put(`/admin/user/${userId}/password`, { newPassword })
}
