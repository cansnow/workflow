<template>
  <div class="preview" :style="pos == 'center' ? { width: `${screenW + 25}px` } : ''">
    <div class="preview_title">{{ title }}</div>
    <Sheet
      style="height: calc(100vh - 40px)"
      :ref="'sheet_'+index"
      :options="data"
      :sheetIndex="index"
    />
  </div>
</template>

<script>
import Sheet from './Sheet.vue';
import '../../helpers/lodashMixins';
import testData from './testData';
import { saveFormData, getDBData, updateFormData, fileList, deleteFormData } from '@/api/editManage';
import { mapGetters } from "vuex";
export default {
  components: { Sheet },
  data() {
    return {
      data: testData[0].data,
      index: 0,
      title: testData[0].title,
      previewData: {},
      ifPreview: true,
      constants: {},
      dataSetList: {},
      pos: 'center',
      cellFormData: [],// 单元格回写规则
      extendInfo: {}, // 扩展信息记录
      query: {}, // 参数
      screenW: 0, // 宽度
    };
  },
  computed: {
    $curSheet() {
      return this.$refs['sheet_' + this.index];
    },
    maxWidth() {
      return this.$curSheet.maxWidth;
    },
    ...mapGetters(["name"]),
  },
  async mounted() {
    const _this = this;
    await fileList();
    // 获取数据集
    const res = await getDBData({ table: 'lang' });
    const constants = _.map(res.data.constants, (item, key) => {
      // orgName roleName userId userName
      const temp = {
        userId: '${USER_ID}',
        userName: '${USER_NAME}',
        roleName: '${ROLE_NAME}',
        orgName: '${ORG_NAME}',
      };
      return {
        key: temp[key],
        value: item,
      };
    });
    // const constants = res.data.constants;
    if (!!constants && constants instanceof Array) {
      // _this.constants = constants;
      constants.forEach((item, index) => {
        _this.constants['key_' + index] = item.key;
        _this.constants['value_' + index] = item.value
        /**
         * [{key: '${USER_ID}', value: '当前用户id' },
         * { key: '${USER_NAME}', value: '当前用户名' },
         * { key: '${ROLE_NAME}', value: '当前角色名称' },
         * { key: '${ORG_NAME}', value: '当前部门名称'}]
         */
      });
    }
    this.init();
    // 按钮点击事件
    this.$curSheet.$on('clikcCellBtn', async function(res) {
      if (_this.ifPreview) {
        return;
      }
      // 提交删除数据
      if (res.p.t == 'submit' || res.p.t == 'delete' || res.p.t == 'search') {
        // 提交
        if (res.p.t == 'submit') {
          // 根据回写规则提交数据
          // 表单校验
          // _this.$curSheet.cells
          let ifOK = true;
          _.each(_this.$curSheet.cells, (cell, key) => {
            _.each(cell, (col, index) => {
              if (
                !!col &&
                typeof col.c != 'undefined' &&
                col.c != 'Cell' &&
                col.c != 'image' &&
                col.c != 'button'
              ) {
                if (col.p.vd.r && col.p.r.w && col.p.r.s) {
                  if (!col.v) {
                    console.log('pos', key, index);
                    ifOK = false;
                    return;
                  }
                }
              }
            })
          });
          if (!ifOK) {
            _this.$modal.msgError('有必填项未填写，请填写！！！');
            return;
          }
          if (_this.previewData.dataList && _this.previewData.dataList.length > 0 && ifOK) {
            const dataList = _this.previewData.dataList;
            const responseData = [];
            _.map(dataList, (item, index) => {
              if (!item.ifDel || item.ifDel != '1') {
                const temp = { table: item.title };
                if (item.filedList && item.filedList.length > 0) {
                  const fields = _.map(item.filedList, fObj => {
                    let fieldValue = '';
                    if (fObj.type == 'cell') {
                      // 字段位置
                      let columnIndex = fObj.value.replace(/[^a-zA-Z]/g,'');
                      let rowIndex = fObj.value.replace(/[^0-9]/g,'');
                      columnIndex = _.$ABC2Number(columnIndex);
                      rowIndex = rowIndex - 1;
                      const pos = { rowIndex, columnIndex };
                      const cell = _this.$curSheet.getPosCell(pos);
                      // 1. 判断是否扩展
                      // 2. 获取扩展方向
                      // 3. 判断是否被顶出去
                      // 4. 获取扩展后的集合
                      // 5. 封装成数据
  
                      if (typeof cell.p != 'undefined' && typeof cell.p.e != 'undefined' && (cell.p.e == 'bottom' || cell.p.e == 'right')) {
                        if (cell.p.e == 'bottom') {
                          // 向下扩展
                          const extend = _this.extendInfo.column[columnIndex];
                          const index = extend.record.findIndex(erfItem => erfItem.startRow == rowIndex);
                          if (index != -1) {
                            const record = extend.record[index];
                            // 循环获取数据
                            const values = [];
                            for (let i = 0; i < record.count + 1; i++) {
                              // 向下取行
                              const pos = { rowIndex: rowIndex + i, columnIndex };
                              const cell = _this.$curSheet.getPosCell(pos);
                              values.push(cell.v);
                            }
                            fieldValue = values; // 获取扩展集合数据
                          }
                        } else {
                          // 向右扩展
                          const extend = _this.extendInfo.row[rowIndex];
                          const index = extend.record.findIndex(erfItem => erfItem.startCol == columnIndex);
                          if (index != -1) {
                            const record = extend.record[index];
                            // 循环获取数据
                            const values = [];
                            for (let i = 0; i < record.count + 1; i++) {
                              // 向下取行
                              const pos = { rowIndex, columnIndex: columnIndex + i };
                              const cell = _this.$curSheet.getPosCell(pos);
                              values.push(cell.v);
                            }
                            fieldValue = values; // 获取扩展集合数据
                          }
                        }
                      } else {
                        fieldValue = cell.v; // 字段值
                      }
                    }
                    // 固定值
                    if (fObj.type == 'value') {
                      fieldValue = fObj.value;
                    }
                    // 参数 parameter
                    if (fObj.type == 'parameter') {
                      let valueKey = '';
                      _.map(_this.constants, (value, key) => {
                        if (key.indexOf('key_') != -1) {
                          // 判断是否包含替换变量
                          if (fObj.value.indexOf(value) != -1) {
                            // 获取值的key
                            valueKey = key.replace('key_', 'value_');
                          }
                        }
                      });
                      fieldValue = _this.constants[valueKey] || '';
                    }
      
                    return {
                      fieldName: fObj.filed, // 字段名
                      fieldValue, // 字段值
                      ifExtend: fieldValue instanceof Array, // 是否扩展
                    };
                  });
                  // 判断是否有主键，有主键为修改，无主键为新增
                  const ifKey = item.filedList.findIndex(filed => filed.key);
                  if (ifKey != -1) {
                    const keys = _.chain(item.filedList).filter(f => f.key).map(f => f.filed);
                    Object.assign(temp, { ifKey: true, keys });
                  }
                  const ifExtend = fields.findIndex(field => field.ifExtend);
                  if (ifExtend != -1) {
                    // 有扩展
                    _.map(fields[ifExtend].fieldValue, (fv, fi) => {
                      const fieldsTemp = _.map(fields, fTemp => {
                        if (fTemp.ifExtend) {
                          return {
                            fieldName: fTemp.fieldName,
                            fieldValue: fTemp.fieldValue.length == fi ? fTemp.fieldValue[fTemp.fieldValue.length ] : fTemp.fieldValue[fi],
                          };
                        } else {
                          return {
                            fieldName: fTemp.fieldName,
                            fieldValue: fTemp.fieldValue,
                          };
                        }
                      });
                      Object.assign(temp, { fields: fieldsTemp });
                      console.log('temp', temp);
                      responseData.push(JSON.parse(JSON.stringify(temp)));
                    });
                  } else {
                    Object.assign(temp, { fields });
                    console.log('temp', temp);
                    responseData.push(temp);
                  }
                }
              }
            });
            const resData = {};
            const updateData = [];
            _.map(responseData, item => {
              if (!!item.ifKey) {
                const conditions = [];
                if (item.keys.length > 0) {
                  _.map(item.keys, key => {
                    const index = item.fields.findIndex(f => f.fieldName == key);
                    conditions.push({
                      fieldName: key,
                      fieldValue: index != -1 ? item.fields[index].fieldValue : '',
                    });
                  });
                }
                updateData.push({
                  conditions,
                  table: item.table,
                  fields: item.fields,
                });
                
              } else {
                if (typeof resData[item.table] == 'undefined') {
                  const temp = [];
                  temp.push(item.fields);
                  resData[item.table] = temp;
                } else {
                  resData[item.table].push(item.fields);
                }
              }
            });
            const data = _.map(resData, (item, key) => {
              return {
                table: key,
                fields: item,
              }
            });
            if (data.length > 0) {
              await saveFormData(data).then((res) => {
                console.log('saveFormData', res);
                _this.$modal.msgSuccess('提交成功！！！');
              });
            }
            if (updateData.length > 0) {
              await updateFormData(updateData).then((res) => {
                console.log('updateFormData', res);
                _this.$modal.msgSuccess('提交成功！！！');
                _this.dataSetList = {};
              });
            }
          }
          // 单元格回写规则
          if (_this.cellFormData.length > 0 && false) {
            const data = [];
            const temp = _.chain(_this.cellFormData).groupBy('table').map((item, table) => {
              const fileds = _.chain(item).map(field => {
                const cell = _this.$curSheet.getPosCell(field.pos);
                return Object.assign({}, field, { value: cell.v });
              }).groupBy('id').map((value, id) => {
                const fileds = _.map(value, val => {
                  return {
                    fieldName: val.field,
                    fieldValue: val.value,
                  }
                })
                return {
                  idName: 'id',
                  idValue: id,
                  table: table,
                  fields: fileds,
                }
              }).value();
              data.push(...fileds);
              return fileds;
            }).value();
            console.log('temp', temp);
            console.log('data', data);
            updateFormData(data).then((res) => {
              console.log('updateFormData', res);
              _this.$modal.msgSuccess('提交成功！！！');
            });
          }
        }
        // 删除
        if (res.p.t == 'delete') {
          if (_this.previewData.dataList && _this.previewData.dataList.length > 0) {
            const dataList = _this.previewData.dataList;
            const responseData = [];
            _.map(dataList, item => {
              if (!!item.ifDel && item.ifDel == '1') {
                const temp = { table: item.title };
                if (item.filedList && item.filedList.length > 0) {
                  const fields = _.map(item.filedList, fObj => {
                    let fieldValue = '';
                    if (fObj.type == 'cell') {
                      // 字段位置
                      let columnIndex = fObj.value.replace(/[^a-zA-Z]/g,'');
                      let rowIndex = fObj.value.replace(/[^0-9]/g,'');
                      columnIndex = _.$ABC2Number(columnIndex);
                      rowIndex = rowIndex - 1;
                      const pos = { rowIndex, columnIndex };
                      const cell = _this.$curSheet.getPosCell(pos);
                      // 1. 判断是否扩展
                      // 2. 获取扩展方向
                      // 3. 判断是否被顶出去
                      // 4. 获取扩展后的集合
                      // 5. 封装成数据
                      if (typeof cell.p != 'undefined' && typeof cell.p.e != 'undefined' && (cell.p.e == 'bottom' || cell.p.e == 'right')) {
                        if (cell.p.e == 'bottom') {
                          // 向下扩展
                          const extend = _this.extendInfo.column[columnIndex];
                          const index = extend.record.findIndex(erfItem => erfItem.startRow == rowIndex);
                          if (index != -1) {
                            const record = extend.record[index];
                            // 循环获取数据
                            const values = [];
                            for (let i = 0; i < record.count + 1; i++) {
                              // 向下取行
                              const pos = { rowIndex: rowIndex + i, columnIndex };
                              const cell = _this.$curSheet.getPosCell(pos);
                              values.push(cell.v);
                            }
                            fieldValue = values; // 获取扩展集合数据
                          }
                        } else {
                          // 向右扩展
                          const extend = _this.extendInfo.row[rowIndex];
                          const index = extend.record.findIndex(erfItem => erfItem.startCol == columnIndex);
                          if (index != -1) {
                            const record = extend.record[index];
                            // 循环获取数据
                            const values = [];
                            for (let i = 0; i < record.count + 1; i++) {
                              // 向下取列
                              const pos = { rowIndex, columnIndex: columnIndex + i };
                              const cell = _this.$curSheet.getPosCell(pos);
                              values.push(cell.v);
                            }
                            fieldValue = values; // 获取扩展集合数据
                          }
                        }
                      } else {
                        fieldValue = cell.v; // 字段值
                      }
                    }
                    // 固定值
                    if (fObj.type == 'value') {
                      fieldValue = fObj.value;
                    }
                    // 参数 parameter
                    if (fObj.type == 'parameter') {
                      let valueKey = '';
                      _.map(_this.constants, (value, key) => {
                        if (key.indexOf('key_') != -1) {
                          // 判断是否包含替换变量
                          if (fObj.value.indexOf(value) != -1) {
                            // 获取值的key
                            valueKey = key.replace('key_', 'value_');
                          }
                        }
                      });
                      fieldValue = _this.constants[valueKey] || '';
                    }
      
                    return {
                      fieldName: fObj.filed, // 字段名
                      fieldValue, // 字段值
                      ifExtend: fieldValue instanceof Array, // 是否扩展
                    };
                  });
                  // 判断是否有主键，有主键为修改，无主键为新增
                  const ifKey = item.filedList.findIndex(filed => filed.key);
                  if (ifKey != -1) {
                    const keys = _.chain(item.filedList).filter(f => f.key).map(f => f.filed);
                    Object.assign(temp, { ifKey: true, keys });
                  }
                  const ifExtend = fields.findIndex(field => field.ifExtend);
                  if (ifExtend != -1) {
                    // 有扩展
                    _.map(fields[ifExtend].fieldValue, (fv, fi) => {
                      const fieldsTemp = _.map(fields, fTemp => {
                        if (fTemp.ifExtend) {
                          return {
                            fieldName: fTemp.fieldName,
                            fieldValue: fTemp.fieldValue.length == fi ? fTemp.fieldValue[fTemp.fieldValue.length ] : fTemp.fieldValue[fi],
                          };
                        } else {
                          return {
                            fieldName: fTemp.fieldName,
                            fieldValue: fTemp.fieldValue,
                          };
                        }
                      });
                      Object.assign(temp, { fields: fieldsTemp });
                      console.log('temp', temp);
                      responseData.push(JSON.parse(JSON.stringify(temp)));
                    });
                  } else {
                    Object.assign(temp, { fields });
                    console.log('temp', temp);
                    responseData.push(temp);
                  }
                }
              }
            });
            const delData = [];
            _.map(responseData, item => {
              if (!!item.ifKey) {
                const conditions = [];
                if (item.keys.length > 0) {
                  _.map(item.keys, key => {
                    const index = item.fields.findIndex(f => f.fieldName == key);
                    let fieldValue = '';
                    fieldValue = index != -1 ? item.fields[index].fieldValue : '';
                    if (key == 'status') {
                      if (fieldValue instanceof Array) {
                        fieldValue = fieldValue.length > 0 && fieldValue[0] == 'true' ? true : false;
                      } else {
                        fieldValue = fieldValue == 'true' ? true : false;
                      }
                    }
                    conditions.push({
                      fieldName: key,
                      fieldValue,
                    });
                  });
                }
                delData.push({
                  conditions,
                  table: item.table,
                  // fields: item.fields,
                });
              }
            });
            if (delData.length > 0) {
              await deleteFormData(delData).then((res) => {
                console.log('deleteFormData', res);
                _this.$modal.msgSuccess('删除成功！');
                // 重置
                _this.dataSetList = {};
                _this.update(_this.$piniastore.$state);
                _this.cellFormData = [];
              });
            }
          }
        }
        
        // 搜索
        if (res.p.t == 'search') {
          if (_this.previewData.searchList && _this.previewData.searchList.length > 0) {
            const searchList = _this.previewData.searchList;
            _.map(searchList, item => {
              if (item.filedList && item.filedList.length > 0) {
                _.map(item.filedList, fObj => {
                  let fieldValue = '';
                  if (fObj.type == 'cell') {
                    // 字段位置
                    let columnIndex = fObj.value.replace(/[^a-zA-Z]/g,'');
                    let rowIndex = fObj.value.replace(/[^0-9]/g,'');
                    columnIndex = _.$ABC2Number(columnIndex);
                    rowIndex = rowIndex - 1;
                    const pos = { rowIndex, columnIndex };
                    const cell = _this.$curSheet.getPosCell(pos);
                    fieldValue = cell.v; // 字段值
                  }
                  // 固定值
                  if (fObj.type == 'value') {
                    fieldValue = fObj.value;
                  }
                  // 参数 parameter
                  if (fObj.type == 'parameter') {
                    let valueKey = '';
                    _.map(_this.constants, (value, key) => {
                      if (key.indexOf('key_') != -1) {
                        // 判断是否包含替换变量
                        if (fObj.value.indexOf(value) != -1) {
                          // 获取值的key
                          valueKey = key.replace('key_', 'value_');
                        }
                      }
                    });
                    fieldValue = _this.constants[valueKey] || '';
                  }
                  const temp = {};
                  temp[fObj.filed] = fieldValue;

                  Object.assign(_this.query, temp);
    
                  return {
                    fieldName: fObj.filed, // 字段名
                    fieldValue, // 字段值
                  };
                });
              }
            });
            _this.dataSetList = {};
            _this.update(_this.$piniastore.$state);
            _this.cellFormData = [];
          }
        }
      } else {
        // 重置
        _this.dataSetList = {};
        _this.update(_this.$piniastore.$state);
        _this.cellFormData = [];
      }
    });
    // 单元格超链接点击事件
    this.$curSheet.$on('clikcCellLink', function(link) {
      const cells = link.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
      let linkTemp = link;
      if (!!cells && cells instanceof Array && cells.length > 0) {
        _.map(cells, item => {
          const str = item.replaceAll(/\$|\{|\}/g, '');
          if (str.indexOf(':') == -1) {
            const pos = _this.formatData(str);
            const cell = _this.$curSheet.getPosCell(pos);
            cell.v; // 字段值
            linkTemp = linkTemp.replace(item, cell.v);
          } else {
            const strList = str.split(':');
            const start = _this.formatData(strList[0]);
            const end = _this.formatData(strList[1]);
            const values = [];
            for (let i = start.rowIndex; i <= end.rowIndex; i++) {
              for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                const cell = _this.$curSheet.getPosCell({ rowIndex: i, columnIndex: j });
                if (!!cell && typeof cell.v != 'undefined' && cell.v != null ) {
                  values.push(cell.v);
                }
              }
            }
            linkTemp = linkTemp.replace(item, values);
          }
        });
      }
      // 变量取值
      const constant = new RegExp(/\$\{\w*\}/);
      if(constant.test(linkTemp)) { // 有包含变量
        // 获取判断条件，获取变量
        _.map(_this.constants, (value, key) => {
          if (key.indexOf('key_') != -1) {
            // 判断是否包含替换变量
            if (linkTemp.indexOf(value) != -1) {
              // 获取值的key
              const valueKey = key.replace('key_', 'value_');
              // ${USER_ID} == admin => '$userName$ == "admin"';
              linkTemp = linkTemp.replaceAll(value, _this.constants[valueKey]);
            }
          }
        });
      }
      window.open(linkTemp, '_blank');
    });

  },
  methods: {
    // 判断是否在有效区域内
    inRangeInside(range, pos) {
      if (!!range.start) {
        const start = this.formatData(range.start);
        if (pos.columnIndex < start.columnIndex) {
          return false;
        }
        if (pos.rowIndex < start.rowIndex) {
          return false;
        }
      }
      if (!!range.end) {
        const end = this.formatData(range.end);
        if (pos.columnIndex > end.columnIndex) {
          return false;
        }
        if (pos.rowIndex > end.rowIndex) {
          return false;
        }
      }
      return true;
    },
    async formatCellData(data, formList, Range, searchList) {
      // 行的信息
      const rows = [];
      // 列的信息
      const columns = [];
      // 合并单元格的信息
      const merges = [];
      // 单元格信息
      const cells = {};
      // 样式信息
      const styles = {};

      // 扩展信息
      const extendList = [];

      // 选项值信息
      const selectOptionInfo = [];

      // 单元格超链接扩展记录
      const cellLinks = {};

      let pos = -1;

      const cellPro = ['s', 'fs', 'f', 'v', 'c', 'p', 'fc', 'sv', 'fcv', 'd', 'options'];
      
      const stylePro = [
        'border',
        'borderStyle',
        'backgroundColor',
        'color',
        'fontFamily',
        'fontSize',
        'fontStyle',
        'fontWeight',
        'textDecoration',
        'textAlign',
        'verticalAlign',
        'whiteSpace',
        'textDel',
        'borderColor',
        'borderBold',
      ];
      
      _.each(data, (item) => {
        /** 行信息 */
        if (typeof(rows[item.pos.start.rowIndex]) == 'undefined') {
          if (item.pos.start.rowIndex > rows.length) {
            const nullLen = item.pos.start.rowIndex - rows.length;
            for (let i = 0; i < nullLen; i++) {
              rows.push(null);
            }
          }
          rows.push({ hpx: item.height });          
        }
        /** 列信息 */
        if (typeof(columns[item.pos.start.columnIndex]) == 'undefined') {
          if (item.pos.start.columnIndex > columns.length) {
            const nullLen = item.pos.start.columnIndex - columns.length;
            for (let i = 0; i < nullLen; i++) {
              columns.push(null);
            }
          }
          columns.push({ wpx: item.width });          
        } else {
          if (columns[item.pos.start.columnIndex] == null) {
            columns[item.pos.start.columnIndex] = { wpx: item.width };
          }
        }
        /** 合并信息 */
        if(typeof(item.merges) != 'undefined') {
          merges.push(item.merges);
        }
        /** 单元格信息 */
        pos = item.pos.start.rowIndex;
        // 是否在有效区域内
        const inRangeInside = this.inRangeInside(Range, item.pos.start);
        let temp = {};
        // 将 s fs f v c p
        cellPro.forEach(pItem => {
          if (typeof(item[pItem]) != 'undefined') {
            temp[pItem] = item[pItem];
          }
        });

        // temp.c == checkbox selectMultiple
        if (typeof temp.c != 'undefined' && (temp.c == 'checkbox' || temp.c == 'selectMultiple')) {
          if (typeof temp.v != 'undefined') {
            Object.assign(temp, { v: !(temp.v instanceof Array) ? [temp.v + ''] : temp.v });
          } else {
            Object.assign(temp, { v: [] })
          }
        }

        // 设置选项值
        if (
          typeof temp.c != 'undefined' &&
          (
            temp.c == 'radio' ||
            temp.c == 'checkbox' ||
            temp.c == 'select' ||
            temp.c == 'selectMultiple'
          ) &&
          typeof temp.p != 'undefined' &&
          typeof temp.p.ds != 'undefined' &&
          temp.p.ds == 'api' &&
          typeof temp.p.api != 'undefined' && !!temp.p.api &&
          (typeof temp.p.apiLabel != 'undefined' || typeof temp.p.apiValue != 'undefined') &&
          (!!temp.p.apiLabel || !!temp.p.apiValue)
        ) {
          // 判断范围
          if (inRangeInside) {
            selectOptionInfo.push({
              pos: item.pos, // 位置信息
              field: temp.p.api, // 变量
              apiLabel: temp.p.apiLabel || temp.p.apiValue,
              apiValue: temp.p.apiValue || temp.p.apiLabel,
            });
          }
        }
        // 判断范围
        // 判断默认值是否包含变量
        if (typeof temp.v != 'undefined' && inRangeInside) {
          const constant = new RegExp(/\$\{\w*\}/);
          if(constant.test(temp.v)) { // 有包含变量
            // 获取判断条件，获取变量
            let expression = temp.v;
            _.map(this.constants, (value, key) => {
              if (key.indexOf('key_') != -1) {
                // 判断是否包含替换变量
                if (temp.v.indexOf(value) != -1) {
                  // 获取值的key
                  const valueKey = key.replace('key_', 'value_');
                  // ${USER_ID} == admin => '$userName$ == "admin"';
                  expression = expression.replaceAll(value, this.constants[valueKey]);
                }
              }
            });
            // 判断是否有 + - * / 符号
            const exp = new RegExp(/\+|\-|\*|\//);
            Object.assign(temp, { v: exp.test(expression) ? expression : expression.replace('=', '') });
            if (exp.test(expression)) {
              Object.assign(temp, { fs: exp.test(expression) ? expression.replace('=', '') : '' });
            }
          }
        }
        // 判断范围
        // 记录扩展
        // 获取p.f 变量，替换v
        if (typeof temp.p != 'undefined' && typeof temp.p.f != 'undefined' && inRangeInside) {
          console.log('temp', temp);
          console.log('temp.p.f', temp.p.f);
          if (temp.p.f.indexOf('.') != -1 && temp.p.f.indexOf('dataSetList') != -1) {
            const fList = temp.p.f.split('.');
            console.log('fList', fList);
            const itemPost = item.pos;
            if (fList[0] == 'dataSetList') {
              // 记录超链接属性，用于扩展
              const extend = {
                c: temp.c,
                pos: itemPost, // 位置信息
                fieldInfo: temp.p.f, // 变量信息
                fieldIndex: fList[1], // 数据集下标
                field: fList[2], // 变量
                extendType: typeof temp.p.e != 'undefined' ? temp.p.e : 'none', // 扩展方向
                merges: typeof(item.merges) != 'undefined', // 是否合并
                mergesInfo: typeof(item.merges) != 'undefined' ? item.merges : '',
                height: item.height,
                width: item.width,
              };
              // 单元格超链接属性
              if (!!temp.c && temp.c == 'Cell' && !!temp.p.ct && temp.p.ct == 'Link') {
                Object.assign(extend, {
                  ct: temp.p.ct,
                  cl: temp.p.cl || '',
                });
                if (!!temp.p.cl) {
                  const fields = temp.p.cl.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
                  if (!!fields && fields.length > 0) {
                    cellLinks[itemPost.start.rowIndex  + '' + itemPost.start.columnIndex] = {
                      fields,
                    };
                  }
                }
              }
              extendList.push(extend);
            }
          }
        }
        // 设置禁用
        if (
          typeof temp.p != 'undefined' &&
          typeof temp.p.r != 'undefined' &&
          typeof temp.p.r.w != 'undefined'
        ) {
          let expression = temp.p.r.r[1];
          if (!!expression) {
            // 获取判断条件，获取变量
            _.map(this.constants, (value, key) => {
              if (key.indexOf('key_') != -1) {
                // 判断是否包含替换变量
                if (expression.indexOf(value) != -1) {
                  // 获取值的key
                  const valueKey = key.replace('key_', 'value_');
                  // ${USER_ID} == admin => '$userName$ == "admin"';
                  expression = expression.replace(value, '$' + valueKey + '$');
                }
              }
            });
            const ifStart = this.getResult(expression);
            if (!ifStart) {
              const p = temp.p;
              const r = p.r;
              r.w = !r.w;
              Object.assign(p, { r });
              Object.assign(temp, { p });
            }
          }
        }
        // 设置隐藏
        if (
          typeof temp.p != 'undefined' &&
          typeof temp.p.r != 'undefined' &&
          typeof temp.p.r.s != 'undefined'
        ) {
          // 表达式
          let expression = temp.p.r.r[0];
          if (!!expression) {
            // 获取判断条件，获取变量
            _.map(this.constants, (value, key) => {
              if (key.indexOf('key_') != -1) {
                // 判断是否包含替换变量
                if (expression.indexOf(value) != -1) {
                  // 获取值的key
                  const valueKey = key.replace('key_', 'value_');
                  // ${USER_ID} == admin => '$userName$ == "admin"';
                  expression = expression.replace(value, '$' + valueKey + '$');
                }
              }
            });
            const ifStart = this.getResult(expression);
            if (!ifStart) {
              const p = temp.p;
              const r = p.r;
              r.s = !r.s;
              Object.assign(p, { r });
              Object.assign(temp, { p });
            }
          }
        }
        if (typeof(cells[pos]) == 'undefined') {
          const cellList = [];
          /** 判断null */
          if (item.pos.start.columnIndex != 0) {
            const nullLen = item.pos.start.columnIndex;
            for (let i = 0; i < nullLen; i++) {
              cellList.push(null);
            }
          }
          // 设置显示隐藏
          if (typeof temp.p != 'undefined' && !temp.p.r.s) {
            cellList.push(null);
          } else {
            cellList.push(temp);
          }
          cells[pos] = cellList;
        } else {
          /** 判断null */
          if (item.pos.start.columnIndex > cells[pos].length) {
            const nullLen = item.pos.start.columnIndex - cells[pos].length;
            for (let i = 0; i < nullLen; i++) {
              cells[pos].push(null);
            }
          }
          // 设置显示隐藏
          if (
            typeof temp.p != 'undefined' &&
            typeof temp.p.r != 'undefined' &&
            !temp.p.r.s
          ) {
            cells[pos].push(null);
          } else {
            cells[pos].push(temp);
          }
        }

        /** 组装样式 */
        if (typeof(item.s) != 'undefined') {
          if(typeof(styles[item.s]) == 'undefined') {
            let tempStyle = {};
            stylePro.forEach(sItem => {
              if (typeof(item[sItem]) != 'undefined') {
                tempStyle[sItem] = item[sItem];
              }
            });
            styles[item.s] = tempStyle;
          }
        }
      });

      // 搜索赋值
      if (!!searchList && searchList.length > 0) {
        _.map(searchList, item => {
          if (item.filedList && item.filedList.length > 0) {
            _.map(item.filedList, fObj => {
              if (fObj.type == 'cell') {
                // 字段位置
                let columnIndex = fObj.value.replace(/[^a-zA-Z]/g,'');
                let rowIndex = fObj.value.replace(/[^0-9]/g,'');
                columnIndex = _.$ABC2Number(columnIndex);
                rowIndex = rowIndex - 1;
                if(typeof this.query[fObj.filed] != 'undefined') {
                  if (typeof cells[rowIndex] != 'undefined') {
                    const temp = !!cells[rowIndex][columnIndex] ? JSON.parse(JSON.stringify(cells[rowIndex][columnIndex])) : {};
                    Object.assign(temp, { v: this.query[fObj.filed] });
                    cells[rowIndex].splice(columnIndex, 1, temp);
                  }
                }
              }
            });
          }
        });
      }
      
      // 权限规则
      if (formList && formList.length > 0) {
        // temp.cells
        _.map(formList, item => {
          // 表达式
          let expression = item.expression;
          // 获取判断条件，获取变量
          _.map(this.constants, (value, key) => {
            if (key.indexOf('key_') != -1) {
              // 判断是否包含替换变量
              if (expression.indexOf(value) != -1) {
                // 获取值的key
                const valueKey = key.replace('key_', 'value_');
                // ${USER_ID} == admin => '$userName$ == "admin"';
                expression = expression.replace(value, '$' + valueKey + '$');
              }
            }
          });
          const ifStart = this.getResult(expression); //'$userName$ == "admin"';
          if (ifStart) {
            const list = item.range.split(':');
            // G9
            if (list.length <= 1) {
              const data = this.formatData(list[0]);
              if (item.type == 'hide') {
                cells[data.rowIndex].splice(data.columnIndex, 1, null);
              } else {
                // 禁用
                const tempCell = cells[data.rowIndex][data.columnIndex];
                if (!!tempCell && typeof tempCell.c != 'undefined' && tempCell.c != 'Cell' ) {
                  const p = tempCell.p;
                  p.r.w = false;
                  Object.assign(tempCell, { p });
                }
                cells[data.rowIndex].splice(data.columnIndex, 1, tempCell);
              }
            }
            // G9:G10
            if (list.length > 1) {
              const start = this.formatData(list[0]);
              const end = this.formatData(list[1]);
              for (let i = start.rowIndex; i <= end.rowIndex; i++) {
                for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                  if (item.type == 'hide') {
                    cells[i].splice(j, 1, null);
                  } else {
                    // 禁用
                    const tempCell = cells[i][j];
                    if (!!tempCell && typeof tempCell.c != 'undefined' && tempCell.c != 'Cell' ) {
                      const p = tempCell.p;
                      p.r.w = false;
                      Object.assign(tempCell, { p });
                    }
                    cells[i].splice(j, 1, tempCell);
                  }
                }
              }
            }
          }
        });
      }

      // 选项赋值
      if (selectOptionInfo.length > 0) {
        console.log('selectOptionInfo', selectOptionInfo);
        for(let selectIndex = 0; selectIndex < selectOptionInfo.length; selectIndex++) {
          const item = selectOptionInfo[selectIndex];
          let valueList = [];
          if (typeof this.dataSetList[item.field] != 'undefined') {
            // TODO 记录已有数据
            valueList = this.dataSetList[item.field];
          } else {
            const res = await getDBData({ table: item.field, ...this.query });
            const dataSetListTemp = res.data.dataSetList;
            valueList = dataSetListTemp[0].valueList; // 变量集合
            this.dataSetList[dataSetListTemp[0].dataSetId] = dataSetListTemp[0].valueList;
          }
          const options = _.map(valueList, value => {
            return {
              label: value[item.apiLabel],
              value: value[item.apiValue],
            };
          });
          const cellsIndex = item.pos.start;
          const cell = JSON.parse(JSON.stringify(cells[cellsIndex.rowIndex][cellsIndex.columnIndex]));
          if (!!cell) {
            cell.options = options;
            cells[cellsIndex.rowIndex].splice(cellsIndex.columnIndex, 1, cell);
          }
        }
      }

      // 单元格超链接扩展取值
      if (Object.keys(cellLinks).length > 0) {
        _.map(cellLinks, (item, key) => {
          const posList = _.map(item.fields, field => {
            const pos = field.replaceAll(/\$|\{|\}/g, '');
            const temp = {
              start: {},
              end: {},
              only: true,
            };
            if (pos.indexOf(':') != -1) {
              const cells = pos.split(':');
              Object.assign(temp, {
                start: this.formatData(cells[0]),
                end: this.formatData(cells[1]),
                only: false,
              });
            } else {
              const cellPos = this.formatData(pos);
              Object.assign(temp, {
                start: cellPos,
                end: cellPos,
              });
            }
            if (temp.only) {
              const cell = cells[temp.start.rowIndex][temp.start.columnIndex];
              if (!!cell && !!cell.p && !!cell.p.e) {
                Object.assign(temp, { e: cell.p.e });
              }
            }
            return temp;
          });
          Object.assign(cellLinks[key], { posList });
        });
        console.log('cellLinks', cellLinks);
      }

      // 设置扩展
      if (extendList.length > 0) {
        console.log('extendList', extendList);
        // 扩展行列记录
        // {
        //   column: {
        //     0: {
        //       count: 25,
        //       record: [
        //         {
        //           startRow: 0,
        //           count: 25
        //         }
        //       ],
        //     }
        //   },
        // }
        const extendInfo = {
          column: {}, // 列
          row: {},
        };
        for (let extendIndex = 0; extendIndex < extendList.length; extendIndex++) {
          const item = extendList[extendIndex];
          const rowIndex = item.pos.start.rowIndex;
          const columnIndex = item.pos.start.columnIndex;
          const rowHeight = item.height; // 行高
          const colWidth = item.width; // 列宽
          // 如果有合并
          // 合并行树
          const rowCountMerges = item.merges ? item.mergesInfo.end.rowIndex - item.pos.start.rowIndex : 0;
          // 合并列树
          const colCountMerges = item.merges ? item.mergesInfo.end.columnIndex - item.pos.start.columnIndex : 0;
          let valueList = [];
          if (typeof this.dataSetList[item.fieldIndex] != 'undefined') {
            // TODO 记录已有数据
            valueList = this.dataSetList[item.fieldIndex];
          } else {
            const res = await getDBData({ table: item.fieldIndex, ...this.query });
            const dataSetListTemp = res.data.dataSetList;
            valueList = dataSetListTemp[0].valueList; // 变量集合
            this.dataSetList[dataSetListTemp[0].dataSetId] = dataSetListTemp[0].valueList;
          }
          
          const len = valueList.length; // 长度
          // 获取填充数据
          const tempList = [];
          const cellColumnIndex = typeof extendInfo.row[rowIndex] == 'undefined' ? columnIndex : columnIndex + extendInfo.row[rowIndex].count;
          const cellRowIndex = typeof extendInfo.column[columnIndex] == 'undefined' ? rowIndex : rowIndex + extendInfo.column[columnIndex].count;
          let tempListIndexValue = '';
          _.map(valueList, (value, index) => {
            if (index != 0) {
              const template = !!cells[cellRowIndex] && !!cells[cellRowIndex][cellColumnIndex] ? JSON.parse(JSON.stringify(cells[cellRowIndex][cellColumnIndex])) : {};
              Object.assign(template, { v: value[item.field]});
              // 判断是否超链接单元格
              if (!!item.ct && typeof cellLinks[rowIndex + '' + columnIndex] != 'undefined') {
                const cellLink = cellLinks[rowIndex + '' + columnIndex];
                const p = template.p;
                let link = p.cl;
                if (cellLink.posList.length > 0) {
                  _.map(cellLink.posList, (pos, Pindex) => {
                    // _.$Number2ABC(columnIndex)
                    // 单个单元格扩展
                    if (pos.only) {
                      if (pos.e != 'bottom') {
                        const col = _.$Number2ABC(pos.start.columnIndex + index + 1);
                        link = link.replace(cellLink.fields[Pindex], '${' + col + pos.start.rowIndex + '}');
                      } else {
                        const col = _.$Number2ABC(pos.start.columnIndex);
                        link = link.replace(cellLink.fields[Pindex], '${' + col + (pos.start.rowIndex + index + 1) + '}');
                      }
                    }
                  });
                }
                Object.assign(p, { cl: link });
                Object.assign(template, { p });
              }
              if (typeof template.c != 'undefined' && (template.c == 'checkbox' || template.c == 'selectMultiple')) {
                if (typeof template.v != 'undefined') {
                  Object.assign(template, { v: !(template.v instanceof Array) ? [template.v + ''] : template.v });
                } else {
                  Object.assign(template, { v: [] })
                }
              }
              tempList.push(template);
            } else {
              tempListIndexValue = value[item.field];
            }
            // 单元格回写规则记录 input select有效
            const itemTypes = {
              text: 'input',
              number: 'input',
              password: 'input',
              datetime: 'input',
              radio: 'select',
              checkbox: 'select',
              select: 'select',
              selectMultiple: 'select',
            };
            
            if (!!itemTypes[item.c]) {
              if ((item.extendType == 'none' && index == 0) || item.extendType != 'none') {
                const pos = {
                  rowIndex: item.extendType == 'bottom' ? rowIndex + index : rowIndex,
                  columnIndex: item.extendType == 'right' ? columnIndex + index : columnIndex,
                };
                this.cellFormData.push({
                  pos, // 位置
                  table: item.fieldIndex, // 表名
                  id: value['id'], // id
                  field: item.field, // 修改字段
                });
              }
            }
          });

          const temp = JSON.parse(JSON.stringify(cells[cellRowIndex][cellColumnIndex]));
          Object.assign(temp, { v: tempListIndexValue + '' });
          if (typeof temp.c != 'undefined' && (temp.c == 'checkbox' || temp.c == 'selectMultiple')) {
            if (typeof temp.v != 'undefined') {
              Object.assign(temp, { v: !(temp.v instanceof Array) ? [temp.v + ''] : temp.v });
            } else {
              Object.assign(temp, { v: [] })
            }
          }
          cells[cellRowIndex].splice(cellColumnIndex, 1, temp);

          // none 无 bottom 向下 right 向右
          if (item.extendType == 'bottom') {
            if (tempList.length > 0) {
              let posBom = cellRowIndex + rowCountMerges + 1; //cellRowIndex + 1;
              // 记录位置
              if (typeof extendInfo.column[columnIndex] == 'undefined') {
                // extendInfo.column[columnIndex] = len - 1;
                extendInfo.column[columnIndex] = {
                  count: len - 1,
                  record: [
                    {
                      startRow: rowIndex + rowCountMerges,
                      count: len - 1,
                    }
                  ],
                }
              } else {
                // posBom += extendInfo.column[columnIndex]; // 将前面加过的位置加上
                // extendInfo.column[columnIndex] += len - 1; // 加上现在的扩展长度
                extendInfo.column[columnIndex].count += len - 1;
                extendInfo.column[columnIndex].record.push({
                  startRow: rowIndex + rowCountMerges,
                  count: len - 1,
                });
              }
              const tempReplace = []; // 记录替换的数据，后面加回去
              const rowsReplace = [];
              _.map(tempList, (item, index) => {
                // 长度不对
                const cellsLen = Object.keys(cells).sort((a, b) => b - a)[0];
                const ifCell = !!cells[posBom + index];
                const temp = ifCell && !!cells[posBom + index][columnIndex] ? JSON.parse(JSON.stringify(cells[posBom + index][columnIndex])) : null;
                tempReplace.push(temp); // 记录数据
                // 加入扩展数据
                if (ifCell) {
                  if (columnIndex >= cells[posBom + index].length) {
                    const nullLen = columnIndex - cells[posBom + index].length;
                    for (let i = 0; i < nullLen; i++) {
                      cells[posBom + index].push(null);
                    }
                    cells[posBom + index].push(item);
                  } else {
                    cells[posBom + index].splice(columnIndex, 1, item);
                  }
                  // 添加新增数据
                  cells[parseInt(cellsLen) + 1] = [];
                } else {
                  const cellList = [];
                  const nullLen = columnIndex;
                  for (let i = 0; i < nullLen; i++) {
                    cellList.push(null);
                  }
                  cellList.push(item);
                  cells[posBom + index] = cellList;
                  if (posBom + index != parseInt(cellsLen) + 1) {
                    cells[parseInt(cellsLen) + 1] = [];
                  } else {
                    cells[parseInt(cellsLen) + 2] = [];
                  }
                }
                // 修改扩展行高
                if (typeof(rows[posBom + index]) == 'undefined') {
                  rows.push({ hpx: rowHeight });
                  rowsReplace.push(null);         
                } else {
                  rowsReplace.push(rows[posBom + index] != null ? JSON.parse(JSON.stringify(rows[posBom + index])) : null);
                  rows.splice(posBom + index, 1, {hpx: rowHeight});
                }
                // 判断最后的下一行是否有值
                if (index == tempList.length - 1) {
                  const ifNextCell = !!cells[posBom + tempList.length];
                  if (!ifNextCell) {
                    cells[posBom + tempList.length] = [];
                  }
                }
              });
              // console.warn('tempReplace', tempReplace);
              // 数据向下移动
              let replaceIndex = 0;
              const cellkeys = Object.keys(cells).sort((a, b) => a - b);
              _.map(cellkeys, (key, index) => {
                if (key >= posBom + tempList.length) {
                  // 判断是否为空
                  if (columnIndex >= cells[key].length) {
                    const nullLen = columnIndex - cells[key].length;
                    for (let i = 0; i < nullLen; i++) {
                      cells[key].push(null);
                    }
                    tempReplace.push(null); // 记录替换
                    cells[key].push(tempReplace[replaceIndex]);
                  } else {
                    tempReplace.push(cells[key][columnIndex]); // 记录替换
                    cells[key].splice(columnIndex, 1, tempReplace[replaceIndex]);
                  }
                  // 修改向下行高
                  if (typeof(rows[key]) == 'undefined') {
                    rows.push(rowsReplace[replaceIndex]);
                    rowsReplace.push(null);         
                  } else {
                    rowsReplace.push(rows[key] != null ? JSON.parse(JSON.stringify(rows[key])) : null);
                    if (rows[key] != null && rowsReplace[replaceIndex] != null) {
                      if (rows[key].hpx < rowsReplace[replaceIndex].hpx) {
                        rows.splice(key, 1, rowsReplace[replaceIndex]);
                      }
                    } else {
                      if (rows[key] == null || rowsReplace[replaceIndex] != null) {
                        rows.splice(key, 1, rowsReplace[replaceIndex]);
                      }
                    }
                  }
                  replaceIndex += 1;
                  // 判断下一个节点是否有值，没有值的时候就要补充进去，防止错位
                  if (!cells[parseInt(key) + 1]) {
                    if (index < cellkeys.length -1 ) {
                      const repairLen = cellkeys[index + 1] - (parseInt(key) + 1);
                      for(let i = 0; i < repairLen; i++) {
                        const cellList = [];
                        for (let j = 0; j < columnIndex; j++) {
                          cellList.push(null);
                        }
                        tempReplace.push(null); // 记录替换
                        cellList.push(tempReplace[replaceIndex + i]);
                        cells[parseInt(key) + i + 1] = cellList;

                        // 修改向下行高
                        if (typeof(rows[parseInt(key) + i + 1]) == 'undefined') {
                          rows.push(rowsReplace[replaceIndex + i]);
                          rowsReplace.push(null);
                        } else {
                          rowsReplace.push(rows[parseInt(key) + i + 1] != null ? JSON.parse(JSON.stringify(rows[parseInt(key) + i + 1])) : null);
                          // rows.splice(parseInt(key) + i + 1, 1, rowsReplace[replaceIndex + i]);

                          if (rows[parseInt(key) + i + 1] != null && rowsReplace[replaceIndex + i] != null) {
                            if (rows[parseInt(key) + i + 1].hpx < rowsReplace[replaceIndex + i].hpx) {
                              rows.splice(parseInt(key) + i + 1, 1, rowsReplace[replaceIndex + i]);
                            }
                          } else {
                            if (rows[parseInt(key) + i + 1] == null || rowsReplace[replaceIndex + i] != null) {
                              rows.splice(parseInt(key) + i + 1, 1, rowsReplace[replaceIndex + i]);
                            }
                          }
                        }
                      }
                      replaceIndex += repairLen;
                    }
                  }
                }
              });
            }
          }
          if (item.extendType == 'right') {
            if (tempList.length > 0) {
              let pos = columnIndex + colCountMerges + 1; // columnIndex + 1;
              // 记录位置
              if (typeof extendInfo.row[rowIndex] == 'undefined') {
                // extendInfo.row[rowIndex] = len - 1;
                extendInfo.row[rowIndex] = {
                  count: len - 1,
                  record: [
                    {
                      startCol: columnIndex + colCountMerges,
                      count: len - 1,
                    }
                  ],
                }
              } else {
                // pos += extendInfo.row[rowIndex]; // 将前面加过的位置加上
                pos += extendInfo.row[rowIndex].count;
                // extendInfo.row[rowIndex] += len - 1; // 加上现在的扩展长度
                extendInfo.row[rowIndex].count += len - 1;
                extendInfo.row[rowIndex].record.push({
                  startCol: columnIndex + colCountMerges,
                  count: len - 1,
                });                
              }
              if (!!cells[cellRowIndex]) {
                if (cells[cellRowIndex].length < pos) {
                  const nullLen = pos - cells[cellRowIndex].length;
                  for (let i = 0; i < nullLen; i++) {
                    cells[cellRowIndex].push(null);
                  }
                }
                cells[cellRowIndex].splice(pos, 0, ...tempList);
              } else {
                const cellList = [];
                const nullLen = pos;
                for (let i = 0; i < nullLen; i++) {
                  cellList.push(null);
                }
                cellList.push(...tempList);
                cells[cellRowIndex] = cellList;
              }
              if (!!columns[pos]) {
                // TODO 有问题，多个向右扩展会导致数据的错乱
                // const colReplace = JSON.parse(JSON.stringfiy(columns[pos].slice(pos)));
                const tempCol = _.map(tempList, () => {
                  return { wpx: colWidth == columns[pos-1].wpx ? colWidth : columns[pos-1].wpx };
                });
                columns.splice(pos, 0, ...tempCol);
              } else {
                const tempCol = _.map(tempList, () => {
                  return { wpx: colWidth };
                });
                columns.push(...tempCol);
              }
            }
          }
        }

        // console.warn('extendInfo', extendInfo);
        this.extendInfo = extendInfo;
      }

      return {
        rows,
        columns,
        merges,
        cells,
        styles,
      };
    },
    init() {
        let _this = this;
        _this.update(this.$piniastore.$state);
        this.$piniastore.$subscribe((mutation, state) => {
            _this.update(state);
        });
        this.index = 0;
    },
    // 格式化数据
    formatData(data) {
      let columnIndex = data.replace(/[^a-zA-Z]/g,'');
      let rowIndex = data.replace(/[^0-9]/g,'');
      columnIndex = _.$ABC2Number(columnIndex);
      rowIndex = rowIndex - 1;
      return { columnIndex, rowIndex };
    },
    // 获取比对值
    getComparisonValue(data) {
      // == < > != <= >=
      const operator = ['==', '<', '>', '!=', '<=', '>='];
      const index = operator.findIndex(item => {
        const index = data.indexOf(item);
        return index != -1;
      });
      return [index, data.replace(/[^a-zA-Z]/g,'')];
    },
    comparison(variable, operator, value) {
      switch(operator) {
        // '==', '<', '>', '!=', '<=', '>='
        case 0:
          return variable == value;
        case 1:
          return variable < value;
        case 2:
          return variable > value;
        case 3:
          return variable != value;
        case 4:
          return variable <= value;
        case 5:
          return variable >= value;
        default:
          return false;
      }
    },
    getResult(data) {
      // 1.获取变量，从this.constants[field]取值
      // 2.获取判断条，== < > != <= >=
      // 3.获取对比值，如：admin
      const expression = data;// '$userName$ == "admin"';
      let ifStart = false;
      const index = expression.indexOf('$');
      if (index != -1) {
        const strList = expression.split('$').filter(item => !!item);
        if (strList.length > 1) {
          // 取变量
          const value = this.constants[strList[index == 0 ? index : 1]];
          if (typeof value != 'undefined') {
            // 取值
            const comparison = this.getComparisonValue(strList[index == 0 ? 1 : 0]);
            if (comparison[0] != -1) {
              // 判断是否生效规则
              ifStart = this.comparison(
                index == 0 ? value : comparison[1],
                comparison[0],
                index == 0 ? comparison[1] : value
              );
            }
          }
        }
      }
      return ifStart;
    },
    async update(state) {
        this.previewData = state.previewData;
        const tempData = JSON.parse(JSON.stringify(testData[0].data));
        this.ifPreview = state.previewData.ifPreview;
        // 自定动态变量
        if (!!state.previewData.query && Object.keys(state.previewData.query).length > 0) {
          Object.assign(this.query, state.previewData.query);
          let index = 4;
          const _this = this;
          _.map(state.previewData.query, (value, key) => {
            _this.constants['key_' + index] = '${' + key + '}';
            _this.constants['value_' + index] = value;
            index += 1;
          });
        }

        this.pos = state.previewData.pos || 'center';

        const temp = await this.formatCellData(
          state.previewData.cells,
          state.previewData.formList,
          { end: state.previewData.end, start: state.previewData.start || 'A1' },
          state.previewData.searchList
        );
        
        // start: data.start, 
        let columnIndex = null;
        let rowIndex = null;
        if (state.previewData.end && false) {
          columnIndex = state.previewData.end.replace(/[^a-zA-Z]/g,'');
          rowIndex = state.previewData.end.replace(/[^0-9]/g,'');
          columnIndex = _.$ABC2Number(columnIndex) + 1;
          // rowIndex = rowIndex - 1;
        } else {
          // 如果没设，则默认最小单元格，
          const key = Object.keys(temp.cells).sort((a, b) => temp.cells[b].length - temp.cells[a].length)[0];
          const rowLen = Object.keys(temp.cells).length; // temp.rows.length;
          const colLen = temp.cells[key].length; // temp.columns.length;
          rowIndex = rowLen < 20 ? 200 : rowLen;
          columnIndex = colLen < 20 ? 20 : colLen;
        }
        Object.assign(temp, {
          title: state.previewData.title,
          rowCount: rowIndex || 200,
          columnCount: columnIndex  || 20,
          maxRowCount: rowIndex || 100000,
          maxColumnCount: columnIndex || 200,
          freezeColumn: state.previewData.freezeColumn, // 冻结行列
          freezeRow: state.previewData.freezeRow, // 冻结行列
        });
        // 对cells做处理，超出结束行列的cell要清空掉
        // _.map(temp.cells, (item, key) => {
        //   if (parseInt(key) > parseInt(rowIndex)) {
        //     delete temp.cells[key];
        //   }
        //   if (item.length > parseInt(columnIndex)) {
        //     item.splice(parseInt(columnIndex), item.length);
        //   }
        // });
        let colLen = _.map(temp.cells, item => item.length).sort((a, b) => b - a)[0];
        colLen = colLen < 20 ? 20 : colLen;
        this.screenW = 0;
        for (let i = 0; i < colLen; i++) {
          if (!!temp.columns[i]) {
            this.screenW += temp.columns[i].wpx;
          } else {
            this.screenW += 64;
          }
        }
        Object.assign(tempData, temp);
        this.data = tempData;
        this.title = tempData.title || 'sheet';

        this.$nextTick(() => {
          if (this.screenW < this.maxWidth) {
            this.screenW = this.maxWidth;
          }
        });
    },
  },
}
</script>

<style lang="scss">
.preview {
  height: 100vh;
  // width: 60vw;
  margin: auto;
  // border: 1px solid #ddd;

  &_title {
    display: flex; 
    align-items: center;
    justify-content: center;
    height: 40px;
    // border-bottom: 1px solid #ddd;
  }
}  
</style>