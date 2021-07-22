import { datePickerShortcuts } from "./consts";

// TODO 看看是否有必要，用解构似乎更方便
// 从预定义的 datePickerShortcuts 常量对象中，过滤出指定的 keys 对应的值，并将这些值以数组的形式返回
export function getDataPickerShortcuts(keys, include) {
  let arr = []
  if(include) {
    keys.forEach(k => {
      arr.push(datePickerShortcuts[k])
    })
  } else {
    for (let [k, v] of Object.entries(datePickerShortcuts)) {
      if(keys.indexOf(k) === -1) {
        arr.push(v)
      }
    }
  }
  return arr
}