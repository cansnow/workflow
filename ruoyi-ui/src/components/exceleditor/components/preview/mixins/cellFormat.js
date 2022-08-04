import _ from 'lodash';
import { isNumber } from '../../../libs/formula';
// import { isNumber } from ''
export default {
  methods: {
    setFormatCell(pos, format) {
      this.cf_setFormatCell(pos, format);
    },
    cf_setFormatCell(pos, format) {
      let cell = this.getPosCell(pos);
      this.setCellAttribute(pos, cell, 'fc', format);
      if (typeof(cell) != 'undefined' && cell && typeof(cell['v']) != 'undefined') {
        if (typeof(cell['sv']) != 'undefined') {
          this.computedFormatCell(pos, cell.sv);
        } else {
          this.computedFormatCell(pos, cell.v);
        }
      }
    },
    // 计算格式
    computedFormatCell(pos, val) {
      let cell = this.getPosCell(pos);
      this.setCellAttribute(pos, cell, 'sv', val); // 原值

      this.setCellAttribute(pos, cell, 'fcv', val); // 防止死循环

      let temp = val;

      let d = 2;
      if (cell.fc == 'number' || cell.fc == 'currency') {
        if(typeof(cell.d) == 'undefined') {
          this.setCellAttribute(pos, cell, 'd', d);
        } else {
          d = cell.d;
        }
      }

      // 设置格式, 常规不做处理
      switch(cell.fc) {
        case 'number':
          temp = this.setNumber(val, d);
          break;
        case 'currency':
          temp = this.setFormatCurrency(val, d);
          break;
        case 'specialAccounting':
          temp = this.setFormatCurrency(val, d);
          break;
        case 'separators':
          temp = this.setSeparators(val, d);
          break;
        case 'percentage':
          temp = this.setPercentage(val, d);
          break;
        case 'shortDate':
          temp = this.setShortDate(val);
          break;
        case 'longDate':
          temp = this.setLongDate(val);
          break;
        case 'time':
          temp = this.setTime(val);
          break;
        case 'special': // 特殊
        case 'text': // 文本
        case 'custom': // 自定义
        case 'routine': // 常规
          break;
        case 'plus': // 正号
          temp = this.plus(val, d);
          break;
        case 'minus': // 负号
          temp = this.minus(val, d);
          break;
      }
      this.c_setCellV(pos, temp);
    },
    // 数值
    setNumber(val, d) {
      let temp = 1;
      let df = d;
      while(df > 0){
        temp *= 10;
        df--;
      }
      let v = val;
      if (isNumber(parseFloat(val))) {
        v =  Math.round(val*temp)/temp;
        let str = v + '';
        let index = str.indexOf('.');
        if (index != -1) {
          let len = str.length - index;
          // 补零
          if (len < d) {
            let zero = d - len;
            while(zero > 0){
              str += '0';
              zero--;
            }
          }
        } else {
          let zero = d;
          str += d != 0 ? '.' : '';
          while(zero > 0){
            str += '0';
            zero--;
          }
        }
        v = str;
      }
      return v;
    },
    // 设置小数位数
    setNumberDecimal(pos, type) {
      let cell = this.getPosCell(pos);
      let d = 2;
      if (typeof(cell) != 'undefined' && cell) {
        if(typeof(cell.d) != 'undefined') {
          d = cell.d;
        }
      }
      d = type == 'increase' ? d + 1 : d - 1;
      this.setCellAttribute(pos, cell, 'd', d < 0 ? 0 : d);
      if (typeof(cell.v) != 'undefined' || typeof(cell.sv) != 'undefined') {
        this.computedFormatCell(pos, cell.sv || cell.v);
      }
    },
    // 货币
    setFormatCurrency(val, d) {
      const temp = this.setSeparators(val, d);
      return '￥' + temp;
    },
    // 千位分隔
    setSeparators(val, d) {
      let temp = this.setNumber(val, d);
      const index = temp.indexOf('.');
      let s = temp.slice(0, index);
      let prefix = s.indexOf('-') != -1 ? s.slice(0, s.indexOf('-') + 1) : '';
      if (!!prefix) {
        s = s.replace(prefix, '');
      }
      let e = temp.slice(index, temp.length);
      let len = s.length;
      if (len > 3) {
        let num = len / 3 + '';
        if (num.indexOf('.') != -1) {
          num = parseInt(num);
        } else {
          num -= 1
        }
        const str = [];
        for (let i = 0; i < num; i++) {
          str.push(s.slice(-3));
          s = s.slice(0, -3);
        }
        str.push(s);
        str.reverse();
        temp = str.join(',') + e;
      }
      if (!!prefix) {
        temp = prefix + temp;
      }
      return temp;
    },
    // 百分比
    setPercentage(val, d) {
      const temp = this.setNumber(val, d);
      return temp + '%';
    },
    // 正号
    plus(val, d) {
      const temp = this.setNumber(val, d);
      return '+' + temp;
    },
    minus(val, d) {
      const temp = this.setNumber(val, d);
      return '-' + temp;
    },
    // 短日期 2022/02/02
    setShortDate(val) {
      const yI = val.indexOf('年');
      const mI = val.indexOf('月');
      const dI = val.indexOf('日');
      if (yI != -1 && mI != -1 && dI != -1) {
        const y = val.slice(0, yI);
        const m = val.slice(yI+1, mI);
        const d = val.slice(mI+1, dI);
        return y + '/' + m + '/' + d;
      } else {
        if (isNumber(parseFloat(val))) {
          let date = new Date(parseFloat(val));
          if (val.indexOf('/') != -1) {
            date = new Date(val);
          }
          const y = date.getFullYear();
          const m = date.getMonth() + 1;
          const d = date.getDate();
          return y + '/' + m + '/' + d;
        }
      }
      return val;
    },
    // 长日期 2022年02月12日
    setLongDate(val) {
      if (isNumber(parseFloat(val))) {
        let date = new Date(parseFloat(val));
        if (val.indexOf('/') != -1) {
          date = new Date(val);
        }
        const y = date.getFullYear();
        const m = date.getMonth() + 1;
        const d = date.getDate();
        return y + '年' + m + '月' + d + '日';
      }
      return val;
    },
    // 时间
    setTime(val) {
      if (isNumber(parseFloat(val))) {
        let date = new Date(parseFloat(val));
        if (val.indexOf('/') != -1) {
          date = new Date(val);
        }
        const y = date.getFullYear();
        const m = date.getMonth() + 1;
        const d = date.getDate();
        const h = date.getHours();
        let M = date.getMinutes();
        M = M < 10 ? '0' + M : M;
        let s = date.getSeconds();
        s = s < 10 ? '0' + s : s;
        return y + '/' + m + '/' + d + ' ' + h + ':' + M + ':' + s; 
      }
      return val;
    }
  },
}
