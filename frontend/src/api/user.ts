import request from './request'

export interface UserVO {
  id: number
  username: string
  email: string
  avatar: string
  bio: string
  skills?: string
  role: number
  isRecommended: number
  status: number
  banUntil: string | null
  createTime: string
  totalLikes?: number
}

export function getUserProfile(userId: number) {
  return request.get(`/user/profile/${userId}`)
}

export function getMyProfile() {
  return request.get('/user/profile')
}

export function updateProfile(data: { avatar?: string; bio?: string; skills?: string }) {
  return request.put('/user/profile', data)
}

export function getRecommendedAuthors() {
  return request.get('/user/recommended-authors')
}

export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return request.put('/user/password', data)
}

export function adminResetPassword(userId: number, data: { newPassword: string }) {
  return request.put(`/admin/user/${userId}/password`, data)
}
