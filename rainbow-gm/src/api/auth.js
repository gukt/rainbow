import request from '../utils/http'

export function login(username, password) {
  return request({
    url: '/gm/login',
    method: 'get',
    params: { username, password }
  })
}

export function getInfoByToken(token) {
  return request({
    url: '/gm/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/gm/logout',
    method: 'post'
  })
}
