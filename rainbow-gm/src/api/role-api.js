import request from '../utils/http'
import qs from 'qs'
import { compact } from '../utils'

export function search(params = {}) {
  // console.log('compacted params:', compact(params))
  return request({
    url: 'houses',
    method: 'GET',
    params: compact(params),
    paramsSerializer: function (params) {
      return qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function save(ids = [], partial = {}) {
  console.log('Saving house', ids, partial)
  // 也可以传入单个 id，此时先要转为数组
  if (!Array.isArray(ids)) {
    ids = [ids]
  }
  return request({
    url: 'houses',
    method: 'PATCH',
    params: { id: ids },
    data: partial,
    paramsSerializer: function (params) {
      return qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

