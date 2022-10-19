<template>
  <div>
    <div class="mainPreview" style="position: relative; top: 0; left: 0; right: 0; bottom: 0; overflow: hidden; height: 100vh;">
      <el-tabs type="border-card" tab-position="bottom" v-model="sheetIndex">
          <el-tab-pane :label="sheet.data.title" v-for="(sheet,index) in data" :key="'_' + index" :name="'_' + index">
          </el-tab-pane>
      </el-tabs>
      <div
        class="preview"
        :key="index"
        v-for="(item, index) in data"
        style="height: calc(100%-42px); width: 100vw; background-color: white; position: absolute; top: 42px; left: 0; right: 0; bottom: 0;"
        :style="{ zIndex: sheetIndex == '_'+index ? 99 : -1 }"
      >
        <div
          style="margin: auto; height: 100%"
          :style="{ 
            width: item.info.pos == 'center' ? 
            `${(item.info.screenW > (maxWidth || 0) ? item.info.screenW : maxWidth) > screenWidth ? screenWidth : (item.info.screenW > (maxWidth || 0) ? item.info.screenW : maxWidth) + 50 || 1400}px`
            : null
          }"
        >
          <Sheet
            style="height: 100%"
            :ref="'sheet_'+index"
            :options="item.data"
            :sheetIndex="index"
            @clikcCellBtn="clikcCellBtn"
            @clikcCellLink="clikcCellLink"
            @doDeleteData="doDeleteData"
            @doAddData="doAddData"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Sheet from './Sheet.vue';
