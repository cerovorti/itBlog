import request from './request'

export function login(username: string, password: string, rememberMe?: boolean) {
  return request.post('/auth/login', { username, password, rememberMe })
}

export function register(username: string, password: string, email: string) {
  return request.post('/auth/register', { username, password, email })
}
