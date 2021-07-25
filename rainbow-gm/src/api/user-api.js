import request from '../utils/http'
import qs from 'qs'
import { compact } from '../utils'

/**
 * TODO:
 *  - 获取用户列表：要传验证信息：withCredential: true
 *  - 编辑用户信息：PATCH /users/1 {active = 0/1, state = -1/0/1 }
 *  - 查看用户信息: GET /users/1
 *  - 从用户列表表格，可以导航到：他发布的房源，他发布的求租
 */
export function search(params) {
  return request({
    url: 'users',
    method: 'GET',
    params: compact(params),
    paramsSerializer: function (params) {
      return qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

// 保存单条记录
export function save(user) {
  return request({
    url: 'users',
    method: 'PATCH'
  })
}

/**
 * 批处理，如批量 [删除、更新、新增]
 *
 * @param action delete | update | add
 * @param data the data
 * @returns an {AxiosPromise} object
 */
export function batch(action, data) {
  const body = {}
  body[action] = data
  console.log('user-api: batch', body)
  return request({
    url: 'users/batch',
    method: 'POST',
    data: body
  })
}

export function resetPassword(id, newPwd) {
  const data = {
    update: [{ id, password: newPwd }]
  }
  return this.batch(data)
}
