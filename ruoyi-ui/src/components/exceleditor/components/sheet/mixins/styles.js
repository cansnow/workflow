import _ from 'lodash';

import { STYLE_CONFIG } from '../../../config/constant';
import { castObjectCell } from './cell/cellUtil';
import Style from './style/Style';

export default {
    mounted() {
        //监听格式刷事件
        this.$on('select-over', this.applyFormatPainter);
    },
    methods: {
        //格式刷
        applyFormatPainter() {
            if (this.curAction === 'formatpainter') {

                this.recordChange('copy', {
                    sArea: this.selectionCopy,
                    tArea: this.selctionExpand,
                });

                this.fillAreaCells(this.selectionCopy, this.getAreaCells(this.selectionCopy), this.selctionExpand, (sCell, option) => {
                    let tCell = this.getPosCell(option);
                    const s = _.get(sCell, 's');
                    if (s) {
                        tCell = castObjectCell(tCell);
                        tCell.s = s;
                    }
                    return tCell;
                });

                this.copyToAreaMerges(this.selectionCopy, this.selctionExpand);
                this.stopAction();
            }
        },
        getCellStylesCount() {
            const styleCount = {};
            _.each(this.cells, row => {
                _.each(row, cell => {
                    if (cell && cell.s) {
                        styleCount[cell.s] = (styleCount[cell.s] || 0) + 1;
                    }
                });
            });
            return styleCount;
        },

        getRCStylesCount() {
            const styleCount = {};
            _.each(this.rows, row => {
                if (row && row.s) {
                    styleCount[row.s] = (styleCount[row.s] || 0) + 1;
                }
            });
            _.each(this.columns, column => {
                if (column && column.s) {
                    styleCount[column.s] = (styleCount[column.s] || 0) + 1;
                }
            });
            return styleCount;
        },

        setAreaStyle(area, iteratee) {
            let addCell = false;

            this.stopAction();
            this.openComboRecord();

            if (this.isSelectRow && this.isSelectColumn) {
                this.s_setSheetStyle(area, iteratee);
            }
            else if (this.isSelectColumn) {
                this.s_setColumnsStyle(area, iteratee);
            }
            else if (this.isSelectRow) {
                this.s_setRowsStyle(area, iteratee);
            }
            else {
                addCell = true;
            }

            this.s_setCellsStyle(area, addCell, iteratee);

            this.closeComboRecord();
        },
        s_setSheetStyle(area, iteratee) {

        },
        s_setColumnsStyle(area, iteratee) {
            this.recordChange('RCStyleChange', {
                area,
                type: 'column',
            });

            const stylesCount = this.getRCStylesCount();

            const columnInfos = _.$mapRange(area.start.columnIndex, area.end.columnIndex, index => {
                return _.$getArrayItem(this.columns, index, { s: undefined });
            });

            const groupStyle = _.groupBy(columnInfos, 's');

            this.s_setStyle(groupStyle, stylesCount, 'RCStyles', iteratee, (column, style) => {
                this.$set(column, 's', style.id);
            });
        },
        s_setRowsStyle(area, iteratee) {
            this.recordChange('RCStyleChange', {
                area,
                type: 'row',
            });
            const stylesCount = this.getRCStylesCount();

            const rowInfos = _.$mapRange(area.start.rowIndex, area.end.rowIndex, index => {
                return _.$getArrayItem(this.rows, index, { s: undefined });
            });

            const groupStyle = _.groupBy(rowInfos, 's');

            this.s_setStyle(groupStyle, stylesCount, 'RCStyles', iteratee, (row, style) => {
                this.$set(row, 's', style.id);
            });
        },
        s_setCellsStyle(area, addCell, iteratee) {
            this.recordChange('styleChange', {
                area,
            });

            if (addCell) {
                this.c_addCells(area);
            }

            const stylesCount = this.getCellStylesCount();

            let cells = this.getAreaExistCells(area);

            if (!addCell) {
                cells = _.filter(cells, cell => cell.options.s);
            }

            const groupStyle = _.chain(cells).map(cell => {
                if (cell && cell.options) {
                    Object.assign(cell, { s: cell.options.s });
                }
                return cell;
            }).groupBy('s').value();
            this.s_setStyle(groupStyle, stylesCount, 'styles', iteratee, (cell, style) => {
                this.setCellAttribute(cell.pos, cell.options, 's', style.id);
            });
        },
        s_getTargetPos(pos, type) {
            const targetPos = {};
            if (type == 't') {
                Object.assign(targetPos, pos, { rowIndex: pos.rowIndex - 1 });
            }
            if (type == 'b') {
                Object.assign(targetPos, pos, { rowIndex: pos.rowIndex + 1 });
            }
            if (type == 'l') {
                Object.assign(targetPos, pos, { columnIndex: pos.columnIndex - 1 });
            }
            if (type == 'r') {
                Object.assign(targetPos, pos, { columnIndex: pos.columnIndex + 1 });
            }
            return targetPos;
        },
        // 去除边框重叠
        s_removeBorderOverlap(curPos, type) {
            // 1. 获取上下左右单元格样式 columnIndex rowIndex
            // 2. 判断是否有边框
            // 3. 判断是否有重叠
            // 4. 修改重叠单元样式
            const types = { t: 'b', b: 't', l: 'r', r: 'l' };
            const pos = JSON.parse(JSON.stringify(curPos));
            const style = this.s_ifBorderByType(pos, type);
            if (!!style) {
                const option = JSON.parse(JSON.stringify(style.option));
                option.border = style.option.border.replace(types[type], '');
                style.setOption(option);
            }
        },
        s_ifBorderByType(pos, type) {
            const types = { t: 'b', b: 't', l: 'r', r: 'l' };
            const targetPos = this.s_getTargetPos(pos, type);
            const cell = this.getPosCell(targetPos);
            if (!!cell && typeof cell.s != 'undefined') {
                const cellStyle = this.getStyle(cell.s);
                if (!!cellStyle.option.border && cellStyle.option.border.indexOf(types[type]) != -1) {
                    return cellStyle;
                }
                return undefined;
            }
            return undefined;
        },

        removeBorder(style, pos) {
            if (style.option.border.indexOf('t') != -1) {
                this.s_removeBorderOverlap(pos, 't');
            }
            if (style.option.border.indexOf('b') != -1) {
                this.s_removeBorderOverlap(pos, 'b');
            }
            if (style.option.border.indexOf('l') != -1) {
                this.s_removeBorderOverlap(pos, 'l');
            }
            if (style.option.border.indexOf('r') != -1) {
                this.s_removeBorderOverlap(pos, 'r');
            }

            if (
                !!style.option.borderStyle ||
                !!style.option.borderColor ||
                !!style.option.borderBold
            ) {
                // 设置样式，设置颜色，设置粗细
                const option = JSON.parse(JSON.stringify(style.option));
                const topCellStyle = this.s_ifBorderByType(pos, 't');
                if (!!topCellStyle && style.option.border.indexOf('t') == -1) {
                    this.s_removeBorderOverlap(pos, 't');
                    option.border = option.border + 't';
                }
                const bottomCellStyle = this.s_ifBorderByType(pos, 'b');
                if (!!bottomCellStyle && style.option.border.indexOf('b') == -1) {
                    this.s_removeBorderOverlap(pos, 'b');
                    option.border = option.border + 'b';
                }
                const leftCellStyle = this.s_ifBorderByType(pos, 'l');
                if (!!leftCellStyle && style.option.border.indexOf('l') == -1) {
                    this.s_removeBorderOverlap(pos, 'l');
                    option.border = option.border + 'l';
                }
                const rightCellStyle = this.s_ifBorderByType(pos, 'r');
                if (!!rightCellStyle && style.option.border.indexOf('r') == -1) {
                    this.s_removeBorderOverlap(pos, 'r');
                    option.border = option.border + 'r';
                }
                style.setOption(option);
            }
        },

        s_setStyle(groupStyle, stylesCount, type, iteratee, setIteratee) {
            _.each(groupStyle, (cells, sid) => {
                _.each(cells, (cell, index) => {                  

                    let oldStyle = this.getStyle(sid, type);
                    let style;
                    if (oldStyle && index == 0) {
                        if (stylesCount[sid] == cells.length) {
                            iteratee(oldStyle);
                            if (!!oldStyle.option.border) {
                                this.removeBorder(oldStyle, cell.pos);
                            }
                            return;
                        }
                        style = oldStyle.clone();
                        iteratee(style);
                        if (!!oldStyle.option.border) {
                            this.removeBorder(oldStyle, cell.pos);
                        }
                        if (style.equals(oldStyle)) {
                            return;
                        }
                    } else {
                        style = (new Style(STYLE_CONFIG.styleOptions())).clone();
                        iteratee(style);
                    }
                    style = this.addStyle(style, type);
                    // 判断是否设置边框
                    if (!!style.option.border) {
                        this.removeBorder(style, cell.pos);
                    }
                    setIteratee(cell, style);
                });
            });
        },

        getRowStyle(index) {
            return this.getStyle(_.get(this.rows[index], 's'), 'RCStyles');
        },

        getColumnStyle(index) {
            return this.getStyle(_.get(this.columns[index], 's'), 'RCStyles');
        },

        getStyle(sid, type) {
            type = type || 'styles';
            return this[type][sid];
        },
        getUseStyle(sid, type) {
            return this.getStyle(sid, type) || Style.getDefault();
        },
        addStyle(style, type) {
            type = type || 'styles';
            const t = false; // this.findStyle(style, type);
            if (!t) {
                this.$set(this[type], style.id, style);
            }
            return t || style;
        },
        findStyle(style, type) {
            type = type || 'styles';
            return _.find(this[type], s => {
                return s.equals(style);
            });
        },
        mixinStyles(s, rowIndex, columnIndex) {
            let rs = _.filter(_.uniq([_.get(this.rows[rowIndex], 's'), _.get(this.columns[columnIndex], 's')]));

            if (_.isEmpty(rs)) {
                return s;
            }

            rs = _.map(rs, r => {
                return this.getStyle(r, 'RCStyles');
            });

            if (s) {
                rs.unshift(this.getStyle(s));
            }

            const newStyle = new Style(_.defaults({}, ..._.map(rs, 'option')));

            const style = this.findStyle(newStyle);
            if (style) {
                return style.id;
            }
            this.addStyle(newStyle);

            return newStyle.id;
        },
    },
};

