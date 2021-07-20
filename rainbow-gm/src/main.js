import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n
import '@/styles/index.scss'
import App from './App'
import router from './router'
import store from './store'
import moment from 'moment'

import '@/icons' // icon
import '@/permission'
import Vant from 'vant'
import 'vant/lib/index.css'
import { extendsArray } from './utils'

Vue.use(ElementUI, { locale, size: 'small' })
Vue.use(Vant)

Vue.config.productionTip = false

// 为数组扩展原型方法
extendsArray()

Vue.filter('dateOnly', function (value) {
  if (value) {
    return moment(value).format('YYYY-MM-DD')
  }
  return ''
})

Vue.filter('defaultDate', function (value) {
  if (value) {
    return moment(value).format('YYYY-MM-DD HH:mm:ss')
  }
  return ''
})

Vue.filter('formatDate', function (value, pattern) {
  if (value) {
    return moment(value).format(pattern)
  }
  return ''
})

Vue.filter('ceil', function(num) {
  if (!num) return num
  return Math.ceil(num)
})
Vue.filter('toStateTag', function(state) {
  switch (state) {
    case -1:
      return '已删除'
    default:
      return '正常'
  }
})
Vue.filter('houseTitle', function(entity) {
  const type = entity.type === 0 ? '整租' : '合租'
  const { community, aspect, address, area = 0, price = 0 } = entity
  return `${type}.朝${aspect}.${community}-${address} (${area})m2-￥${price}/月`
})
Vue.filter('houseTitleInColumn', function(entity) {
  const type = entity.type === 0 ? '整租' : '合租'
  const { community = '', area = 0, price = 0, line = '', station = '' } = entity
  return `${type}.${community} (${area}m²-${price}/月) | ${line}-${station}`
})
Vue.filter('houseLayout', function(entity) {
  const { room = 0, livingRoom = 0, restRoom = 0, kitchen = 0, balcony = 0 } = entity
  return `${room} 室 ${livingRoom} 厅 ${kitchen} 厨 ${restRoom} 卫 ${balcony} 阳台`
})
Vue.filter('houseImage', function(filename) {
  return 'http://localhost:8080/upload/' + filename
})
Vue.filter('houseStation', function(entity) {
  const { line = '', station = '' } = entity
  return `${line}-${station}站`
})

new Vue({
  el: '#app',
  router,
  store,
  // TODO 这里是什么意思？
  render: h => h(App)
})
