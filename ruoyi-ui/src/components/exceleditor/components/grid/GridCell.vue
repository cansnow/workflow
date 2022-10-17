<template>
    <div
        class="meg-gridcell"
        @mousedown.left="mouseDown"
        @drop="handleDrop"
        @dragover="handleDragover"
        @dragleave="handleDragleave"
        @contextmenu.prevent="showMenu"
        @dblclick="doEdit"
        :style="cell.style.css"
    >   
        <div
            @keydown.enter="handleInputEnter"
            style="height: 100%;"
            v-if="cellType == 'text' || cellType == 'password' || cellType == 'number'"
        >
            <el-input type="text" :ref="cellType" v-model="value" @input="handleChange" :placeholder="cellProps.ph || '请输入'" v-if="cellType == 'text'" />
            <el-input type="text" :ref="cellType" show-password @input="handleChange" :placeholder="cellProps.ph || '请输入'" v-model="value" v-if="cellType == 'password'" />
            <el-input-number :ref="cellType" type="text" @change="handleChange" :placeholder="cellProps.ph || '请输入'" v-model="value" v-if="cellType == 'number'" />
        </div>
		<el-upload
			v-if="cellType == 'upload'"
			class="upload-demo"
			:action="uploadFileUrl"
			:on-preview="handlePreview"
			:on-remove="handleRemove"
			:before-remove="beforeRemove"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccess"
            :headers="headers"
            name="uploadFile"
            :data="{ type: 1, name: 'jhjj'}"
			multiple
			:limit="5"
			:on-exceed="handleExceed"
			:file-list="fileList">
		  <el-button size="small" type="primary">点击上传</el-button>
		  <div slot="tip" class="el-upload__tip">只能上传{{ cellProps.t | getUploadTypeText }}文件，且不超过1MB</div>
		</el-upload>
		
		<el-image v-if="cellType == 'image'" :src="value | getImgUrl"></el-image>
		<el-button :type="cellProps.t == 'text' ? 'text' : 'primary'" :style="setBtnStyle(cell.style.css)" v-if="cellType == 'button'" >{{value}}</el-button>
        <el-date-picker
            v-if="cellType == 'datetime'"
            v-model="value"
            type="datetime"
            @change="handleChangeDate"
            @focus="handleDateFocus"
            :placeholder="cellProps.ph || '选择日期时间'"
        >
        </el-date-picker>
        <!-- 单选 -->
		<el-radio-group v-model="value" v-if="cellType == 'radio'" @change="handleChange">
			<el-radio :label="item.value + ''" v-for="item in options" :key="item.value">{{item.label}}</el-radio>
		</el-radio-group>
        <!-- 多选 -->
		<el-checkbox-group v-model="value" v-if="cellType == 'checkbox'" @change="handleChange">
			<el-checkbox :label="item.value" v-for="item in options" :key="item.value">{{item.label}}</el-checkbox>
		</el-checkbox-group>
        <!-- 下拉单选 -->
		<el-select v-model="value" v-if="cellType == 'select'" @change="handleChange">
			<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉多选 -->
        <el-select v-model="value" multiple v-if="cellType == 'selectMultiple'" @change="handleChange">
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
		<div 
            class="meg-cellval" 
            :class="[cell.style.alignCss]" 
            :style="!!cell.style.css.textDecoration && cell.style.css.textDecoration == 'underline' ? { textDecoration: 'underline' } : {}" 
            v-if="!cellType"
        >
            <del v-if="!!cell.style.css.textDel">
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" >{{ formatValue(cell) }}</el-button>
                <template v-else >{{ formatValue(cell) }}</template>
            </del>
            <template v-else>
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" >{{ formatValue(cell) }}</el-button>
                <template v-else >{{ formatValue(cell) }}</template>
            </template>
        </div>
		<div 
            class="meg-cellval" 
            :class="[cell.style.alignCss]"
            :style="!!cell.style.css.textDecoration && cell.style.css.textDecoration == 'underline' ? { textDecoration: 'underline' } : {}" 
            v-if="cellType == 'Cell'"
        >
            <del v-if="!!cell.style.css.textDel">
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" >{{ value }}</el-button>
                <template v-else >{{ value }}</template>
            </del>
            <template v-else>
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" >{{ value }}</el-button>
                <template v-else >{{ value }}</template>
            </template>
        </div>
    </div>
