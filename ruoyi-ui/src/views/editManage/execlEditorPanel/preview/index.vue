<template>
  <div>
    <PreviewDiv />
  </div>
</template>

<script>
import PreviewDiv from '@/components/exceleditor/components/preview/index.vue';
import testData from './testData';
import { formatData } from '../src/utils';

export default {
  name: 'Preview',
  components: {
    PreviewDiv
  },
  created: function() {
    let temp = formatData(testData);
    const data = this.$piniastore.testData2;
    const cells = data.cells;
    if (cells.length > 0) {
      temp = formatData(cells);
      /** 计算行列 */

      // start: data.start, // TODO 开始行
      let columnIndex = null;
      let rowIndex = null;
      if (data.end) {
        columnIndex = data.end.replace(/[^a-zA-Z]/g,'');
        rowIndex = data.end.replace(/[^0-9]/g,'');
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
        title: data.title,
        rowCount: rowIndex || 200,
        columnCount: columnIndex  || 20,
        maxRowCount: rowIndex || 100000,
        maxColumnCount: columnIndex || 200,
        freezeColumn: data.freezeColumn, // 冻结行列
        freezeRow: data.freezeRow, // 冻结行列
      });
    }
		this.$piniastore.setPreviewData(temp);
	}
}
</script>
