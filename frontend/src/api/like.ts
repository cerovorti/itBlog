import request from './request'

export function toggleLike(articleId: number) {
  return request.post(`/like/toggle/${articleId}`)
}