</template>

<script>
import { getCellValue } from '../sheet/mixins/cell/cellUtil';
import TreeSelect from '../treeSelect/index.vue';
import TreeSelectMultiple from '../treeSelect/treeSelectMultiple.vue';
import { getToken } from "@/utils/auth";

export default {
    inject: ['$sheet'],
    props: {
        cell: Object,
    },
    filters: {
        getImgUrl(value) {
            const url = location && location.origin ? location.origin + '/uploads' + value : '/uploads' + value;
            return url;
        },
        getUploadTypeText(t) {
            switch(t) {
                case 'word':
                    return '文档';
                case 'zip':
                    return '压缩';
                case 'image':
                    return '图片';
                default: return '自定义';
            }
        },
    },
    components: { TreeSelect, TreeSelectMultiple },
	data(){
		return {
			data:{},
			props:this.cell,
            fileList:[],
            cellType: undefined,
            value: undefined, // 默认值
            options: undefined, // select 组件必须参数
            ifCell: true, // 是否单元格 true 单元格，false 组件
            cellProps: {},
            uploadFileUrl: process.env.VUE_APP_BASE_API + "/workflow/fileInfo/upload", // 上传的图片服务器地址
            headers: {
                Authorization: "Bearer " + getToken(),
            },
            cellPos: {}, // 日期位置
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
    mounted() {
        this.$sheet.$on('on-cellval-change', (data) => {
            this.updateCellType();
        });
    },
    methods: {
        // 回车去除焦点
        handleInputEnter() {
            if (this.cellType == 'number') {
                this.$el.querySelector('.el-input__inner').blur();
                return;
            }
            this.$refs[this.cellType].blur();
        },
        setBtnStyle(style) {
            const styleTemp = JSON.parse(JSON.stringify(style));
            const temp = {};
            if (!!styleTemp.backgroundColor) {
                Object.assign(temp, { borderColor: style.backgroundColor, backgroundColor: style.backgroundColor });
            }
            if (!!styleTemp.color) {
                Object.assign(temp, { color: style.color });
            }
            return temp;
        },
        handlePreview(){},
        handleAvatarSuccess(res, file) {
			console.warn('handleAvatarSuccess', res, file);
		},
        handleRemove(file, fileList) {
            console.log(file, fileList);
        },
        beforeRemove(file, fileList) {
            const type = this.getUploadType(file.raw)
            if (type) {
                return this.$confirm(`确定移除 ${ file.name }？`);
            }
        },
        getUploadType(file) {
            const word = [
                'text/plain',
                'application/msword',
                'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                'application/vnd.ms-excel',
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                'application/pdf',
                'application/vnd.ms-powerpoint',
                'application/vnd.openxmlformats-officedocument.presentationml.presentation',
            ];
            const img = ['image/jpeg', 'image/png', 'image/bmp', 'image/gif', 'image/tiff'];
            const zip = ['application/x-zip-compressed', '7z', 'rar'];
            let index = -1;
            switch(this.cellProps.t) {
                case 'word':
                    index = word.findIndex(item => item == file.type);
                    return index != -1;
                case 'image':
                    index = img.findIndex(item => item == file.type);
                    return index != -1;
                case 'zip':
                    if (file.type.indexOf('.') != -1) {
                        const strList = file.type.split('.');
                        index = zip.findIndex(item => item == strList[1]);
                        if (index != -1) {
                            return true;
                        }
                    }
                    if (!!file.type) {
                        index = zip.findIndex(item => item == file.type);
                        return index != -1;
                    }
                    return false;
                default: return true;
            }

        },
        // 开始上传
        beforeAvatarUpload(file) {
            const isIMG = this.getUploadType(file); //file.type === 'image/jpeg' || file.type === 'image/png';
            const isLt1M = file.size / 1024 / 1024 < 1;

            if (!isIMG) {
                const temp = {
                    word: '文档',
                    image: '图片',
                    zip: '压缩包',
                };
                this.$message.error(`上传文件只能是${temp[this.cellProps.t]}格式!`);
            }
            if (!isLt1M) {
            this.$message.error('上传文件大小不能超过 1MB!');
            }
            return isIMG && isLt1M;
        },
        handleExceed(files, fileList){
            this.$message.warning(`当前限制选择 5 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
        },
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
                // radio checkbox select 是字符串
                if (this.cellType == 'radio' || this.cellType == 'select') {
                    if (!!this.value && this.value instanceof Array) {
                        if (this.cellType == 'radio' || this.cellType == 'select') this.value = '';
                    }
                }
                this.cellProps = typeof this.cell.option['p'] == 'undefined' ? {} : this.cell.option.p;
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
        // 拖拽释放
        handleDrop(event) {
            const pos = {
                columnIndex: this.cell.columnIndex,
                rowIndex: this.cell.rowIndex,
            };
            const text = event.dataTransfer.getData("Text");
            const tableName = event.dataTransfer.getData("TableName");
            // const p = { f: text, r: { r: [''], s: true } };
            // this.$sheet.setCellAttribute(pos, text, 'p', p);
            // this.cell.option
            const temp = !!this.cell.option ? JSON.parse(JSON.stringify(this.cell.option)) : {};
            
            // 如果是单元格组件
            if (!!this.cell.option && temp.c) {
                if (!!temp.p) {
                    const p = temp.p;
                    Object.assign(p, { f: text, e: 'none', tn: tableName });
                    if (temp.c == 'Cell') {
                        Object.assign(p, { ct: 'Cell', cl: '' });
                    }
                    Object.assign(temp, { p });
                } else {
                    const p = { f: text, e: 'none', r: { r: ['', ''], s: true, w: true }, tn: tableName };
                    if (temp.c == 'Cell') {
                        Object.assign(p, { ct: 'Cell', cl: '' });
                    }
                    Object.assign(temp, { p });
                }
                Object.assign(temp, { v: text });
            } else {
                const value = {
                    c: 'Cell',
                    p: { f: text, e: 'none', r: { r: ['', ''], s: true, w: true }, ct: 'Cell', cl: '', tn: tableName },
                    v: text,
                }
                Object.assign(temp, value);
            }
            this.$sheet.setSelectArea(pos, pos);
            this.$sheet.doEditCell();
            this.$sheet.doCancelEdit();
            this.$sheet.doEditCellValue(temp);
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
        handleChange(e) {
            this.$sheet.doEditCell();
            const temp = !!this.cell.option ? JSON.parse(JSON.stringify(this.cell.option)) : {};
            if (!!this.cellProps && typeof this.cellProps.f != 'undefined') {
                Object.assign(this.cellProps, { f: e + ''});
            }
            if (this.cellType == 'datetime') {
                if (e instanceof Date || parseInt(e) != NaN) {
                    Object.assign(temp, { p: this.cellProps, v: this.formatDate(e) + ''});
                }
            } else if (this.cellType == 'selectMultiple' || this.cellType == 'checkbox') {
                Object.assign(temp, { p: this.cellProps, v: e });
            } else {
                Object.assign(temp, { p: this.cellProps, v: e + '' });
            }
            this.$sheet.doCancelEdit();
            this.$sheet.$emit('selectCell');
            this.$sheet.doEditCellValue(temp);
        },
        formatDate(date) {
            let temp = new Date(date);
            let y = temp.getFullYear();
            let m = temp.getMonth() + 1;
            let d = temp.getDate();
            let h = temp.getHours();
            let M = temp.getMinutes();
            let s = temp.getSeconds();
            function format(e) {
                return e < 10 ? '0' + e : e;
            }
            return `${y}-${format(m)}-${d} ${format(h)}:${format(M)}:${format(s)}`;
        },
        // 日期组件获取焦点
        handleDateFocus() {
            this.cellPos = JSON.parse(JSON.stringify(this.$sheet.selctionExpand));
        },
        // 日期变更
        handleChangeDate(e) {
            // 判断是否该组件
            if (JSON.stringify(this.cellPos) == JSON.stringify(this.$sheet.selctionExpand)) {
                this.handleChange(e);
            }
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
    .el-button {
        padding: unset;
        border-radius: unset;
    }
    .el-date-editor .el-input__prefix .el-input__icon.el-icon-time,
    .el-date-editor .el-input__suffix .el-input__suffix-inner .el-input__icon {
        line-height: 100% !important;
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
