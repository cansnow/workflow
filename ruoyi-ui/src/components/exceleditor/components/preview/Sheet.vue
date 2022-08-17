<template>
    <div class="meg-sheet" @scroll="l_scroll" :class="{ 'meg-sheet-formatpainter': curAction == 'formatpainter' }">

        <div class="sheet-content table" :style="{ top: scrollTop + 'px', left: scrollLeft + 'px' }">

            <!--第一行-->
            <div class="tr tr1" v-if="showHead">
                <div class="td td-empty">
                    <div class="table-empty">
                        {{ selectPost }}
                    </div>
                </div>
                <!--行头-->
                <div class="td table-colhead-wrap">
                    <Heads direction="column" ref="colHead" :scroll="scrollLeft" :fixed="fixedColumnIndex"
                        :compen="plusWidth" :flows="blockFlows.rb.columnFlows">
                    </Heads>
                </div>
            </div>

            <!--第二行-->
            <div class="tr tr2">

                <!--列头-->
                <div v-if="showHead" class="td table-rowhead-wrap">
                    <Heads direction="row" ref="rowHead" :scroll="scrollTop" :fixed="fixedRowIndex" :compen="plusHeight"
                        :flows="blockFlows.rb.rowFlows">
                    </Heads>
                </div>

                <!--数据区-->
                <div class="grid-content">

                    <!--上边数据区-->
                    <div class="grid-top" :style="{ height: fixedHeight + 'px' }">
                        <!--左上-->
                        <div class="grid-lt">
                            <Block v-if="fixedHeight && blockFlows.lt" key="lt" :rowFlows="blockFlows.lt.rowFlows"
                                :columnFlows="blockFlows.lt.columnFlows" :plusHeight="0" :plusWidth="0" />
                        </div>
                        <!--右上-->
                        <div class="grid-rt">
                            <Block v-if="fixedHeight && blockFlows.rt" key="rt" :rowFlows="blockFlows.rt.rowFlows"
                                :columnFlows="blockFlows.rt.columnFlows" :plusHeight="0" :plusWidth="plusWidth" />
                            <div class="grdusedrange" :style="{ top: `1px`, left: `${maxWidth}px` }"></div>
                        </div>
                    </div>

                    <!--下边数据区-->
                    <div class="grid-bottom">

                        <!--左下-->
                        <div class="grid-lb">
                            <Block v-if="blockFlows.lb" key="lb" :rowFlows="blockFlows.lb.rowFlows"
                                :columnFlows="blockFlows.lb.columnFlows" :plusHeight="plusHeight" :plusWidth="0" />
                            <div class="grdusedrange" :style="{ top: `${maxHeight}px`, left: `1px` }"></div>
                        </div>

                        <!--右下-->
                        <div class="grid-rb">
                            <Block key="rb" :rowFlows="blockFlows.rb.rowFlows" :columnFlows="blockFlows.rb.columnFlows"
                                :plusHeight="plusHeight" :plusWidth="plusWidth" />
                            <div class="grdusedrange" :style="{ top: `${maxHeight}px`, left: `${maxWidth}px` }"></div>
                        </div>

                    </div>

                    <div v-for="(style, i) in resizeLineStyle" :class="resizeClass" :style="style" :key="i"></div>
                </div>
            </div>

        </div>

        <div class="grdusedrange" :style="{ top: `${sheetHeight}px`, left: `${sheetWidth}px` }"></div>

        <Context-Menu v-model="contextMenuState" @click-item="clickItem" :items="curMenuItems" :style="contextMenuPos">
        </Context-Menu>

    </div>
</template>
<script>
import { mixins } from './mixins';//'../sheet/mixins';
import Block from './block/Block.vue';
import Heads from '../head/Head.vue';
import Grid from './grid/Grid.vue';
import FlowClip from './flow/FlowClip.vue';
import Flow from './flow/Flow.vue';

import Style from './mixins/style/Style';

function parseStyle(styles) {
    let result = {};
    for (let id in styles) {
        let style = styles[id];
        if (style instanceof Style) {
            result[id] = style;
        } else {
            result[id] = new Style(style, id);
        }
    }
    return result;
}

function parseStyle1(styles) {
    let result = {};
    for (let id in styles) {
        result[id] = styles[id].option;
    }
    return result;
}

function parseMerges(merges) {
    let result = {};
    merges.forEach(merge => {
        result[`${merge.start.rowIndex}_${merge.start.columnIndex}`] = merge;
    });
    return result;
}


