/**
 * Returns the {obj} itself if not null, otherwise throw an error.
 *
 * @param {object} obj the object to test, execept for number
 * @param {string} name the name of {obj}
 * @returns {object} the {obj} if not null
 */
import { format } from './index'

export function requireNonNull(obj, name) {
  if (!obj && isNaN(obj)) {
    throw new Error(name + ' is null (expected: non-null)')
  }
  return obj
}

/**
 * 检查指定的对象是否存在，如果不存在，则抛出 BadArgumentError。
 *
 * @param {*} expected 被检查的对象
 * @param {*} s 错误消息格式
 * @param  {...any} args 错误消息参数
 */
export function checkArgument(expected, s, ...args) {
  if (!expected) {
    if (!s) {
      throw new Error('BadArgumentError')
    } else {
      throw new Error('BadArgumentError: ' + format(s, args))
    }
  }
}

/**
 * 检查指定的对象是否为数组。如果不是则抛出异常，反之返回对象本身。
 *
 * @param {object} obj 被检查的对象
 * @param {string} name 被检查的对象名称
 * @returns 如果是数组，返回对象本身，反之抛出异常
 */
export function requireArray(obj, name) {
  if (!Array.isArray(obj)) {
    throw new Error(format('Array.isArray(%s): false (expected: true)', name))
  }
  return obj
}

/**
 * 检查指定的对象是否为非空。如果是空的，则抛出异常，反之返回对象本身。
 *
 * @param {object} obj 被检查的对象
 * @param {string} name 被检查的对象名称
 * @returns 如果是数组，返回对象本身，反之抛出异常
 */
export function requireNonEmpty(obj, name) {
  requireNonNull(obj, name)
  requireArray(obj, name)
  // TODO 添加对 set,map map-like, array-like 等支持和测试
  checkArgument(obj.length > 0, '%s.length: %d (expected: false)', name, obj.length)
  return obj
}

export function isMobile(mobile) {
  if (!mobile) return false
  const reg = /^1((3[0-9])|(4[1579])|(5[0-9])|(6[6])|(7[0-9])|(8[0-9])|(9[0-9]))\d{8}$/
  return reg.test(mobile)
}
