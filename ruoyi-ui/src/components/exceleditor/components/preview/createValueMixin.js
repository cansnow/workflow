/*
 * @Author: yangzekun
 * @Date: 2022-12-28 20:36:13
 * @LastEditors: yangzekun
 * @LastEditTime: 2022-12-28 21:37:26
 * @Description: 默认值
 */
export default {
  methods: {
    // 生成日期
    dateFormat(fmt, date) {
      let ret
      const opt = {
          'Y+': date.getFullYear().toString(), // 年
          'm+': (date.getMonth() + 1).toString(), // 月
          'd+': date.getDate().toString(), // 日
          // 'H+': date.getHours().toString(), // 时
          // 'M+': date.getMinutes().toString(), // 分
          // 'S+': date.getSeconds().toString(), // 秒
          // 有其他格式化字符需求可以继续添加，必须转化成字符串
      }
      for (let k in opt) {
          ret = new RegExp('(' + k + ')').exec(fmt)
          if (ret) {
              fmt = fmt.replace(
                  ret[1],
                  ret[1].length == 1 ? opt[k] : opt[k].padStart(ret[1].length, '0')
              )
          }
      }
      return fmt
    },
    // 递增
    getCount(len, digit) {
      let lenStr = len + '';
      let str = '';
      if (lenStr.length < digit) {
        for (let i = lenStr.length; i < digit; i++) {
          str += '0';
        }
      }
      return str + (len + 1);
    }
  },
}