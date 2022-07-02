<template>
  <div>
    <PreviewDiv />
  </div>
</template>

<script>
import PreviewDiv from '@/components/exceleditor/components/preview/index.vue';
import testData from './testData';
/** 格式化数据 */
function formatData(data) {
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

  let pos = -1;

  const cellPro = ['s', 'fs', 'f', 'v', 'c', 'p', 'options'];
  const stylePro = [
    'border',
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
  ];
  
  _.each(data, item => {
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
    if (typeof(cells[pos]) == 'undefined') {
      const cellList = [];
      /** 判断null */
      if (item.pos.start.columnIndex != 0) {
        const nullLen = item.pos.start.columnIndex;
        for (let i = 0; i < nullLen; i++) {
          cellList.push(null);
        }
      }
      cellList.push(temp);
      cells[pos] = cellList;
    } else {
      /** 判断null */
      if (item.pos.start.columnIndex > cells[pos].length) {
        const nullLen = item.pos.start.columnIndex - cells[pos].length;
        for (let i = 0; i < nullLen; i++) {
          cells[pos].push(null);
        }
      }
      cells[pos].push(temp);
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
  
  console.log('rows', rows);
  console.log('columns', columns);
  console.log('merges', merges);
  console.log('cells', cells);
  console.log('styles', styles);

  return {
    rows,
    columns,
    merges,
    cells,
    styles,
  };
};
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
      let columnIndex = 20;
      let rowIndex = 20;
      if (data.end) {
        columnIndex = data.end.replace(/[^a-zA-Z]/g,'');
        rowIndex = data.end.replace(/[^0-9]/g,'');
        columnIndex = _.$ABC2Number(columnIndex);
        rowIndex = rowIndex - 1;
      }
      Object.assign(temp, {
        title: data.title,
        rowCount: rowIndex || 20,
        columnCount: columnIndex  || 20,
        maxRowCount: rowIndex || 20,
        maxColumnCount: columnIndex || 20,
        freezeColumn: 0, // 冻结行列
        freezeRow: 0, // 冻结行列
      });
      debugger;
    }
		this.$piniastore.setPreviewData(temp);
	}
}
</script>
