export default {
  // 行的信息
  rows: [],
  // 列的信息
  columns: [],
  // 合并单元格的信息
  merges: [],
  // 单元格信息
  cells: {},
  // 样式信息
  styles: {
      s2: {
          border: 'blrt',
          backgroundColor: '#ffff00',
          color: '#ff1400',
          fontFamily: '微软雅黑',
          fontSize: 12,
          fontStyle: 'italic',
          fontWeight: 'bold',
          textDecoration: 'underline',
          textAlign: 'center',
          verticalAlign: 'middle',
          whiteSpace: 'normal'
      },
      s3: {
          border: 'blrt',
          backgroundColor: '#ccc',
          color: '#ff1400',
          fontFamily: '微软雅黑',
          fontSize: 12,
          fontStyle: 'italic',
          fontWeight: 'bold',
          textDecoration: 'underline',
          textAlign: 'center',
          verticalAlign: 'middle',
          whiteSpace: 'normal'
      }
  },
  //行列样式
  RCStyles: {
      s1: {
          border: undefined,
          backgroundColor: undefined,
          color: '#ff1400',
          fontFamily: '微软雅黑',
          fontSize: 12,
          fontStyle: 'italic',
          fontWeight: 'bold',
          textDecoration: 'underline',
          textAlign: 'center',
          verticalAlign: 'middle',
          whiteSpace: 'normal'
      }
  },
  //行的数量
  rowCount: 200,
  //列的数量
  columnCount: 20,
  //允许的最大行
  maxRowCount: 10000,
  //允许的最大列
  maxColumnCount: 200,
  // 冻结行
  freezeColumn: 0,
  // 冻结列
  freezeRow: 0,
}
