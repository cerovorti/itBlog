import request from './request'

export function recordSearchHistory(keyword: string) {
  return request.post('/search/history', {}, { params: { keyword } })
}

export function getSearchHistory() {
  return request.get('/search/history')
}

export function clearSearchHistory() {
  return request.delete('/search/history')
}

export function deleteSearchHistoryItem(id: number) {
  return request.delete(`/search/history/${id}`)
}