<template>
    <div class="meg-gridcell" @mousedown.left="mouseDown" @contextmenu.prevent="showMenu" @dblclick="doEdit"
        :style="cell.style.css">
        <el-input type="text" v-model="props.option.v" v-if="isCtrl('text')" />
		<el-input type="text" show-password v-model="props.option.v" v-if="isCtrl('password')" />
		<el-input-number type="text" v-model="props.option.v" v-if="isCtrl('number')" />
		<el-upload
			v-if="isCtrl('upload')"
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
		
		<el-image v-if="isCtrl('image')" :src="props.option.v"></el-image>
		<el-button type="primary" v-if="isCtrl('button')" >{{props.option.v}}</el-button>
		<el-date-picker
			:value="props.option.v"
            placement ="bottom"
			type="datetime"
			placeholder="选择日期时间"
			v-if="isCtrl('datetime')">
		</el-date-picker>
		<el-date-picker
			:value="props.option.v"
            placement ="bottom"
			type="date"
			placeholder="选择日期"
			v-if="isCtrl('date')">
		</el-date-picker>
        <el-date-picker
			:value="props.option.v"
            placement ="bottom"
			type="time"
			placeholder="选择时间"
			v-if="isCtrl('time')">
		</el-date-picker>
		<el-radio-group v-model="props.option.v" v-if="isCtrl('radio')">
			<el-radio :label="item.value" v-for="item in cell.option.options" :key="item.value">{{item.label}}</el-radio>
		</el-radio-group>
		<el-checkbox-group v-model="checkboxValue" v-if="isCtrl('checkbox')">
			<el-checkbox :label="item.value" v-for="item in cell.option.options" :key="item.value">{{item.label}}</el-checkbox>
		</el-checkbox-group>
		<el-select v-model="props.option.v" v-if="isCtrl('select')">
			<el-option v-for="item in cell.option.options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉多选 -->
        <el-select v-model="props.option.v" multiple v-if="isCtrl('select')">
			<el-option v-for="item in cell.option.options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉树单选 -->
        <TreeSelect
            style="width: 100%"
            v-if="isCtrl('treeSelect')"
            :options="cell.option.options"
            :value="props.option.v"
            :clearable="true"
            :accordion="true"
            @getValue="getTreeSelectValue($event)"
        />
        <!-- 下拉树多选 -->
        <TreeSelectMultiple
            style="width: 100%"
            v-if="isCtrl('treeSelectMultiple')"
            :options="cell.option.options"
            :clearable="true"
            :accordion="true"
            @getValue="getTreeSelectMultipleValue($event)"
        />
		<div class="meg-cellval" :class="[cell.style.alignCss]" v-if="!isCtrl()">{{ formatValue(cell) }}</div>
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
		}
	},
    methods: {
        handlePreview(){},
        handleRemove(){},
        beforeRemove(){},
        handleExceed(){},
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
            this.$sheet.doEditCell();
        },
        formatValue(cell) {
            const v = getCellValue(cell.option);
            return v;
        },
        // 下拉树单选 数据
        getTreeSelectValue(e) {
            console.log('getTreeSelectValue e',e);
            this.$set(this.props.option, 'v', e[0]);
        },
        // 下拉树多选 数据
        getTreeSelectMultipleValue(e) {
            console.log('getTreeSelectMultipleValue e',e);
            const temp = [];
            e.forEach(item => {
                temp.push(item.id);
            });
            this.$set(this.props.option, 'v', temp);
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
