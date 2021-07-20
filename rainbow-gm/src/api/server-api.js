import request from '../utils/http'
import { compact } from '../utils'

export function getConfigs(params) {
  return request({
    url: 'configs',
    method: 'GET',
    params: compact(params)
  })
}

export function save(user) {
  return request({
    url: 'configs',
    method: 'PATCH'
  })
}

