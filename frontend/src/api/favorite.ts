import request from './request'

export function toggleFavorite(articleId: number) {
  return request.post(`/favorite/toggle/${articleId}`)
}

export function getFavoriteStatus(articleId: number) {
  return request.get(`/favorite/status/${articleId}`)
}

export function getFavoriteList(page = 1, size = 10) {
  return request.get('/favorite/list', { params: { page, size } })
}
