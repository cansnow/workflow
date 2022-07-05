import _ from 'lodash';

export default {
  methods: {
    setFormatCell(pos, format) {
      this.cf_setFormatCell(pos, format);
    },
    cf_setFormatCell(pos, format) {
      let cell = this.getPosCell(pos);
      this.setCellAttribute(pos, cell, 'fc', format);
      if (typeof(cell) != 'undefined' && typeof(cell['v']) != 'undefined') {
        this.computedFormatCell(pos, cell.v);
      }
    },
    computedFormatCell(pos, val) {
      let cell = this.getPosCell(pos);
      this.setCellAttribute(pos, cell, 'fcv', val);
      // TODO 设置格式, 常规不做处理
      if (cell.fc != 'routine') {

      }
      this.c_setCellV(pos, val);
    },
  },
}
