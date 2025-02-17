<template>
    <div class="meg-gridcell" @mousedown.left="mouseDown" @contextmenu.prevent="showMenu" @dblclick="doEdit"
        :style="cell.style.css">
        <el-input type="text" v-model="value" v-if="cellType == 'text'" />
		<el-input type="text" show-password v-model="value" v-if="cellType == 'password'" />
		<el-input-number type="text" v-model="value" v-if="cellType == 'number'" />
		<el-upload
			v-if="cellType == 'upload'"
			class="upload-demo"
			action="https://jsonplaceholder.typicode.com/posts/"
			:on-preview="handlePreview"
			:on-remove="handleRemove"
			:before-remove="beforeRemove"
			multiple
			:limit="3"
			:on-exceed="handleExceed"
			:file-list="fileList">
		  <el-button size="small" type="primary">点击上传</el-button>
		  <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
		</el-upload>
		
		<el-image v-if="cellType == 'image'" :src="value"></el-image>
		<el-button type="primary" v-if="cellType == 'button'" >{{value}}</el-button>
		<el-date-picker
			:value="value"
            placement ="bottom"
			type="datetime"
			placeholder="选择日期时间"
			v-if="cellType == 'datetime'">
		</el-date-picker>
		<el-date-picker
			:value="value"
            placement ="bottom"
			type="date"
			placeholder="选择日期"
			v-if="cellType == 'date'">
		</el-date-picker>
        <el-date-picker
			:value="value"
            placement ="bottom"
			type="time"
			placeholder="选择时间"
			v-if="cellType == 'time'">
		</el-date-picker>
		<el-radio-group v-model="value" v-if="cellType == 'radio'">
			<el-radio :label="item.value" v-for="item in options" :key="item.value">{{item.label}}</el-radio>
		</el-radio-group>
		<el-checkbox-group v-model="checkboxValue" v-if="cellType == 'checkbox'">
			<el-checkbox :label="item.value" v-for="item in options" :key="item.value">{{item.label}}</el-checkbox>
		</el-checkbox-group>
		<el-select v-model="value" v-if="cellType == 'select'">
			<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉多选 -->
        <el-select v-model="value" multiple v-if="cellType == 'selectMultiple'">
			<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉树单选 -->
        <TreeSelect
            style="width: 100%"
            v-if="cellType == 'treeSelect'"
            :options="options"
            :value="value"
            :clearable="true"
            :accordion="true"
            @getValue="getTreeSelectValue($event)"
        />
        <!-- 下拉树多选 -->
        <TreeSelectMultiple
            style="width: 100%"
            v-if="cellType == 'treeSelectMultiple'"
            :options="options"
            :value="value"
            :clearable="true"
            :accordion="true"
            @getValue="getTreeSelectMultipleValue($event)"
        />
		<div class="meg-cellval" :class="[cell.style.alignCss]" v-if="!cellType || cellType == 'Cell'">{{ formatValue(cell) }}</div>
    </div>
</template>

<script>
import { getCellValue } from '../sheet/mixins/cell/cellUtil';
import TreeSelect from '../treeSelect/index.vue';
import TreeSelectMultiple from '../treeSelect/treeSelectMultiple.vue';