export default {
    /***
     * 将$sheet属性作为sheet的实例，注入到后代组件中
     */
    provide() {
        return {
            $sheet: this,
        };
    },
    components: {
        Heads,
        Grid,
        FlowClip,
        Flow,
        Block,
    },
    props: {
        sheetIndex:{
            type:[Number,String],
            default:0
        },
        options:{
            type:Object,
            default(){
                return {};
            }
        },
        autoCreate: {
            type: Boolean,
            default: false,
        },
    },
    mixins,
    data() {
        console.log(this.options);
        return {
            optionsTemp: this.options,
            // 行的信息
            rows: undefined,
            // 列的信息
            columns: undefined,
            // 合并单元格的信息
            merges: undefined,
            // 单元格信息
            cells: undefined,
            // 样式信息
            styles: undefined,
            //行列样式
            RCStyles: undefined,
            //行的数量
            rowCount: 200,
            //列的数量
            columnCount: 20,
            //允许的最大行
            maxRowCount: 100000,
            //允许的最大列
            maxColumnCount: 200,
            // 冻结行
            freezeColumn: 0,
            // 冻结列
            freezeRow: 0,
            showHead: false,
        };
    },
    computed: {
        relMaxRowCount() {
            return this.autoCreate ? this.maxRowCount : this.rowCount;
        },
        relMaxColumnCount() {
            return this.autoCreate ? this.maxColumnCount : this.columnCount;
        },
    },
    watch: {
        options(options){
            console.log('options', options);
            this.init(options);
        },
        optionsTemp: {
            handler: function(options) {
                console.log('optionsTemp', options);
                this.init(options);
            },
            immediate: true,
        },
        freezeColumn(columnIndex) {
            this.freezeWindow(this.freezeRow, columnIndex);
        },
        freezeRow(rowIndex) {
            this.freezeWindow(rowIndex, this.freezeColumn);
        },
    },
    methods: {
        init(options) {
            this.rows = options.rows || [];
            this.columns = options.columns || [];
            this.merges = parseMerges(options.merges);
            this.cells = options.cells || {};
            this.styles = parseStyle(options.styles || {});
            this.RCStyles = parseStyle(options.RCStyles || {});
            this.rowCount = options.rowCount || 200;
            this.columnCount = options.columnCount || 20;
            this.maxRowCount = options.maxRowCount || 10000;
            this.maxColumnCount = options.maxColumnCount || 200;
            this.freezeColumn = options.freezeColumn || 0;
            this.freezeRow = options.freezeRow || 0;
            // 设置
            this.freezeWindow(this.freezeRow, this.freezeColumn);
        },
        setFreezeColumn(columnIndex) {
            this.freezeColumn = columnIndex;
        },
        setFreezeRow(rowIndex) {
            this.freezeRow = rowIndex;
        },
    },
};
</script>
<style lang="scss">
.meg-sheet {
    position: relative;
    overflow: auto;

    &:focus {
        outline: none;
    }

    .sheet-content {
        position: absolute;
        top: 0px;
        left: 0px;
    }

}

.grdusedrange {
    position: absolute;
    visibility: hidden;
    width: 1px;
    height: 1px;
}

@import "../../style/style";

.table {
    width: 100%;
    height: 100%;
    flex-direction: column;
    user-select: none;
}

.table,
.tr {
    display: flex;
}

.tr {
    max-height: 100%;
    overflow: hidden;
}

.tr1 {
    flex-shrink: 0;
}

.tr2 {
    position: relative;
    flex-shrink: 1;
}

.td {
    flex-grow: 0;
}

.grid-content {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    overflow: hidden;
    position: relative;

    .grid-lt,
    .grid-lb,
    .grid-rt,
    .grid-rb {
        position: relative;
    }

    .grid-top,
    .grid-bottom {
        width: 100%;
        overflow: hidden;
        display: flex;
        flex-direction: row;
    }

    .grid-top {
        flex-shrink: 0;
    }

    .grid-bottom {
        flex-grow: 1;
    }

    .grid-lt,
    .grid-lb {
        flex-shrink: 0;
        overflow: hidden;
    }

    .grid-rt,
    .grid-rb {
        flex-shrink: 1;
        flex-grow: 1;
        width: 100%;
        overflow: hidden;
        position: relative;
    }

}

.resize-line {
    position: absolute;
    z-index: 10;

    &.resize-line-row {
        border-bottom: 1px dashed #666;
        width: 100%;
    }

    &.resize-line-column {
        border-right: 1px dashed #666;
        height: 100%;
    }
}

.table-rowhead-wrap {
    overflow: hidden;
    width: 44px;
    flex-shrink: 0;
}

.table-colhead-wrap {
    overflow: hidden;
}

.table-empty {
    width: 44px;
    height: 1px;
    text-align: center;
}

.td-empty {
    border: 0px solid $border-color;
    border-width: 0px 1px 1px 0px;
    margin: -1px 0px 0px -1px;
}

.meg-sheet-formatpainter {
    cursor: crosshair;
}
</style>
