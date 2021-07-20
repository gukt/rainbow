import axios from 'axios'
import store from '../store'
import { Message, MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'
import Vue from 'vue'
import Router from 'vue-router'
import { requireNonNull } from './predications'
import { BASE_API } from '../../config/prod.env'

Vue.use(Router)

// 创建 axios 实例
const service = axios.create({
  // baseURL: process.env.BASE_API, // api的base_url
  baseURL: '/api',
  timeout: 60000
})

service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      // 让每个请求携带自定义 token 请根据实际情况自行修改
      config.headers['X-Token'] = getToken()
    }
    return config
  },
  Promise.reject
)

export function resolveParams(params) {
  if (!params) {
    return ''
  }
  const parts = []
  for (const k in params) {
    const v = params[k]
    if (Array.isArray(v)) {
      v.forEach(elem => {
        parts.push(k + '=' + elem)
      })
    } else {
      parts.push(k + '=' + v)
    }
  }
  return parts.join('&')
}

export function resolveUrl(url, params = {}) {
  requireNonNull(url, 'url')
  if (!url.startsWith('http')) {
    url = BASE_API + url
  }
  if (params) {
    if (url.indexOf('?') === -1) {
      url += '?' + resolveParams(params)
    } else {
      url += (url.endsWith('?') ? '' : '&') + resolveParams(params)
    }
  }
  return url
}

let requestId = 0
function _requestId() {
  return `[#${++requestId}]`
}

service.interceptors.response.use(
  res => {
    // 服务器返回的数据，格式为: {code:0, error:'', data:{}}，
    // 其中，code 为 0 时表示成功，此时，data 字段为接口真正的返回值；
    // 任何非零 code 都表示失败，错误描述信息在 error 字段中。
    const apiResult = res.data
    const config = res.config
    const reqId = _requestId()
    console.debug(reqId, config.method.toLocaleUpperCase() + ':', resolveUrl(config.url, config.params))
    console.debug(reqId, apiResult.data, apiResult)
    // 成功，返回接口响应值
    if (apiResult.code === 0) {
      return apiResult.data
    }
    // else {
    //   const errMsg = apiResult.error
    //   // 对于任何发生的错误，均弹出错误信息对话框提示
    //   Message({
    //     type: 'error',
    //     message: errMsg,
    //     duration: 5 * 1000
    //   })
    // }
    // 50008:非法的token; 50012:其他客户端登录了;  50014:Token 过期了;
    // if (payload.code === 50008 || payload.code === 50012 || payload.code ===
    //   50014) {
    if (apiResult.code === '-2') {
      MessageBox.confirm(
        '由于您长时间没有操作，您需要重新登录',
        '提示',
        {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        store.dispatch('FedLogOut').then(() => {
          location.reload() // 为了重新实例化vue-router对象 避免bug
        })
      })
    }
    return Promise.reject(apiResult)
  }, err => {
    // NOTE: 在状态码不为200时表示error
    let message = String(err)

    // 超时的情况下，error.response为undefined
    // 其他情况下会有response值（服务器返回的，根据不同的实现，消息格式会有不同）
    if (err.response) {
      message += ', Details: ' + JSON.stringify(err.response)
    }
    Message({ message, ype: 'error', duration: 5 * 1000 })
    return Promise.reject(err)
  }
)
export default service