export default {
    inject: ['$sheet'],
    props: {
        cell: Object,
    },
    components: { TreeSelect, TreeSelectMultiple },
	data(){
		return {
			data:{},
			props:this.cell,
            fileList:[],
			checkboxValue:[],
            cellType: undefined,
            value: undefined, // 默认值
            options: undefined, // select 组件必须参数
            ifCell: true, // 是否单元格 true 单元格，false 组件
		}
	},
    watch: {
        '$sheet.cellType': {
            handler() {
                this.updateCellType();
            },
            immediate: true,
        },
    },
    methods: {
        handlePreview(){},
        handleRemove(){},
        beforeRemove(){},
        handleExceed(){},
        updateCellType() {
            if (this.cell.option) {
                if (typeof(this.cell.option['c']) == 'undefined') {
                    this.cellType = null;
                    this.ifCell = true;
                } else {
                    this.cellType = this.cell.option.c;
                    this.ifCell = this.cellType == 'Cell';
                }
                this.value = typeof(this.cell.option['v']) == 'undefined' ? '' : this.cell.option.v;
                this.options = typeof(this.cell.option['options']) == 'undefined' ? [] : this.cell.option.options;
            }
        },
		isCtrl(v){
			if(v){
				return this.cell.option && this.cell.option.c==v;
			}else{
				return this.cell.option && this.cell.option.c;
			}
		},
        mouseDown(event) {
            const pos = {
                columnIndex: this.cell.columnIndex,
                rowIndex: this.cell.rowIndex,
            };
            this.$sheet.s_cellSelectStart(event, pos);
        },
        showMenu(e) {
            this.$sheet.showCellMenu({
                columnIndex: this.cell.columnIndex,
                rowIndex: this.cell.rowIndex,
            }, e.pageY, e.pageX);
        },
        doEdit() {
            // 组件不需要双击编辑
            if (!this.ifCell) {
                return;
            }
            this.$sheet.doEditCell();
        },
        formatValue(cell) {
            const v = getCellValue(cell.option);
            return v;
        },
        // 下拉树单选 数据
        getTreeSelectValue(e) {
            console.log('getTreeSelectValue e',e);
            this.$set(this.props.option, 'v', e[0] + '');
            this.value = e[0] + '';
        },
        // 下拉树多选 数据
        getTreeSelectMultipleValue(e) {
            console.log('getTreeSelectMultipleValue e',e);
            this.$set(this.props.option, 'v', e);
            this.value = e;
        },
    },
};
</script>

<style lang="scss">
.meg-gridcell {
    position: absolute;
    height: 100%;
    overflow: hidden;
    font-size: 11pt;
    z-index: 1;
    padding: 0 1px 1px 0;
    box-sizing: border-box;
    .el-input,.el-input-number{
		border: 0;
		.el-input-number__decrease, .el-input-number__increase{
			height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
		}
		.el-input__inner{
			width: 100%;
			height: 100%;
			border-radius:0;border: 0;
		}
	}
	.el-select,.el-radio-group{
		.el-radio{
			font-style: normal;
		}
	}
	.el-button,
	.el-select,
	.el-radio-group,
    .el-checkbox-group,
	.el-input,
	.el-input-number,
	.el-image{
		width: 100%;
		height: 100%;
	}
	.el-radio-group,
    .el-checkbox-group{
            align-items: center;
            display: flex;
            flex-wrap: wrap;
            justify-content: left;
    }
}

.meg-cellval-warp {
    position: relative;
    height: inherit;
    width: inherit;
}

.meg-cellval-vat {
    position: absolute;
    top: 0px;
}

.meg-cellval-vab {
    position: absolute;
    bottom: 0px;
}

.meg-cellval-vam {
    position: absolute;
    top: 50%;
    transform: translateY(-51%);
}

//自动换行
.meg-cellval-lnor {
    width: inherit;
    max-height: 100%;
    text-align: left;
    white-space: normal;
    word-wrap: break-word;
    padding-left: 2px;
}

.meg-cellval-cnor {
    width: inherit;
    max-height: 100%;
    text-align: center;
    white-space: normal;
    word-wrap: break-word;
}

.meg-cellval-rnor {
    width: inherit;
    max-height: 100%;
    text-align: right;
    white-space: normal;
    word-wrap: break-word;
    right: 2px;
}

//不换行
.meg-cellval-lnow {
    width: inherit;
    max-height: 100%;
    text-align: left;
    white-space: nowrap;
    padding-left: 2px;
}

.meg-cellval-cnow {
    width: inherit;
    text-align: center;
    white-space: nowrap;
}

.meg-cellval-rnow {
    width: inherit;
    text-align: right;
    white-space: nowrap;
    right: 2px;
}
</style>
