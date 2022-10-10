/** 格式化数据 */
export function formatData(data, ifPreview = false) {
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
    'borderSrc',
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
      // // 是否可见
      if (ifPreview) {
        if (typeof temp.p != 'undefined' && !temp.p.r.s) {
          cellList.push(null);
        } else {
          cellList.push(temp);
        }
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
      if (ifPreview) {
        if (typeof temp.p != 'undefined' && !temp.p.r.s) {
           cells[pos].push(null);
        } else {
           cells[pos].push(temp);
        }
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

  return {
    rows,
    columns,
    merges,
    cells,
    styles,
  };
};
