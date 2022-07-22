<template>
  <div class="preview" :style="pos == 'center' ? { width: '60vw' } : ''">
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
import { saveFormData, getDBData } from '@/api/editManage';
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
      dataSetList: [],
      pos: 'center',
    };
  },
  computed: {
    $curSheet() {
      return this.$refs['sheet_' + this.index];
    },
    ...mapGetters(["name"]),
  },
  async mounted() {
    const _this = this;
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
    
    this.$curSheet.$on('clikcCellBtn', function(res) {
      if (_this.ifPreview) {
        return;
      }
      console.log('clickCellBtn', res);
      console.log('_this.previewData.dataList', _this.previewData.dataList);
      // 提交数据
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
        }
        if (_this.previewData.dataList && _this.previewData.dataList.length > 0 && ifOK) {
          const dataList = _this.previewData.dataList;
          const responseData = [];
          _.map(dataList, (item, index) => {
            const temp = { table: item.title };
            if (item.filedList && item.filedList.length > 0) {
              const fields = [];
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
  
                fields.push({
                  fieldName: fObj.filed, // 字段名
                  fieldValue, // 字段值
                });
              });
              Object.assign(temp, { fields });
            }
            console.log('temp', temp);
            responseData.push(temp);
          });
          const resData = {};
          _.map(responseData, item => {
            if (typeof resData[item.table] == 'undefined') {
              const temp = [];
              temp.push(item.fields);
              resData[item.table] = temp;
            } else {
              resData[item.table].push(item.fields);
            }
          });
          const data = _.map(resData, (item, key) => {
            return {
              table: key,
              fields: item,
            }
          });
          saveFormData(data).then((res) => {
            console.log('saveFormData', res);
            _this.$modal.msgSuccess('提交成功！！！');
          });
        }
      } else {
        // 重置
        _this.update(_this.$piniastore.$state);
      }
    });
  },
  methods: {
    async formatCellData(data, formList) {
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
        let temp = {};
        // 将 s fs f v c p
        cellPro.forEach(pItem => {
          if (typeof(item[pItem]) != 'undefined') {
            temp[pItem] = item[pItem];
          }
        });
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
          selectOptionInfo.push({
            pos: item.pos, // 位置信息
            field: temp.p.api, // 变量
            apiLabel: temp.p.apiLabel || temp.p.apiValue,
            apiValue: temp.p.apiValue || temp.p.apiLabel,
          });
        }

        // 获取p.f 变量，替换v
        if (typeof temp.p != 'undefined' && typeof temp.p.f != 'undefined') {
          console.log('temp', temp);
          console.log('temp.p.f', temp.p.f);
          const fList = temp.p.f.split('.');
          console.log('fList', fList);
          const itemPost = item.pos;
          if (fList[0] == 'dataSetList') {
            extendList.push({
              pos: itemPost, // 位置信息
              fieldInfo: temp.p.f, // 变量信息
              fieldIndex: fList[1], // 数据集下标
              field: fList[2], // 变量
              extendType: typeof temp.p.e != 'undefined' ? temp.p.e : 'none', // 扩展方向
              merges: typeof(item.merges) != 'undefined', // 是否合并
            });
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
          if (typeof temp.p != 'undefined' && !temp.p.r.s) {
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
          const res = await getDBData({ table: item.field });
          const dataSetListTemp = res.data.dataSetList;
          const options = _.map(dataSetListTemp[0].valueList, value => {
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
        // _.map(selectOptionInfo, async (item) => {
        //   const res = await getDBData({ table: item.field });
        //   const dataSetListTemp = res.data.dataSetList;
        //   const options = _.map(dataSetListTemp[0].valueList, value => {
        //     return {
        //       label: value[item.apiLabel],
        //       value: value[item.apiValue],
        //     };
        //   });
        //   const cellsIndex = item.pos.start;
        //   const cell = JSON.parse(JSON.stringify(cells[cellsIndex.rowIndex][cellsIndex.columnIndex]));
        //   if (!!cell) {
        //     cell.options = options;
        //     cells[cellsIndex.rowIndex].splice(cellsIndex.columnIndex, 1, cell);
        //   }
        // });
      }

      // 设置扩展
      if (extendList.length > 0) {
        console.log('extendList', extendList);
        const extendInfo = {
          column: {}, // 列
          row: {},
        };
        for (let extendIndex = 0; extendIndex < extendList.length; extendIndex++) {
          const item = extendList[extendIndex];
          const rowIndex = item.pos.start.rowIndex;
          const columnIndex = item.pos.start.columnIndex;
          const res = await getDBData({ table: item.fieldIndex });
          const dataSetListTemp = res.data.dataSetList;
          const valueList = dataSetListTemp[0].valueList; // 变量集合
          const len = valueList.length; // 长度
          // 获取填充数据
          const tempList = [];
          const cellColumnIndex = typeof extendInfo.row[rowIndex] == 'undefined' ? columnIndex : columnIndex + extendInfo.row[rowIndex];
          const cellRowIndex = typeof extendInfo.column[columnIndex] == 'undefined' ? rowIndex : rowIndex + extendInfo.column[columnIndex];
          let tempListIndexValue = '';
          _.map(valueList, (value, index) => {
            if (index != 0) {
              const template = !!cells[cellRowIndex] && !!cells[cellRowIndex][cellColumnIndex] ? JSON.parse(JSON.stringify(cells[cellRowIndex][cellColumnIndex])) : {};
              Object.assign(template, { v: value[item.field]});
              tempList.push(template);
            } else {
              tempListIndexValue = value[item.field];
            }
          });

          const temp = JSON.parse(JSON.stringify(cells[rowIndex][columnIndex]));
          Object.assign(temp, { v: tempListIndexValue });
          cells[rowIndex].splice(columnIndex, 1, temp);

          // none 无 bottom 向下 right 向右
          if (item.extendType == 'bottom') {
            if (tempList.length > 0) {
              let posBom = cellRowIndex + 1; //cellRowIndex + 1;
              // 记录位置
              if (typeof extendInfo.column[columnIndex] == 'undefined') {
                extendInfo.column[columnIndex] = len - 1;
              } else {
                // posBom += extendInfo.column[columnIndex]; // 将前面加过的位置加上
                extendInfo.column[columnIndex] += len - 1; // 加上现在的扩展长度
              }
              const tempReplace = []; // 记录替换的数据，后面加回去
              _.map(tempList, (item, index) => {
                // 长度不对
                const cellsLen = Object.keys(cells).sort((a, b) => b - a)[0];
                const ifCell = !!cells[posBom + index];
                const temp = ifCell && !!cells[posBom + index][columnIndex] ? JSON.parse(JSON.stringify(cells[posBom + index][columnIndex])) : null;
                tempReplace.push(temp); // 记录数据
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
              let pos = columnIndex + 1; // columnIndex + 1;
              // 记录位置
              if (typeof extendInfo.row[rowIndex] == 'undefined') {
                extendInfo.row[rowIndex] = len - 1;
              } else {
                pos += extendInfo.row[rowIndex]; // 将前面加过的位置加上
                extendInfo.row[rowIndex] += len - 1; // 加上现在的扩展长度
              }
              if (!!cells[cellRowIndex]) {
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
            }
          }
        }
        // _.map(extendList, async (item) => {
        //   const rowIndex = item.pos.start.rowIndex;
        //   const columnIndex = item.pos.start.columnIndex;
        //   const res = await getDBData({ table: item.fieldIndex });
        //   const dataSetListTemp = res.data.dataSetList;
        //   const valueList = dataSetListTemp[0].valueList; // 变量集合
        //   const len = valueList.length; // 长度
        //   // 获取填充数据
        //   const tempList = [];
        //   const cellColumnIndex = typeof extendInfo.row[rowIndex] == 'undefined' ? columnIndex : columnIndex + extendInfo.row[rowIndex];
        //   const cellRowIndex = typeof extendInfo.column[columnIndex] == 'undefined' ? rowIndex : rowIndex + extendInfo.column[columnIndex];
        //   let tempListIndexValue = '';
        //   _.map(valueList, (value, index) => {
        //     if (index != 0) {
        //       const template = !!cells[cellRowIndex] && !!cells[cellRowIndex][cellColumnIndex] ? JSON.parse(JSON.stringify(cells[cellRowIndex][cellColumnIndex])) : {};
        //       Object.assign(template, { v: value[item.field]});
        //       tempList.push(template);
        //     } else {
        //       tempListIndexValue = value[item.field];
        //     }
        //   });

        //   const temp = JSON.parse(JSON.stringify(cells[rowIndex][columnIndex]));
        //   Object.assign(temp, { v: tempListIndexValue });
        //   cells[rowIndex].splice(columnIndex, 1, temp);

        //   // none 无 bottom 向下 right 向右
        //   if (item.extendType == 'bottom') {
        //     if (tempList.length > 0) {
        //       let posBom = cellRowIndex + 1; //cellRowIndex + 1;
        //       // 记录位置
        //       if (typeof extendInfo.column[columnIndex] == 'undefined') {
        //         extendInfo.column[columnIndex] = len - 1;
        //       } else {
        //         // posBom += extendInfo.column[columnIndex]; // 将前面加过的位置加上
        //         extendInfo.column[columnIndex] += len - 1; // 加上现在的扩展长度
        //       }
        //       const tempReplace = []; // 记录替换的数据，后面加回去
        //       _.map(tempList, (item, index) => {
        //         // 长度不对
        //         const cellsLen = Object.keys(cells).sort((a, b) => b - a)[0];
        //         const ifCell = !!cells[posBom + index];
        //         const temp = ifCell && !!cells[posBom + index][columnIndex] ? JSON.parse(JSON.stringify(cells[posBom + index][columnIndex])) : null;
        //         tempReplace.push(temp); // 记录数据
        //         if (ifCell) {
        //           if (columnIndex >= cells[posBom + index].length) {
        //             const nullLen = columnIndex - cells[posBom + index].length;
        //             for (let i = 0; i < nullLen; i++) {
        //               cells[posBom + index].push(null);
        //             }
        //             cells[posBom + index].push(item);
        //           } else {
        //             cells[posBom + index].splice(columnIndex, 1, item);
        //           }
        //           // 添加新增数据
        //           cells[parseInt(cellsLen) + 1] = [];
        //         } else {
        //           const cellList = [];
        //           const nullLen = columnIndex;
        //           for (let i = 0; i < nullLen; i++) {
        //             cellList.push(null);
        //           }
        //           cellList.push(item);
        //           cells[posBom + index] = cellList;
        //           if (posBom + index != parseInt(cellsLen) + 1) {
        //             cells[parseInt(cellsLen) + 1] = [];
        //           } else {
        //             cells[parseInt(cellsLen) + 2] = [];
        //           }
        //         }
        //         // 判断最后的下一行是否有值
        //         if (index == tempList.length - 1) {
        //           const ifNextCell = !!cells[posBom + tempList.length];
        //           if (!ifNextCell) {
        //             cells[posBom + tempList.length] = [];
        //           }
        //         }
        //       });
        //       // console.warn('tempReplace', tempReplace);
        //       // 数据向下移动
        //       let replaceIndex = 0;
        //       const cellkeys = Object.keys(cells).sort((a, b) => a - b);
        //       _.map(cellkeys, (key, index) => {
        //         if (key >= posBom + tempList.length) {
        //           // 判断是否为空
        //           if (columnIndex >= cells[key].length) {
        //             const nullLen = columnIndex - cells[key].length;
        //             for (let i = 0; i < nullLen; i++) {
        //               cells[key].push(null);
        //             }
        //             tempReplace.push(null); // 记录替换
        //             cells[key].push(tempReplace[replaceIndex]);
        //           } else {
        //             tempReplace.push(cells[key][columnIndex]); // 记录替换
        //             cells[key].splice(columnIndex, 1, tempReplace[replaceIndex]);
        //           }
        //           replaceIndex += 1;
        //           // 判断下一个节点是否有值，没有值的时候就要补充进去，防止错位
        //           if (!cells[parseInt(key) + 1]) {
        //             if (index < cellkeys.length -1 ) {
        //               const repairLen = cellkeys[index + 1] - (parseInt(key) + 1);
        //               for(let i = 0; i < repairLen; i++) {
        //                 const cellList = [];
        //                 for (let j = 0; j < columnIndex; j++) {
        //                   cellList.push(null);
        //                 }
        //                 tempReplace.push(null); // 记录替换
        //                 cellList.push(tempReplace[replaceIndex + i]);
        //                 cells[parseInt(key) + i + 1] = cellList;
        //               }
        //               replaceIndex += repairLen;
        //             }
        //           }
        //         }
        //       });

        //     }
        //   }
        //   if (item.extendType == 'right') {
        //     if (tempList.length > 0) {
        //       let pos = columnIndex + 1; // columnIndex + 1;
        //       // 记录位置
        //       if (typeof extendInfo.row[rowIndex] == 'undefined') {
        //         extendInfo.row[rowIndex] = len - 1;
        //       } else {
        //         pos += extendInfo.row[rowIndex]; // 将前面加过的位置加上
        //         extendInfo.row[rowIndex] += len - 1; // 加上现在的扩展长度
        //       }
        //       if (!!cells[cellRowIndex]) {
        //         cells[cellRowIndex].splice(pos, 0, ...tempList);
        //       } else {
        //         const cellList = [];
        //         const nullLen = pos;
        //         for (let i = 0; i < nullLen; i++) {
        //           cellList.push(null);
        //         }
        //         cellList.push(...tempList);
        //         cells[cellRowIndex] = cellList;
        //       }
        //     }
        //   }
        //   // console.log('tempList', tempList);
        //   console.log('extendInfo', extendInfo);
        //   console.warn('cells', cells);
        // });
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
              ifStart = this.comparison(value, comparison[0], comparison[1]);
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
          let index = 4;
          const _this = this;
          _.map(state.previewData.query, (value, key) => {
            _this.constants['key_' + index] = '${' + key + '}';
            _this.constants['value_' + index] = value;
            index += 1;
          });
        }

        this.pos = state.previewData.pos || 'center';

        const temp = await this.formatCellData(state.previewData.cells, state.previewData.formList);
        
        // start: data.start, 
        let columnIndex = null;
        let rowIndex = null;
        if (state.previewData.end) {
          columnIndex = state.previewData.end.replace(/[^a-zA-Z]/g,'');
          rowIndex = state.previewData.end.replace(/[^0-9]/g,'');
          columnIndex = _.$ABC2Number(columnIndex) + 1;
          // rowIndex = rowIndex - 1;
        } else {
          // 如果没设，则默认最小单元格，
          const rowLen = temp.rows.length;
          const colLen = temp.columns.length;
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
        Object.assign(tempData, temp);
        this.data = tempData;
        this.title = tempData.title || 'sheet';
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