import request from './request'

/** 上传文章内容图片（Markdown 编辑器使用） */
export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData)
}

/** 上传用户头像 */
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/avatar', formData)
}

/** 上传文章封面图 */
export function uploadCover(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/cover', formData)
}