<template>
    <div class="meg-gridrow"
        @mousedown.left.self="mouseDown"
        @drop.self="handleDrop"
        @dragover.self="handleDragover"
        @dragleave.self="handleDragleave"
        @contextmenu.self.prevent="showMenu"
    >
        <div v-for="cell in cells" :key="getKey(cell)" @click="cellClick">
            <GridCell v-show="!cell.isHide" :cell="cell"></GridCell>
            <div v-if="isShowBackgroudPorxy(cell)" class="meg-gridrow-pce" :style="getCss(cell.style.css)"></div>
        </div>

        <!--<div
      v-for="(cell,index) in cells"
      class="meg-gridcell"

      @mousedown.left="mouseDownCell($event,cell.columnIndex)"
      @contextmenu.prevent="showMenuCell($event,cell.columnIndex)"
      @dblclick="doEdit"

      :style="cell.style.css"
      :key="index">

      <div class="meg-cellval-warp">
        <div :class="['meg-cellval',cell.style.alignCss]">
          {{ formatValue(cell) }}
        </div>
      </div>
    </div>-->
    </div>
</template>

<script>
import _ from 'lodash';
import { isNullCell, getCellValue } from '../mixins/cell/cellUtil';
import GridCell from './GridCell.vue';

let cellKey = 0;

export default {
    name: 'GridRow',
    inject: ['$sheet', '$grid'],
    props: {
        row: Object,
        startColumnIndex: Number,
        endColumnIndex: Number
    },

    components: {
        GridCell
    },

    computed: {
        cells() {
            const rowStyle = this.$sheet.getRowStyle(this.row.rowIndex);
            const columnStyles = this.$parent.columnStyles;
            const columnInfos = this.$parent.columnInfos;

            const cells = _.map(this.row.cells, (cell, index) => {
                const columnStyle = columnStyles[index];

                const columnIndex = this.startColumnIndex + index;
                const option = cell.option;
                const isHide = _.get(columnInfos[index], 'h');
                const show = !isNullCell(option) || !_.isEmpty(rowStyle) || !_.isEmpty(columnStyle);

                const baseStyle = this.getBaseStyle(index, cell);
                const style = this.getCellStyle(option, baseStyle, cell.rowIndex === this.row.rowIndex ? rowStyle : null, cell.columnIndex === columnIndex ? columnStyle : null);

                return Object.assign(
                    {
                        merge: baseStyle.merge,
                        show,
                        style,
                        isHide,
                    },
                    cell
                );
            });

            return _.filter(cells, cell => {
                return this.isCellShow(cell);
            });
        }
    },

    methods: {
        cellClick() {
            //console.log(arguments);
        },
        isShowBackgroudPorxy(cell) {
            if (cell.merge && cell.style.css.backgroundColor == '#fff') {
                return false;
            }
            return !cell.isHide && cell.style.css.backgroundColor;
        },

        getCss(css) {
            const c = _.pick(css, ['left', 'top', 'height', 'width']);

            if (css.backgroundColor) {
                c.borderColor = css.backgroundColor;
            }

            return c;
        },

        getWidth(index) {
            return this.$grid.widths[index];
        },

        getLeft(index) {
            return this.$grid.lefts[index];
        },

        isCellShow(cell) {
            const start = this.$sheet.selection.start;
            return cell.show || (start.rowIndex == this.row.rowIndex && start.columnIndex == cell.columnIndex);
        },

        formatValue(cell) {
            const v = getCellValue(cell.option);
            return v;
        },

        getCellStyle(option, baseStyle, rowStyle, columnStyle) {
            const styleObj = this.$sheet.getUseStyle(_.get(option, 's'));
            const css = styleObj.getStyle(baseStyle, [rowStyle, columnStyle]);
            // 合并有边框时，设置宽高+1
            if (baseStyle.merge) {
                if (!!css['border-bottom']) {
                    Object.assign(css, { height: `${parseInt(css.height) + 1}px` });
                }

                if (!!css['border-right']) {
                    Object.assign(css, { width: `${parseInt(css.width) + 1}px` });
                }
            }
            return {
                css,
                alignCss: styleObj.getAlignment([rowStyle, columnStyle])
            };
        },

        getBaseStyle(index, cell) {
            const columnIndex = cell.columnIndex;
            const rowIndex = cell.rowIndex;
            const mergeInfo = this.$sheet.getMerge({
                rowIndex,
                columnIndex
            });

            const baseStyle = {
                left: 0
            };

            if (mergeInfo) {
                const size = this.$sheet.getAreaSize(mergeInfo);
                baseStyle.width = size.width - 1;
                baseStyle.height = size.height - 1;
                baseStyle.merge = true;
                if (rowIndex !== this.row.rowIndex) {
                    baseStyle.top = -this.$sheet.getGapHeight(rowIndex, this.row.rowIndex);
                }
                if (columnIndex !== index + this.startColumnIndex) {
                    baseStyle.left = -this.$sheet.getGapWidth(columnIndex, index + this.startColumnIndex);
                }
            } else {
                baseStyle.width = this.getWidth(index);
            }

            baseStyle.left += this.getLeft(index);

            return baseStyle;
        },

        mouseDown(event) {
            const cellIndex = this.findCellIndex(event.offsetX);
            const pos = {
                columnIndex: cellIndex + this.startColumnIndex,
                rowIndex: this.row.rowIndex
            };
            console.log('mouseDown', pos);
            this.$sheet.s_cellSelectStart(event, pos);
        },
        // 拖拽释放
        handleDrop(event) {
            const cellIndex = this.findCellIndex(event.offsetX);
            const pos = {
                columnIndex: cellIndex + this.startColumnIndex,
                rowIndex: this.row.rowIndex
            };
            console.log('handleDrop', pos, event);
            event.preventDefault();
        },
        // 拖拽到内部
        handleDragover(event) {
            event.preventDefault();
        },
        // 拖拽移出
        handleDragleave(event) {
            // console.log('handleDragleave', event);
        },

        showMenu(event) {
            const cellIndex = this.findCellIndex(event.offsetX);
            const pos = {
                columnIndex: cellIndex + this.startColumnIndex,
                rowIndex: this.row.rowIndex
            };
            this.$sheet.showCellMenu(pos, event.pageY, event.pageX);
        },

        doEdit() {
            this.$sheet.doEditCell();
        },

        findCellIndex(x) {
            const lefts = [0];
            const size = this.endColumnIndex - this.startColumnIndex;
            for (let i = 1; i < size + 1; i++) {
                lefts[i] = (lefts[i - 1] || 0) + this.getWidth(i - 1);
            }

            return _.findIndex(lefts, (left, index) => {
                let nextLeft = 9999999999;
                if (index < lefts.length - 1) {
                    nextLeft = lefts[index + 1];
                }
                if (x >= left && x <= nextLeft) {
                    return true;
                }
            });
        },

        getKey(cell, index) {
            if (cell.option) {
                if (cell.option.__key__) {
                    return cell.option.__key__;
                }

                Object.defineProperties(cell.option, {
                    __key__: {
                        value: ++cellKey
                    }
                });

                return cellKey;
            }

            return index;
        }
    }
};
</script>

<style lang="scss">
.meg-gridrow {
    position: absolute;
    width: 100%;
}

.meg-gridrow-pce {
    position: absolute;
    border: 1px solid transparent;
    min-height: 100%;
    margin: -1px;
}
</style>
