/**
 * 为 Array 扩展原型方法
 */
export function extendsArray() {
  /**
   * 清除数组中的所有元素
   */
  // eslint-disable-next-line no-extend-native
  Array.prototype.clear = function () {
    this.splice(0, this.length)
  }

  /**
   * 判断数据是否为空
   */
  // eslint-disable-next-line no-extend-native
  Array.prototype.isEmpty = function () {
    return this.length === 0
  }

  /**
   * 判断数据是否不为空
   */
  // eslint-disable-next-line no-extend-native
  Array.prototype.isNotEmpty = function () {
    return this.length !== 0
  }

  /**
   * 从数组中移除指定的元素
   */
  // eslint-disable-next-line no-extend-native
  Array.prototype.remove = function (b) {
    const a = this.indexOf(b)
    if (a >= 0) {
      this.splice(a, 1)
      return true
    }
    return false
  }

  /**
   * 判断数组中是否包含指定的元素
   */
  // eslint-disable-next-line no-extend-native
  Array.prototype.contains = function (item) {
    return this.indexOf(item) !== -1
  }
}

export function compact(obj = {}) {
  for (const prop in obj) {
    if (obj[prop] === null) {
      delete obj[prop]
    }
  }
  return obj
}

/**
 * 格式化字符串，使用方法：
 * 1. format("api/values/{id}/{name}",{id:101,name:"test"})
 * 2. format("api/values/{0}/{1}",101,"test")
 *
 * @param {string} s 字符串模板
 * @param {object | any[]} args 数据
 */
export function format(s, args) {
  if (!s || args === undefined) {
    return s
  }
  if (typeof args === 'object') {
    for (const key in args) {
      if (args.hasOwnProperty(key)) {
        s = s.replace(new RegExp('\{' + key + '\}', 'g'), args[key])
      }
    }
  } else {
    args = arguments
    const reg = new RegExp('\{([0-' + (args.length - 1) + '])\}', 'g')
    return s.replace(reg, function (match, index) {
      return args[index - (-1)]
    })
  }
  return s
}

export function parseTime(time, cFormat) {
  if (arguments.length === 0) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if (('' + time).length === 10) time = parseInt(time) * 1000
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    if (key === 'a') {
      return ['一', '二', '三', '四', '五', '六', '日'][value - 1]
    }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

export function formatTime(time, option) {
  time = +time * 1000
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      d.getMonth() +
      1 +
      '月' +
      d.getDate() +
      '日' +
      d.getHours() +
      '时' +
      d.getMinutes() +
      '分'
    )
  }
}