import '../../helpers/lodashMixins';
import testData from './testData';
import { validPhone, validIDCard, validEmail } from '@/utils/validate';
import { 
  saveFormData, 
  getDBData, 
  updateFormDataNew as updateFormData, 
  getConstants, 
  deleteFormData, 
  getTableFieldByName,
} from '@/api/editManage';
export default {
  components: { Sheet },
  data() {
    return {
      data: [],
      sheetIndex: '_0',
      previewData: {},
      ifPreview: true,
      constants: {}, // 全局变量
      dataSetList: {}, // 数据
      query: {}, // 参数
      tableField: {}, // 表字段
      ifEdit: false, 
      screenWidth: 0,
      cellFormData: [],// 没用到，单元格回写规则

      screenW: 0, // 宽度
      pos: 'center',
      addData: {}, // 新增数据
      extendInfo: {}, // 扩展信息记录
      maxWidth: 0,
      // $sheet: null,
      cellCheck: true, // 单元格校验
      cellCheckStyle: {}, // 单元格样式缓存
    };
  },
  async mounted() {
    const _this = this;
    // await fileList();
    // 获取数据集
    const res = await getConstants();
    const constants = _.map(res.constants, (item, key) => {
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
    this.screenWidth = document.body.clientWidth
    window.onresize = () => {
        return (() => {
            this.screenWidth = document.body.clientWidth
        })()
    };
  },
  watch: {
    sheetIndex(newV) {
      this.$curSheet().maxWidth = this.data[newV.substring(1)].info.screenW;
      this.$curSheet().maxHeight = this.data[newV.substring(1)].info.screenH;
    },
  },
  methods: {
    $curSheet() {
      return this.$refs['sheet' + this.sheetIndex][0];
    },
    // 按钮点击事件
    async clikcCellBtn(res) {
      const _this = this;
      if (_this.ifPreview) {
        return;
      }
      // 提交删除数据
      if (res.p.t == 'submit' || res.p.t == 'delete' || res.p.t == 'search') {
        // 提交
        if (res.p.t == 'submit') {
          // 根据回写规则提交数据
          // 表单校验
          _this.cellCheck = true;
          _.each(_this.$curSheet().cells, (cell, key) => {
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
          const dataList = _this.data[_this.sheetIndex.substring(1)].info.dataList;
          if (dataList && dataList.length > 0 && _this.cellCheck) {
            const responseData = [];
            _.map(dataList, (item, index) => {
              if (!item.ifDel || item.ifDel != '1') {
                const temp = { table: item.title, ifId: item.ifId };
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
                      const cell = _this.$curSheet().getPosCell(pos);
                      // 1. 判断是否扩展
                      // 2. 获取扩展方向
                      // 3. 判断是否被顶出去
                      // 4. 获取扩展后的集合
                      // 5. 封装成数据
                      if (!!cell) {
                        if (typeof cell.p != 'undefined' && typeof cell.p.e != 'undefined' && (cell.p.e == 'bottom' || cell.p.e == 'right')) {
                          if (cell.p.e == 'bottom') {
                            // 向下扩展
                            const extend = _this.extendInfo[_this.sheetIndex].column[columnIndex];
                            if (!!extend) {
                              const index = extend.record.findIndex(erfItem => erfItem.startRow == rowIndex);
                              if (index != -1) {
                                const record = extend.record[index];
                                // 循环获取数据
                                const values = [];
                                for (let i = 0; i < record.count + 1; i++) {
                                  // 向下取行
                                  const pos = { rowIndex: rowIndex + i, columnIndex };
                                  const cell = _this.$curSheet().getPosCell(pos);
                                  console.log('cell', cell);
                                  values.push(cell.v);
                                }
                                fieldValue = values; // 获取扩展集合数据
                              }
                            } else {
                              fieldValue = cell.v; // 字段值
                            }                          
                          } else {
                            // 向右扩展
                            const extend = _this.extendInfo[_this.sheetIndex].row[rowIndex];
                            if (!!extend) {
                              const index = extend.record.findIndex(erfItem => erfItem.startCol == columnIndex);
                              if (index != -1) {
                                const record = extend.record[index];
                                // 循环获取数据
                                const values = [];
                                for (let i = 0; i < record.count + 1; i++) {
                                  // 向下取行
                                  const pos = { rowIndex, columnIndex: columnIndex + i };
                                  const cell = _this.$curSheet().getPosCell(pos);
                                  values.push(cell.v);
                                }
                                fieldValue = values; // 获取扩展集合数据
                              }
                            } else {
                              fieldValue = cell.v; // 字段值
                            }
                          }
                        } else {
                          fieldValue = cell.v; // 字段值
                        }
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
                      fieldValue: fieldValue, // 字段值
                      ifExtend: fieldValue instanceof Array, // 是否扩展
                    };
                  });
                  // 判断是否有主键，有主键为修改，无主键为新增
                  const ifKey = item.filedList.findIndex(filed => filed.key);
                  if (ifKey != -1) {
                    const keys = _.chain(item.filedList).filter(f => f.key).map(f => f.filed).value();
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
              // 判断true false
              const fields = _.map(item.fields, field => {
                if (field.fieldValue == 'true') {
                  return {
                    fieldName: field.fieldName,
                    fieldValue: true,
                  }
                }
                if (field.fieldValue == 'false') {
                  return {
                    fieldName: field.fieldName,
                    fieldValue: false,
                  }
                }
                return field;
              });
              // 有主键修改
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
                    if (!!fieldValue) {
                      conditions.push({
                        fieldName: key,
                        fieldValue,
                      });
                    }
                    
                    const id = fields.findIndex(f => f.fieldName == 'id');
                    if (id != -1) {
                      fields.splice(id, 1);
                    }
                  });
                }
                if (conditions.length > 0) {
                  updateData.push({
                    conditions,
                    table: item.table,
                    fields,
                    ifId: item.ifId
                  });
                }
              } else {
                // 新增删除id
                const idIndex = fields.findIndex(f => f.fieldName == 'id');
                if (idIndex != -1) {
                  fields.splice(idIndex, 1);
                }
                // TODO 判断是否客户表，新增需要设置id，uuid
                if (item.ifId || item.table == 'person' || item.table == 'tea_sale') {
                  const id = _this.guid();
                  fields.push({
                    fieldName: 'id',
                    fieldValue: id,
                  })
                }
                if (typeof resData[item.table] == 'undefined') {
                  const temp = [];
                  temp.push(fields);
                  resData[item.table] = temp;
                } else {
                  resData[item.table].push(fields);
                }
              }
            });
            // 新增的数据
            const data = [];
            _.map(resData, (item, key) => {
              _.map(item, fileds => {
                data.push({
                  table: key,
                  singleFields: fileds,
                })
              })
            });

            // 新增数据
            const addInfo = [];
            // 修改 剔除新增数据
            const delUpdateData = [];
            _.map(updateData, (u, i) => {
              _.map(u.conditions, c => {
                // 我们的表新增
                if (!u.ifId && c.fieldName == 'id' && c.fieldValue == -1) {
                  addInfo.push({
                    t: u.table,
                    values: JSON.parse(JSON.stringify(u.fields)),
                  });
                  // updateData.splice(i, 1);
                  delUpdateData.push(i);
                }
                // 客户的表新增
                if (u.ifId || u.table == 'person' || u.table == 'tea_sale') {
                  if (c.fieldName == 'id') {
                    if (!!_this.addData[_this.sheetIndex] && !!_this.addData[_this.sheetIndex][u.table]) {
                      const index = _this.addData[_this.sheetIndex][u.table].findIndex(item => item.id == c.fieldValue);
                      if (index != -1) {
                        addInfo.push({
                          t: u.table,
                          id: c.fieldValue,
                          values: JSON.parse(JSON.stringify(u.fields)),
                        });
                        // updateData.splice(i, 1);
                        delUpdateData.push(i);
                      }
                    }
                  }
                }
              });
            });
            // 删除新增项
            if (delUpdateData.length > 0) {
              _.map(delUpdateData, item => {
                updateData.splice(item, 1);
              })
            }
            // 新增
            if ((data.length > 0 && !_this.ifEdit) || addInfo.length > 0) {
              if (addInfo.length > 0) {
                _.map(addInfo, a => {
                  // 补全字段
                  const fields = a.values;
                  _.map(_this.tableField[a.t], field => {
                    const fi = fields.findIndex(f => f.fieldName == field);
                    if (fi == -1) {
                      if (field == 'id') {
                        // 客户表需要id，我们的表不需要
                        if (a.t == 'person' || a.t == 'tea_sale') {
                          const id = _this.guid();
                          fields.push({
                            fieldName: field,
                            fieldValue: a.id || id,
                          });
                        }
                      } else {
                        fields.push({
                          fieldName: field,
                          fieldValue: '1',
                        });
                      }
                    }
                  });
                  data.push({
                    table: a.t,
                    singleFields: fields,
                  });
                });
              }
              if (updateData.length <= 0) {
                let ifNullData = false;
                _.map(data, item => {
                  if (item.singleFields.length < 0) {
                    ifNullData = true;
                  }
                });
                if (ifNullData) {
                  _this.$modal.msgError('新增数据为空！！！');
                  return;
                }
              }
            }
            // 修改
            if (updateData.length > 0) {
              let ifConditions = true;
              let ifFields = true;
              _.map(updateData, item => {
                if (item.conditions.length <= 0) {
                  ifConditions = false;
                }
                if (item.fields.length <= 0) {
                  ifFields = false;
                }
              });
              if (!ifConditions) {
                _this.$modal.msgError('主键为空，修改错误！！！');
                return;
              }
              if (!ifFields) {
                _this.$modal.msgError('修改数据为空，请检查回写规则！！！');
                return;
              }
            }
            const submitData = {
              formDataVO: data,
              updateObj: updateData,
            };
            await saveFormData(submitData).then((res) => {
              console.log('saveFormData', res);
              _this.$modal.msgSuccess('提交成功！！！');
            });
            _this.addData[_this.sheetIndex] = {};
            _this.dataSetList = {};
            _this.update(_this.$piniastore.$state);
            _this.cellFormData = [];
          }
          // TODO 单元格回写规则 暂时不用
          if (_this.cellFormData.length > 0 && false) {
            const data = [];
            const temp = _.chain(_this.cellFormData).groupBy('table').map((item, table) => {
              const fileds = _.chain(item).map(field => {
                const cell = _this.$curSheet().getPosCell(field.pos);
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
        // TODO 删除 暂时不用
        if (false && res.p.t == 'delete') {
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
                      const cell = _this.$curSheet().getPosCell(pos);
                      // 1. 判断是否扩展
                      // 2. 获取扩展方向
                      // 3. 判断是否被顶出去
                      // 4. 获取扩展后的集合
                      // 5. 封装成数据
                      if(!!cell) {
                        if (typeof cell.p != 'undefined' && typeof cell.p.e != 'undefined' && (cell.p.e == 'bottom' || cell.p.e == 'right')) {
                          if (cell.p.e == 'bottom') {
                            // 向下扩展
                            const extend = _this.extendInfo[_this.sheetIndex].column[columnIndex];
                            if (!!extend) {
                              const index = extend.record.findIndex(erfItem => erfItem.startRow == rowIndex);
                              if (index != -1) {
                                const record = extend.record[index];
                                // 循环获取数据
                                const values = [];
                                for (let i = 0; i < record.count + 1; i++) {
                                  // 向下取行
                                  const pos = { rowIndex: rowIndex + i, columnIndex };
                                  const cell = _this.$curSheet().getPosCell(pos);
                                  values.push(cell.v);
                                }
                                fieldValue = values; // 获取扩展集合数据
                              }
                            } else {
                              fieldValue = cell.v; // 字段值
                            }
                            
                          } else {
                            // 向右扩展
                            const extend = _this.extendInfo[_this.sheetIndex].row[rowIndex];
                            if (!!extend) {
                              const index = extend.record.findIndex(erfItem => erfItem.startCol == columnIndex);
                              if (index != -1) {
                                const record = extend.record[index];
                                // 循环获取数据
                                const values = [];
                                for (let i = 0; i < record.count + 1; i++) {
                                  // 向下取列
                                  const pos = { rowIndex, columnIndex: columnIndex + i };
                                  const cell = _this.$curSheet().getPosCell(pos);
                                  values.push(cell.v);
                                }
                                fieldValue = values; // 获取扩展集合数据
                              }
                            } else {
                              fieldValue = cell.v; // 字段值
                            }
                          }
                        } else {
                          fieldValue = cell.v; // 字段值
                        }
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
                    const keys = _.chain(item.filedList).filter(f => f.key).map(f => f.filed).value();
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
                            fieldValue: fTemp.fieldValue.length == fi ? fTemp.fieldValue[fTemp.fieldValue.length] : fTemp.fieldValue[fi],
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
          const searchList = _this.data[_this.sheetIndex.substring(1)].info.searchList;
          if (searchList && searchList.length > 0) {
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
                    const cell = _this.$curSheet().getPosCell(pos);
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
        _this.addData[_this.sheetIndex] = {};
        _this.dataSetList = {};
        _this.update(_this.$piniastore.$state);
        _this.cellFormData = [];
      }
    },
    // 单元格超链接点击事件
    clikcCellLink(link) {
      const _this = this;
      const cells = link.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
      let linkTemp = link;
      if (!!cells && cells instanceof Array && cells.length > 0) {
        _.map(cells, item => {
          const str = item.replaceAll(/\$|\{|\}/g, '');
          // 单个单元格
          if (str.indexOf(':') == -1) {
            const pos = _this.formatData(str);
            const cell = _this.$curSheet().getPosCell(pos);
            // 字段值 cell.v;
            linkTemp = linkTemp.replace(item, cell.v);
          } else {
            // 多个单元格
            const strList = str.split(':');
            const start = _this.formatData(strList[0]);
            const end = _this.formatData(strList[1]);
            const values = [];
            for (let i = start.rowIndex; i <= end.rowIndex; i++) {
              for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                const cell = _this.$curSheet().getPosCell({ rowIndex: i, columnIndex: j });
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
    },
    // 删除数据
    async doDeleteData({ cell: data }) {
      const _this = this;
      console.log('data', data);
      const loading = _this.$loading({
        lock: true,
        text: 'Loading',
        // spinner: 'el-icon-loading',
        // background: 'rgba(0, 0, 0, 0.7)'
      });
      if (!!data && !!data.cdi && !!data.t && !!data.i) {
        let ifDelApi = true;
        if (
          (data.t == 'person' || data.t == 'tea_sale' || data.i == -1) && 
          (typeof _this.addData[_this.sheetIndex] != 'undefined' && typeof _this.addData[_this.sheetIndex][data.t] != 'undefined')
        ) {
          const index = _this.addData[_this.sheetIndex][data.t].findIndex(item => item.id == data.i);
          if (index != -1) {
            ifDelApi = false;
            _this.addData[_this.sheetIndex][data.t].splice(index, 1)
          }
        }
        if (ifDelApi) {
          const res = await deleteFormData([{
            conditions: data.cdi,
            table: data.t
          }]).finally(() => {
            loading.close();
          });
          console.log('deleteFormData', res);
        }        
        _this.$modal.msgSuccess('删除成功！');
        // 重置
        _this.dataSetList = {};
        _this.update(_this.$piniastore.$state);
        _this.cellFormData = [];
      }
      loading.close();
    },
    // 新增数据
    doAddData({ cell: data }) {
      const _this = this;
      console.log('data', data);
      const loading = _this.$loading({
        lock: true,
        text: 'Loading',
        // spinner: 'el-icon-loading',
        // background: 'rgba(0, 0, 0, 0.7)'
      });
      if (!!data && !!data.t && !!_this.tableField[data.t]) {
        const temp = {};
        //1. 获取字段，组合新数据
        _.map(_this.tableField[data.t], field => {
          temp[field] = '';
        });
        //2. 设置id，用于提交数据时剔除修改项，加入新增项
        if (data.t == 'person' || data.t == 'tea_sale') {
          const id = _this.guid();
          Object.assign(temp, { id });
        } else {
          Object.assign(temp, { id: -1 });
        }
        //3. 添加到全局，重载数据渲染
        if (!!_this.addData[_this.sheetIndex]) {
          if (!!_this.addData[_this.sheetIndex][data.t]) {
            _this.addData[_this.sheetIndex][data.t].push(temp);
          } else {
            _this.addData[_this.sheetIndex][data.t] = [temp];
          }
        } else {
          const addTemp = {};
          addTemp[data.t] = [temp]
          _this.addData[_this.sheetIndex] = addTemp;
        }
        // 重置数据
        _this.dataSetList = {};
        _this.update(_this.$piniastore.$state);
        _this.cellFormData = [];
      }
      loading.close();
    },
    guid() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0,
                v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    },
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
      const index = operator.findIndex(item => data.includes(item));
      return [index, data.replace(/[^a-zA-Z0-9]/g,'')];
    },
    // 对比判断
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
      // 1.获取判断条，== < > != <= >=
      // 2.获取对比值，如：admin
      const expression = data;// '"admin" == "admin"';
      let ifStart = true;
      const operator = ['==', '<', '>', '!=', '<=', '>='];
      const ifOperator = operator.findIndex(item => expression.includes(item));
      if (ifOperator != -1) {
        const strList = expression.split(operator[ifOperator]).filter(item => !!item);
        if (strList.length > 1) {
          ifStart = this.comparison(strList[0], ifOperator, strList[1]);
        }
      }
      return ifStart;
    },
    async formatCellData(data, formList, Range, searchList, dataList) {
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

      // 记录隐藏带单元格判断
      const hiddenList = [];

      // 记录禁用带单元格判断
      const disabledList = [];

      let pos = -1;
      // 配置属性
      const cellPro = ['s', 'fs', 'f', 'v', 'c', 'p', 'fc', 'sv', 'fcv', 'd', 'options'];
      // 样式属性
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
          rows.push({ hpx: item.height, h: !!item.hh ? true : undefined });  
        }
        /** 列信息 */
        if (typeof(columns[item.pos.start.columnIndex]) == 'undefined') {
          if (item.pos.start.columnIndex > columns.length) {
            const nullLen = item.pos.start.columnIndex - columns.length;
            for (let i = 0; i < nullLen; i++) {
              columns.push(null);
            }
          }
          columns.push({ wpx: item.width, h: !!item.wh ? true : undefined });          
        } else {
          if (columns[item.pos.start.columnIndex] == null) {
            columns[item.pos.start.columnIndex] = { wpx: item.width, h: !!item.wh ? true : undefined };
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
          (!!temp.p.apiLabel || !!temp.p.apiValue) &&
          typeof temp.p.apiT != 'undefined'
        ) {
          // 判断范围
          if (inRangeInside) {
            selectOptionInfo.push({
              pos: item.pos, // 位置信息
              field: temp.p.api, // 变量
              apiLabel: temp.p.apiLabel || temp.p.apiValue,
              apiValue: temp.p.apiValue || temp.p.apiLabel,
              fieldT: temp.p.apiT, // 表名
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
        if (
          typeof temp.p != 'undefined' &&
          typeof temp.p.f != 'undefined' &&
          typeof temp.p.tn != 'undefined' &&
          inRangeInside
        ) {
          console.log('temp', temp);
          console.log('temp.p.f', temp.p.f);
          if (temp.p.f.indexOf('.') != -1 && temp.p.f.indexOf('dataSetList') != -1) {
            const fList = temp.p.f.split('.');
            console.log('fList', fList);
            const itemPost = item.pos;
            if (fList[0] == 'dataSetList') {
              const conditions = [];
              _.map(dataList, rule => {
                if (rule.title == fList[1]) {
                  const conditionsTemp = [];
                  _.map(rule.filedList, filed => {
                    if (filed.key) {
                      conditionsTemp.push(filed.filed);
                    }
                  });
                  conditions.push(conditionsTemp);
                }
              });
              // 记录超链接属性，用于扩展
              const extend = {
                conditions,
                fieldInfo: temp.p.f, // 变量信息
                fieldIndex: fList[1], // 数据集下标
                field: fList[2], // 变量
                tableName: temp.p.tn, // 表名

                noReplace: false, // 替换v, false 替换， true 不替换

                c: temp.c,
                pos: itemPost, // 位置信息
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
              Object.assign(temp, { v: '' });
            }
          } else {
            if (!!temp.c && temp.c == 'Cell' && !!temp.p.ct && temp.p.ct == 'Link') {
            const itemPost = item.pos;
            if (!!temp.p.cl) {
              const fields = temp.p.cl.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
              if (!!fields && fields.length > 0) {
                cellLinks[itemPost.start.rowIndex  + '' + itemPost.start.columnIndex] = {
                  fields,
                  noReplace: true,
                  extend: {
                    c: temp.c,
                    pos: itemPost, // 位置信息
                    extendType: typeof temp.p.e != 'undefined' ? temp.p.e : 'none', // 扩展方向
                    merges: typeof(item.merges) != 'undefined', // 是否合并
                    mergesInfo: typeof(item.merges) != 'undefined' ? item.merges : '',
                    height: item.height,
                    width: item.width,
                    ct: temp.p.ct,
                    cl: temp.p.cl || '',
                  }
                };
              }
            }
          }
          }
        } else {
          // 跟随扩展单元，非数据绑定
          if (!!temp.c && temp.c == 'Cell' && !!temp.p.ct && temp.p.ct == 'Link') {
            const itemPost = item.pos;
            if (!!temp.p.cl) {
              const fields = temp.p.cl.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
              if (!!fields && fields.length > 0) {
                cellLinks[itemPost.start.rowIndex  + '' + itemPost.start.columnIndex] = {
                  fields,
                  noReplace: true,
                  extend: {
                    c: temp.c,
                    pos: itemPost, // 位置信息
                    extendType: typeof temp.p.e != 'undefined' ? temp.p.e : 'none', // 扩展方向
                    merges: typeof(item.merges) != 'undefined', // 是否合并
                    mergesInfo: typeof(item.merges) != 'undefined' ? item.merges : '',
                    height: item.height,
                    width: item.width,
                    ct: temp.p.ct,
                    cl: temp.p.cl || '',
                  }
                };
              }
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
                  expression = expression.replace(value, this.constants[valueKey]);
                }
              }
            });
            if (/\$|\{|\}/g.test(expression)) {
              // 单元格取值
              disabledList.push({
                expression, // 判断
                pos: item.pos, // 位置
              });
            } else {
              // 没有单元格
              const ifStart = this.getResult(expression);
              if (!ifStart) {
                const tempD = JSON.parse(JSON.stringify(temp));
                const p = tempD.p;
                const r = p.r;
                r.w = !r.w;
                Object.assign(p, { r });
                Object.assign(temp, { p });
              }
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
                  expression = expression.replace(value, this.constants[valueKey]);
                }
              }
            });
            if (/\$|\{|\}/g.test(expression)) {
              // 单元格取值
              hiddenList.push({
                expression, // 判断
                pos: item.pos, // 位置
              });
            } else {
              // 没有单元格
              const ifStart = this.getResult(expression);
              if (!ifStart) {
                const tempH = JSON.parse(JSON.stringify(temp));
                const p = tempH.p;
                const r = p.r;
                r.s = !r.s;
                Object.assign(p, { r });
                Object.assign(temp, { p });
              }
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
                if (typeof cells[rowIndex] != 'undefined') {
                  const temp = !!cells[rowIndex][columnIndex] ? JSON.parse(JSON.stringify(cells[rowIndex][columnIndex])) : {};
                  // 传参有值，填写到单元格
                  if(typeof this.query[fObj.filed] != 'undefined') {
                    Object.assign(temp, { v: this.query[fObj.filed] });
                    cells[rowIndex].splice(columnIndex, 1, temp);
                  } else {
                    // 没值，单元格有值，填写到参数进行查询
                    if (!!temp && typeof temp.v != 'undefined' && !!temp.v) {
                      this.query[fObj.filed] = temp.v;
                    }
                  }
                }
              }
              // 固定值
              if (fObj.type == 'value') {
                this.query[fObj.filed] = fObj.value;
              }
              // 参数 parameter
              if (fObj.type == 'parameter') {
                let valueKey = '';
                _.map(this.constants, (value, key) => {
                  if (key.indexOf('key_') != -1) {
                    // 判断是否包含替换变量
                    if (fObj.value.indexOf(value) != -1) {
                      // 获取值的key
                      valueKey = key.replace('key_', 'value_');
                    }
                  }
                });
                this.query[fObj.filed] = this.constants[valueKey] || '';
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
                // ${USER_ID} == admin => '"admin" == "admin"';
                expression = expression.replace(value, this.constants[valueKey]);
              }
            }
          });
          // 判断单元格，取值，有以下情况，该单元是读数据的
          // 还有单元格变量
          if (/\$|\{|\}/g.test(expression)) {
            // 获取单元格集合
            const cellsEx = expression.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
            _.map(cellsEx, cellItem => {
              const str = cellItem.replaceAll(/\$|\{|\}/g, '');
              // 单个单元格
              if (str.indexOf(':') == -1) {
                const pos = this.formatData(str);
                const cell = cells[pos.rowIndex][pos.columnIndex]
                // 字段值 cell.v;
                expression = expression.replace(cellItem, cell.v);
              } else {
                // 多个单元格
                const strList = str.split(':');
                const start = _this.formatData(strList[0]);
                const end = _this.formatData(strList[1]);
                const values = [];
                for (let i = start.rowIndex; i <= end.rowIndex; i++) {
                  for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                    const cell = cells[i][j];
                    if (!!cell && typeof cell.v != 'undefined' && cell.v != null ) {
                      values.push(cell.v);
                    }
                  }
                }
                expression = expression.replace(cellItem, values);
              }
            });
          }
          const ifStart = this.getResult(expression); //'$userName$ == "admin"';
          if (ifStart) {
            // 位置的隐藏，禁用隐藏
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
            
            if (typeof this.tableField[item.field] == 'undefined') {
              // TODO 判断是否uuid，如果是uuid则取fieldT，否则取field
              // uuid 正则：/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i
              // /^[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}$/i
              const params = {};
              if (/^[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}$/i.test(item.field)) {
                Object.assign(params, { tid: item.field });
              } else {
                Object.assign(params, { table: item.field });
              }
              const res = await getTableFieldByName(params);
              const fields = _.map(res.data.columns, item => {
                return item.columnName;
              });
              this.tableField[item.field] = fields;
              // spl参数
              const sqlParams = _.map(res.data.sqlParams, item => {
                return item.paramName;
              });
              this.tableField[item.field + '_sqlParams'] = sqlParams;
            }

            // 获取参数
            const query = {};
            _.map(this.query, (value, key) => {
              const index = this.tableField[item.field + '_sqlParams'].findIndex(field => field == key);
              if (index != -1) {
                if (!!value) {
                  query[key] = value;
                }
              }
            });
            const params = {...query};
            if (/^[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}$/i.test(item.field)) {
              Object.assign(params, { table: item.fieldT });
            } else {
              Object.assign(params, { table: item.field });
            }
            const res = await getDBData(params);
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
                const index = extendList.findIndex(item => item.pos.start.rowIndex == temp.start.rowIndex && item.pos.start.columnIndex == temp.start.columnIndex);
                if (index != -1) {
                  // 添加扩展
                  if (item.noReplace) {
                    const exTarget = extendList[index];
                    const extend = Object.assign({}, item.extend, {
                      noReplace: true,
                      conditions: exTarget.conditions,
                      fieldInfo: exTarget.fieldInfo,
                      fieldIndex: exTarget.fieldIndex,
                      field: exTarget.field,
                      tableName: exTarget.tableName,
                    });
                    extendList.push(extend);
                  }
                }
              }
            }
            return temp;
          });
          Object.assign(cellLinks[key], { posList });
        });
        console.log('cellLinks', cellLinks);
      }

      // expression pos
      const disabledList2 = []
      // 获取单元格判断禁用
      if (disabledList.length > 0) {
        _.map(disabledList, (item, index) => {
          let expression = item.expression;
          const cellsEx = expression.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
          _.map(cellsEx, cellItem => {
            const str = cellItem.replaceAll(/\$|\{|\}/g, '');
            // 单个单元格
            if (str.indexOf(':') == -1) {
              let pos = this.formatData(str);
              const cell = cells[pos.rowIndex][pos.columnIndex]
              // 字段值 cell.v;
              if (!!cell) {
                if (
                  typeof cell.p != 'undefined' &&
                  typeof cell.p.f != 'undefined' &&
                  typeof cell.p.tn != 'undefined' &&
                  !!cell.p.tn
                ) {
                  const dlIndex = disabledList2.findIndex(dlItem => dlItem.index == index);
                  if (dlIndex == -1) {
                    disabledList2.push({
                      expression,
                      pos: item.pos,
                      pos2: [pos],
                      cellItem: [cellItem],
                      index,
                    });
                  } else {
                    const tempDLI = JSON.parse(JSON.stringify(disabledList2[dlIndex]));
                    tempDLI.pos2.push(pos);
                    tempDLI.cellItem.push(cellItem);
                    disabledList2.splice(dlIndex, 1, tempDLI);
                  }
                } else {
                  expression = expression.replace(cellItem, cell.v);
                  const dlIndex = disabledList2.findIndex(dlItem => dlItem.index == index);
                  if (dlIndex != -1) {
                    disabledList2.splice(dlIndex, 1, Object.assign(disabledList2[dlIndex], { expression }));
                  }
                }
              }
            } else {
              // 多个单元格
              const strList = str.split(':');
              const start = _this.formatData(strList[0]);
              const end = _this.formatData(strList[1]);
              const values = [];
              for (let i = start.rowIndex; i <= end.rowIndex; i++) {
                for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                  const cell = cells[i][j];
                  if (!!cell && typeof cell.v != 'undefined' && cell.v != null ) {
                    values.push(cell.v);
                  }
                }
              }
              expression = expression.replace(cellItem, values);
            }
          });
          if (!/\$|\{|\}/g.test(expression)) {
            const ifStart = this.getResult(expression);
            const pos = item.pos;
            if (!ifStart) {
              const cell = cells[pos.start.rowIndex][pos.start.columnIndex];
              if (!!cell) {
                const cellDTemp = JSON.parse(JSON.stringify(cell));
                const p = cellDTemp.p;
                const r = p.r;
                r.w = !r.w;
                Object.assign(p, { r });
                Object.assign(cellDTemp, { p });
                cells[pos.start.rowIndex].splice(pos.start.columnIndex, 1, cellDTemp);
              }            
            }
          }
        });
      }
      const hiddenList2 = []
      // 获取单元格判断隐藏
      if (hiddenList.length > 0) {
        _.map(hiddenList, (item, index) => {
          let expression = item.expression;
          const cellsEx = expression.match(/\$\{[a-zA-Z]*[0-9]*\:[a-zA-Z]*[0-9]*\}|\$\{[a-zA-Z]*[0-9]*\}/g);
          _.map(cellsEx, cellItem => {
            const str = cellItem.replaceAll(/\$|\{|\}/g, '');
            // 单个单元格
            if (str.indexOf(':') == -1) {
              const pos = this.formatData(str);
              const cell = cells[pos.rowIndex][pos.columnIndex]
              // 字段值 cell.v;
              if (!!cell) {
                if (
                  typeof cell.p != 'undefined' &&
                  typeof cell.p.f != 'undefined' &&
                  typeof cell.p.tn != 'undefined' &&
                  !!cell.p.tn
                ) {
                  const hlIndex = hiddenList2.findIndex(hlItem => hlItem.index == index);
                  if (hlIndex == -1) {
                    hiddenList2.push({
                      expression,
                      pos: item.pos,
                      pos2: [pos],
                      cellItem: [cellItem],
                      index,
                    });
                  } else {
                    const tempHLI = JSON.parse(JSON.stringify(hiddenList2[hlIndex]));
                    tempHLI.pos2.push(pos);
                    tempHLI.cellItem.push(cellItem);
                    hiddenList2.splice(hlIndex, 1, tempHLI);
                  }
                } else {
                  expression = expression.replace(cellItem, cell.v);
                  const hlIndex = hiddenList2.findIndex(hlItem => hlItem.index == index);
                  if (hlIndex != -1) {
                    hiddenList2.splice(hlIndex, 1, Object.assign(hiddenList2[hlIndex], { expression }));
                  }
                }
              }
            } else {
              // 多个单元格
              const strList = str.split(':');
              const start = _this.formatData(strList[0]);
              const end = _this.formatData(strList[1]);
              const values = [];
              for (let i = start.rowIndex; i <= end.rowIndex; i++) {
                for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                  const cell = cells[i][j];
                  if (!!cell && typeof cell.v != 'undefined' && cell.v != null ) {
                    values.push(cell.v);
                  }
                }
              }
              expression = expression.replace(cellItem, values);
            }
          });
          if (!/\$|\{|\}/g.test(expression)) {
            const ifStart = this.getResult(expression);
            const pos = item.pos;
            if (!ifStart) {
              const cell = cells[pos.start.rowIndex][pos.start.columnIndex];
              if (!!cell) {
                const cellHTemp = JSON.parse(JSON.stringify(cell));
                const p = cellHTemp.p;
                const r = p.r;
                r.s = !r.s;
                if (!r.s) {
                  cells[pos.start.rowIndex].splice(pos.start.columnIndex, 1, null);
                }
              }
            }
          }
        });
      }

      // 设置扩展
      if (extendList.length > 0) {
        console.log('extendList', extendList);
        // 扩展行列记录
        // {column:{0:{count:25,record:[{startRow: 0,count: 25}]}}}
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
          // 合并行数
          const rowCountMerges = item.merges ? item.mergesInfo.end.rowIndex - item.pos.start.rowIndex : 0;
          // 合并列数
          const colCountMerges = item.merges ? item.mergesInfo.end.columnIndex - item.pos.start.columnIndex : 0;
          let valueList = [];
          // 获取表数据
          if (typeof this.dataSetList[item.fieldIndex] != 'undefined') {
            // TODO 记录已有数据
            valueList = JSON.parse(JSON.stringify(this.dataSetList[item.fieldIndex]));
          } else {

            if (typeof this.tableField[item.fieldIndex] == 'undefined') {
              const params = {};
              if (/^[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}$/i.test(item.tableName)) {
                Object.assign(params, { tid: item.tableName });
              } else {
                Object.assign(params, { table: item.tableName });
              }
              const res = await getTableFieldByName(params);
              const fields = _.map(res.data.columns, item => {
                return item.columnName;
              });
              this.tableField[item.fieldIndex] = fields;
              // spl参数
              const sqlParams = _.map(res.data.sqlParams, item => {
                return item.paramName;
              });
              this.tableField[item.fieldIndex + '_sqlParams'] = sqlParams;
            }
            const query = {};
            _.map(this.query, (value, key) => {
              const index = this.tableField[item.fieldIndex + '_sqlParams'].findIndex(field => field == key);
              if (index != -1) {
                if (!!value) {
                  query[key] = value;
                }
              }
            });

            const res = await getDBData({ table: item.fieldIndex, ...query });
            const dataSetListTemp = res.data.dataSetList;
            valueList = JSON.parse(JSON.stringify(dataSetListTemp[0].valueList)); // 变量集合
            this.dataSetList[dataSetListTemp[0].dataSetId] = JSON.parse(JSON.stringify(dataSetListTemp[0].valueList));
          }

          // TODO 获取新增数据
          if (
            !!this.addData[this.sheetIndex] &&
            !!this.addData[this.sheetIndex][item.fieldIndex] &&
            this.addData[this.sheetIndex][item.fieldIndex].length > 0
          ) {
            valueList.push(...this.addData[this.sheetIndex][item.fieldIndex]);
          }
          const len = valueList.length; // 长度
          // 获取填充数据
          const tempList = [];
          const cellColumnIndex = typeof extendInfo.row[rowIndex] == 'undefined' ? columnIndex : columnIndex + extendInfo.row[rowIndex].count;
          const cellRowIndex = typeof extendInfo.column[columnIndex] == 'undefined' ? rowIndex : rowIndex + extendInfo.column[columnIndex].count;
          const tempListIndexValue = {};
          _.map(valueList, (value, index) => {
            // 记录主键值
            const cdi = [];
            if (item.conditions.length > 0) {
              const cdiTemp = _.map(item.conditions[0], cond => {
                return {
                  fieldName: cond,
                  fieldValue: value[cond]
                }
              });
              cdi.push(...cdiTemp);
            }
            if (index != 0) {
              const template = !!cells[cellRowIndex] && !!cells[cellRowIndex][cellColumnIndex] ? JSON.parse(JSON.stringify(cells[cellRowIndex][cellColumnIndex])) : {};
              Object.assign(template, { t: item.fieldIndex });
              if (!item.noReplace) {
                Object.assign(template, { v: value[item.field] });
              }
              
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
                  Object.assign(template, { v: [] });
                }
              }
              if (cdi.length > 0) {
                Object.assign(template, { cdi });
              }
              Object.assign(template, { i: value['id'] });
              tempList.push(template);
            } else {
              // 首个单元格
              Object.assign(tempListIndexValue, { t: item.fieldIndex, i: value['id'] });
              // 非跟随扩展修改v
              if (!item.noReplace) {
                Object.assign(tempListIndexValue, { v: value[item.field] + '' });
              }
              if (cdi.length > 0) {
                Object.assign(tempListIndexValue, { cdi });
              }
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

          // 替换第一行数据
          const temp = JSON.parse(JSON.stringify(cells[cellRowIndex][cellColumnIndex]));
          Object.assign(temp, tempListIndexValue);
          if (typeof temp.c != 'undefined' && (temp.c == 'checkbox' || temp.c == 'selectMultiple')) {
            if (typeof temp.v != 'undefined') {
              Object.assign(temp, { v: !(temp.v instanceof Array) ? [temp.v + ''] : temp.v });
            } else {
              Object.assign(temp, { v: [] })
            }
          }
          cells[cellRowIndex].splice(cellColumnIndex, 1, temp);
          // 隐藏，禁用，有些单元格是取后端返回值的，这时就要获取第一行数据
          if (disabledList2.length > 0) {
            // 值替换
            for (let hdlI = 0; hdlI < disabledList2.length; hdlI++) {
              for (let hpi = 0; hpi < disabledList2[hdlI].pos2.length; hpi++) {
                const hp = disabledList2[hdlI].pos2[hpi];
                if (hp.rowIndex == cellRowIndex && hp.columnIndex == cellColumnIndex) {
                  Object.assign(disabledList2[hdlI], {
                    expression: disabledList2[hdlI].expression.replace(disabledList2[hdlI].cellItem[hpi], temp.v),
                  })
                }
              }
            }

            // 隐藏单元格 
            _.map(disabledList2, hdl => {
              if (!(/\$|\{|\}/g.test(hdl.expression))) {
                const ifStart = this.getResult(hdl.expression);
                const posD = hdl.pos;
                if (!ifStart) {
                  const cellD = cells[posD.start.rowIndex][posD.start.columnIndex];
                  // console.warn('disabledList2');
                  if (!!cellD) {
                    const cellDTemp = JSON.parse(JSON.stringify(cellD))
                    const p = cellDTemp.p;
                    const r = p.r;
                    r.w = !r.w;
                    Object.assign(p, { r });
                    Object.assign(cellDTemp, { p });
                    cells[posD.start.rowIndex].splice(posD.start.columnIndex, 1, cellDTemp);
                  }
                }
              }
            });
          }
          if (hiddenList2.length > 0) {
            // 值替换
            for (let hdlI = 0; hdlI < hiddenList2.length; hdlI++) {
              for (let hpi = 0; hpi < hiddenList2[hdlI].pos2.length; hpi++) {
                const hp = hiddenList2[hdlI].pos2[hpi];
                if (hp.rowIndex == cellRowIndex && hp.columnIndex == cellColumnIndex) {
                  Object.assign(hiddenList2[hdlI], {
                    expression: hiddenList2[hdlI].expression.replace(hiddenList2[hdlI].cellItem[hpi], temp.v),
                  })
                }
              }
            }

            // 隐藏单元格 
            _.map(hiddenList2, hdl => {
              if (!(/\$|\{|\}/g.test(hdl.expression))) {
                const ifStart = this.getResult(hdl.expression);
                const posH = hdl.pos;
                if (!ifStart) {
                  const cellH = cells[posH.start.rowIndex][posH.start.columnIndex];
                  if (!!cellH) {
                    const cellHTemp = JSON.parse(JSON.stringify(cellH));
                    const p = cellHTemp.p;
                    const r = p.r;
                    r.s = !r.s;
                    if (!r.s) {
                      cells[posH.start.rowIndex].splice(posH.start.columnIndex, 1, null);
                    }
                  }
                }
              }
            });
          }     

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
        this.extendInfo[this.sheetIndex] = extendInfo;
      }

      console.warn('cells', JSON.parse(JSON.stringify(cells)));
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
    },
    async update(state) {
        this.previewData = state.previewData;
        this.ifPreview = state.previewData.ifPreview;
        // 自定动态变量
        if (!!state.previewData.query && Object.keys(state.previewData.query).length > 0) {
          Object.assign(this.query, state.previewData.query);
          if (Object.keys(state.previewData.query).length > 0) {
            this.ifEdit = true;
          } else {
            this.ifEdit = false;
          }
          let index = 4;
          const _this = this;
          _.map(state.previewData.query, (value, key) => {
            _this.constants['key_' + index] = '${' + key + '}';
            _this.constants['value_' + index] = value;
            index += 1;
          });
        }
        // sheet 数据集
        const sheetData = state.previewData.data;
        if (!!sheetData && sheetData.length > 0) {
          for (let i = 0; i < sheetData.length; i++) {            
            const temp = await this.formatCellData(
              sheetData[i].cells,
              sheetData[i].formList,
              { end: sheetData[i].end, start: sheetData[i].start || 'A1' },
              sheetData[i].searchList,
              sheetData[i].dataList
            );

            // 删除空单元格
            _.map(temp.cells, (item, key) => {
              const ifDel = item.every(cell => !cell);
              if (ifDel) {
                delete temp.cells[key];
              } else {
                const tempCells = item.filter(cell => !!cell);
                let ifCellDel = false;
                const noDelKey = {};
                _.map(tempCells, tempCell => {
                  if (typeof tempCell.c != 'undefined' && (typeof tempCell.v == 'undefined' || !tempCell.v) && typeof tempCell.c == 'Cell') {
                    ifCellDel = true;
                  } else {
                    ifCellDel = false;
                    noDelKey[key] = false;
                  }
                });
                if (ifCellDel) {
                  if (typeof noDelKey[key] == 'undefined') {
                    delete temp.cells[key];
                  }
                }
              }
            });


            let columnIndex = null;
            let rowIndex = null;
            const cellsLen = Object.keys(temp.cells).sort((a, b) => b - a)[0];
            if (sheetData[i].end && false) {
              columnIndex = sheetData[i].end.replace(/[^a-zA-Z]/g,'');
              rowIndex = sheetData[i].end.replace(/[^0-9]/g,'');
              columnIndex = _.$ABC2Number(columnIndex) + 1;
              // rowIndex = rowIndex - 1;
            } else {
              // 如果没设，则默认最小单元格，
              const key = Object.keys(temp.cells).sort((a, b) => temp.cells[b].length - temp.cells[a].length)[0];
              const rowLen = cellsLen || Object.keys(temp.cells).length; // temp.rows.length;
              const colLen = rowLen != 0 ? temp.cells[key].length : rowLen; // temp.columns.length;
              rowIndex = parseInt(rowLen) + 1; // rowLen < 20 ? 200 : rowLen;
              columnIndex = colLen; // colLen < 20 ? 20 : colLen;
            }

            let colLen = _.map(temp.cells, item => item.length).sort((a, b) => b - a)[0];
            // colLen = colLen < 20 ? 20 : colLen;
            let screenW = 0;
            for (let i = 0; i < colLen; i++) {
              if (!!temp.columns[i]) {
                screenW += temp.columns[i].wpx;
              } else {
                screenW += 64;
              }
            }
            let screenH = 0;
            for (let i = 0; i <= cellsLen; i++) {
              if (!!temp.rows[i]) {
                screenH += temp.rows[i].hpx;
              } else {
                screenH += 19;
              }
            }

            Object.assign(temp, {
              title: sheetData[i].title,
              rowCount: rowIndex || 200,
              columnCount: columnIndex  || 20,
              maxRowCount: rowIndex || 100000,
              maxColumnCount: columnIndex || 200,
              freezeColumn: sheetData[i].freezeColumn, // 冻结行列
              freezeRow: sheetData[i].freezeRow, // 冻结行列
            });
            const dataTemp = JSON.parse(JSON.stringify(testData[0].data));
            Object.assign(dataTemp, JSON.parse(JSON.stringify(temp)));
            const sheetDataTemp = {
              data: dataTemp,
              info: {
                pos: sheetData[i].pos || 'center', // 布局
                screenW, // 宽度
                screenH, // 高度
                formList: sheetData[i].formList, // 权限规则
                searchList: sheetData[i].searchList, // 搜索规则
                dataList: sheetData[i].dataList, // 回写规则
              }
            };
            if (typeof this.data[i] != 'undefined') {
              this.data.splice(i, 1, sheetDataTemp);
            } else {
              this.data.push(sheetDataTemp);
            }
            // this.addData[i] = {};
          }
        }
        document.title = state.previewData.name || '未命名表单';
        this.$nextTick(() => {
          if (Object.keys(this.$refs).length == 0) {
            console.log('this.$refs', 0);
            return;
          }
          if (this.data.length == 0) {
            console.log('this.data', 0);
            return;
          }
          this.$curSheet().maxWidth = this.data[this.sheetIndex.substring(1)].info.screenW;
          this.$curSheet().maxHeight = this.data[this.sheetIndex.substring(1)].info.screenH;
          this.$watch('screenWidth', function() {
            this.$curSheet().maxWidth = this.data[this.sheetIndex.substring(1)].info.screenW;
            this.$curSheet().maxHeight= this.data[this.sheetIndex.substring(1)].info.screenH;
          });
        });
    },
  },
}
</script>

<style lang="scss">
.preview {
  // height: 100vh;
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

  .mainPreview .el-tabs--border-card > .el-tabs__content {
      padding: 0px !important;
  }

  /* .mainPreview .el-tabs--border-card > .el-tabs__header > .el-tabs__nav-wrap {
      width: calc(100vw - 30px) !important;
  } */

  .mainPreview .el-tabs--border-card {
      box-shadow: unset !important;
  }


  .mainPreview .el-tabs--border-card > .el-tabs__header.is-bottom {
      margin: unset !important;
  }

  .mainPreview .el-tabs--border-card > .el-tabs__header > .el-tabs__nav-wrap > .el-tabs__nav-scroll > .el-tabs__nav > .el-tabs__item.is-bottom.is-closable{
      border-right-color: #dcdfe6;
  }
</style>