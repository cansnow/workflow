<template>
  <div class="preview">
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
import { formatData } from '@/views/editManage/execlEditorPanel/src/utils';
import { saveFormData } from '@/api/editManage';
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
    };
  },
  computed: {
    $curSheet() {
      return this.$refs['sheet_' + this.index];
    },
    ...mapGetters(["name"]),
  },
  mounted() {
    this.init();
    const _this = this;
    this.$refs['sheet_' + this.index].$on('clikcCellBtn', function(res) {
      if (_this.ifPreview) {
        return;
      }
      console.log('clickCellBtn', res);
      console.log('_this.previewData.dataList', _this.previewData.dataList);
      // 提交数据
      if (res.p.t == 'submit') {
        // 根据回写规则提交数据
        if (_this.previewData.dataList && _this.previewData.dataList.length > 0) {
          const dataList = _this.previewData.dataList;
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
                  fieldValue = _this[fObj.value] || '';
                }
  
                fields.push({
                  fieldName: fObj.filed, // 字段名
                  fieldValue, // 字段值
                });
              });
              Object.assign(temp, { fields });
            }
            console.log('temp', temp);
            saveFormData(temp).then((res) => {
              console.log('saveFormData', res);
            });
          });
        }
      } else {
        _this.update(_this.$piniastore.$state);
      }
    });
  },
  methods: {
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
    update(state) {
        this.previewData = state.previewData;
        const tempData = JSON.parse(JSON.stringify(testData[0].data));
        this.ifPreview = state.previewData.ifPreview;
        const temp = formatData(state.previewData.cells);
        // TODO 权限规则
        if (state.previewData.formList && state.previewData.formList.length > 0) {
          // temp.cells
          _.map(state.previewData.formList, item => {
            const list = item.range.split(':');
            // G9
            if (list.length <= 1) {
              const data = this.formatData(list[0]);
              if (item.type == 'hide') {
                temp.cells[data.rowIndex].splice(data.columnIndex, 1, null);
              }
            }
            // G9:G10
            if (list.length > 1) {
              const start = this.formatData(list[0]);
              const end = this.formatData(list[1]);
              for (let i = start.rowIndex; i <= end.rowIndex; i++) {
                for(let j = start.columnIndex; j <= end.columnIndex; j++) {
                  if (item.type == 'hide') {
                    temp.cells[i].splice(j, 1, null);
                  }
                }
              }
            }
          });
        }
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
  width: 60vw;
  margin: auto;
  // border: 1px solid #ddd;

  &_title {
    display: flex; 
    align-items: center;
    justify-content: center;
    height: 40px;
    // border-bottom: 1px solid #ddd;
  }
  .meg-gdlinewrap-column {
    .meg-gdline {
        border-right: unset !important;
    }
  }

  .meg-gdlinewrap-row {
      flex-direction: column;

      .meg-gdline {
          border-bottom: unset !important;
      }
  }
}  
</style>