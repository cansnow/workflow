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
export default {
  components: { Sheet },
  data() {
    return {
      data: testData[0].data,
      index: 0,
      title: testData[0].title,
      previewData: {},
    };
  },
  mounted() {
    this.init();
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
    update(state) {
        this.previewData = state.previewData;
        const tempData = JSON.parse(JSON.stringify(testData[0].data));
        const temp = formatData(state.previewData.cells);
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