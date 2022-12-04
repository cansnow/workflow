/*
 * @Author: yangzekun
 * @Date: 2022-12-04 21:05:26
 * @LastEditors: yangzekun
 * @LastEditTime: 2022-12-05 00:14:19
 * @Description: 数据校验
 */
import _ from 'lodash';
import { validPhone, validIDCard, validEmail } from '@/utils/validate';

const options = {
  none: '无',
  int: '整数',
  float: '小数',
  datetime: '日期',
  length: '文本长度',
  phone: '手机号',
  emil: '邮箱',
  IDCard: '身份证号',
  custom: '自定义正则表达式',
};
const roles = ['none','int','float','datetime','length','phone','emil','IDCard','custom'];
const optionsJudge = {
  'and': '介于',
  'not': '未介于',
  '==': '等于',
  '!=': '不等于',
  '>': '大于',
  '<': '小于',
  '>=': '大于等于',
  '<=': '小于等于',
};
const judges = [ 'and', 'not', '==', '!=', '>', '<', '>=', '<='];

export default {
  data() {
    return {
      roleList: [],
      roleTitle: '',
      roleContent: '',
      roleErrorShow: false,
    }
  },
  methods: {
    validPhone,
    validIDCard,
    validEmail,
    // Judge:"and"
    // condition:"int"
    // content:"我是标题内容"
    // disabled:false
    // end:"2"
    // index:-1
    // pos:"A1"
    // start:"1"
    // title:"我是标题"

    roles() {
      const cells = this.$curSheet().cells;
      const roleList = this.roleList;
      for(let i = 0; i < roleList.length; i++) {
        const item = roleList[i];
        const pos = this.formatData(item.pos);
        const tempCell = cells[pos.rowIndex][pos.columnIndex];
        if(tempCell) {
          switch(item.condition) {
            case 'int':
              break;
            case 'float':
              break;
            case 'datetime':
              break;
            case 'length':
              break;
            case 'phone':
              if (!this.validPhone(tempCell.v)) {
                this.roleTitle = item.title;
                this.roleContent = item.content;
                this.roleErrorShow = true;
                return false;
              }
              break;
            case 'emil':
              if (!validEmail(tempCell.v)) {
                this.roleTitle = item.title;
                this.roleContent = item.content;
                this.roleErrorShow = true;
                return false;
              }
              break;
            case 'IDCard':
              if (!IDCard(tempCell.v)) {
                this.roleTitle = item.title;
                this.roleContent = item.content;
                this.roleErrorShow = true;
                return false;
              }
              break;
            case 'custom':
              if (item.start && !new RegExp(item.start).test(tempCell.v)) {
                this.roleTitle = item.title;
                this.roleContent = item.content;
                this.roleErrorShow = true;
                return false;
              }
              break;
          }
        }
      }
      return true;
      _.each(this.$curSheet().cells, (cell, key) => {
        _.each(cell, (col, index) => {
          if (
            !!col &&
            typeof col.c != 'undefined' &&
            col.c != 'Cell' &&
            col.c != 'image' &&
            col.c != 'button'
          ) {
            if (col.p.r.w && col.p.r.s) {
              const exPos = { columnIndex: index, rowIndex: key };
              const exSelect = { end: exPos, start: exPos };
              let ifOkTemp = true;
              // 必填校验
              if (col.p.vd.r) {
                if (!col.v) {
                  console.log('pos', key, index);
                  ifOkTemp = false;
                }
              }
              if (!!col.p.vd.ex) {
                switch(col.p.vd.ex) {
                  case 'phone':
                    // 手机号码校验
                    if (!!col.v) {
                      if (!validPhone(col.v)) {
                        // 校验不通过
                        ifOkTemp = false;
                      }
                    }
                    break;
                  case 'idCard':
                    // 身份证校验
                    if (!!col.v) {
                      if (!validIDCard(col.v)) {
                        // 校验不通过
                        ifOkTemp = false;
                      }
                    }
                    break;
                  case 'email':
                    // 邮箱校验
                    if (!!col.v) {
                      if (!validEmail(col.v)) {
                        // 校验不通过
                        ifOkTemp = false;
                      }
                    }
                    break;
                  case 'number':
                    // 数值校验
                    break;
                  case 'length':
                    // 文本长度校验
                    break;
                  default:
                    break;
                }
              }
              if (!ifOkTemp) {
                // 修改单元格边框颜色
                _this.cellCheck = false;
                if (!!col.s) {
                  const style = _this.$curSheet().getStyle(col.s);
                  // console.log('style.option', style);
                  if (style) {
                    _this.cellCheckStyle[index + '' + key] = JSON.parse(JSON.stringify(style.option));
                  }
                }
                
                _this.$curSheet().setAreaStyle(exSelect, style => {
                    style.setOption({
                      borderColor: 'red',
                      border: 'blrt',
                    });
                });
              } else {
                if (col.p.vd.r || !!col.p.vd.ex) {
                  const option = {};
                  if (!!_this.cellCheckStyle[index + '' + key]) {
                    Object.assign(option, JSON.parse(JSON.stringify(_this.cellCheckStyle[index + '' + key])))
                  }
                  if (!option.borderColor) {
                    Object.assign(option, { borderColor: undefined });
                  }
                  if (!option.border) {
                    Object.assign(option, { border: undefined });
                  }
                  _this.$curSheet().setAreaStyle(exSelect, style => {
                      style.setOption(option);
                  });
                }
              }
            }
          }
        });
      });
      if (!_this.cellCheck) {
        _this.$modal.msgError('校验未通过！！！');
        return;
      }
      return false;
    },
  },
};