<template>
    <div
        class="meg-gridcell"
        @mousedown.left="mouseDown"
        @drop="handleDrop"
        @dragover="handleDragover"
        @dragleave="handleDragleave"
        @contextmenu.prevent="showMenu"
        @dblclick="doEdit"
        :style="gridcellStyle(cell.style.css)"
    >   
        <div
            @keydown.enter="handleInputEnter"
            style="height: 100%;" 
            v-if="cellType == 'text' || cellType == 'password' || cellType == 'number'"
        >
            <del v-if="!!cell.style.css.textDel">
                <el-input type="text" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" v-model="value" @input="handleChange" :placeholder="cellProps.ph || '请输入'" v-if="cellType == 'text'" />
                <el-input type="text" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" show-password @input="handleChange" v-model="value" :placeholder="cellProps.ph || '请输入'" v-if="cellType == 'password'" />
                <el-input-number :style="styleCpn(cell.style.css)" type="text" :ref="cellType" :disabled="!props.option.p.r.w" @change="handleChange" :placeholder="cellProps.ph || '请输入'" v-model="value" v-if="cellType == 'number'" />
            </del>
            <template v-else>
                <el-input type="text" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" v-model="value" @input="handleChange" :placeholder="cellProps.ph || '请输入'" v-if="cellType == 'text'" />
                <el-input type="text" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" show-password @input="handleChange" v-model="value" :placeholder="cellProps.ph || '请输入'" v-if="cellType == 'password'" />
                <el-input-number :style="styleCpn(cell.style.css)" type="text" :ref="cellType" :disabled="!props.option.p.r.w" @change="handleChange" :placeholder="cellProps.ph || '请输入'" v-model="value" v-if="cellType == 'number'" />
            </template>
        </div>
		<el-upload
			v-if="cellType == 'upload'"
			class="upload-demo"
			:action="uploadFileUrl"
			:on-preview="handlePreview"
            :disabled="!props.option.p.r.w"
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
		<el-button
            :type="cellProps.t == 'text' ? 'text' : 'primary'"
            :ref="cellType"
            :style="setBtnStyle(cell.style.css)"
            v-if="cellType == 'button'"
            @click="handleCellBtnClick"
        >{{value}}</el-button>
        <el-date-picker
            v-if="cellType == 'datetime'"
            :ref="cellType"
            :style="styleCpn(cell.style.css)"
            v-model="value"
            type="datetime"
            @change="handleChangeDate"
            @focus="handleDateFocus"
            :placeholder="cellProps.ph || '选择日期时间'"
        >
        </el-date-picker>
		<el-radio-group v-model="value" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" v-if="cellType == 'radio'" @change="handleChange">
			<el-radio :label="item.value + ''" v-for="item in options" :key="item.value">{{item.label}}</el-radio>
		</el-radio-group>
		<el-checkbox-group v-model="value" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" v-if="cellType == 'checkbox'" @change="handleChange">
			<el-checkbox :label="item.value + ''" v-for="item in options" :key="item.value">{{item.label}}</el-checkbox>
		</el-checkbox-group>
		<el-select v-model="value" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" v-if="cellType == 'select'" @change="handleChange">
			<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉多选 -->
        <el-select v-model="value" :style="styleCpn(cell.style.css)" :ref="cellType" :disabled="!props.option.p.r.w" multiple v-if="cellType == 'selectMultiple'" @change="handleChange">
			<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
			</el-option>
		</el-select>
        <!-- 下拉树单选 -->
        <TreeSelect
            style="width: 100%"
            v-if="cellType == 'treeSelect'"
            :options="options"
            :style="styleCpn(cell.style.css)"
            :ref="cellType"
            :disabled="!props.option.p.r.w"
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
            :style="styleCpn(cell.style.css)"
            :ref="cellType"
            :disabled="!props.option.p.r.w"
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
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" @click="handleCellLink" >{{ formatValue(cell) }}</el-button>
                <template v-else >{{ formatValue(cell) }}</template>
            </del>
            <template v-else>
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" @click="handleCellLink" >{{ formatValue(cell) }}</el-button>
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
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" @click="handleCellLink" >{{ value }}</el-button>
                <template v-else >{{ value }}</template>
            </del>
            <template v-else>
                <el-button :style="setBtnStyle(cell.style.css)" v-if="cellProps.ct == 'Link'" type="text" @click="handleCellLink" >{{ value }}</el-button>
                <template v-else >{{ value }}</template>
            </template>
        </div>
    </div>
</template>

<script>
import { getCellValue } from '../mixins/cell/cellUtil';
import TreeSelect from '../../treeSelect/index.vue';
import TreeSelectMultiple from '../../treeSelect/treeSelectMultiple.vue';
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
        styleCpn(css) {
            const temp = {
                display: 'flex'
            };
            if (!!css.fontSize) {
                Object.assign(temp, { fontSize: css.fontSize });
            }
            if (!!css.backgroundColor) {
                Object.assign(temp, { backgroundColor: css.backgroundColor });
            }
            if (this.cellType == 'text' || this.cellType == 'password' || this.cellType == 'datetime') {
                this.$nextTick(() => {
                    this.$refs[this.cellType].$el.children[0].style.color = css.color || '';
                    this.$refs[this.cellType].$el.children[0].style.fontWeight = css.fontWeight || '';
                    this.$refs[this.cellType].$el.children[0].style.fontStyle = css.fontStyle || '';
                    this.$refs[this.cellType].$el.children[0].style.fontFamily = css.fontFamily || '';
                    if (this.cellType == 'datetime') {
                        this.$refs[this.cellType].$el.children[0].style.textDecoration = !!css.textDel ? 'line-through' : '';
                    }
                })
            }
            if (this.cellType == 'number') {
                this.$nextTick(() => {
                    this.$refs[this.cellType].$el.children[2].style.fontSize = css.fontSize || '';
                    this.$refs[this.cellType].$el.children[2].style.backgroundColor = css.backgroundColor || '';
                    this.$refs[this.cellType].$el.children[2].children[0].style.color = css.color || '';
                    this.$refs[this.cellType].$el.children[2].children[0].style.fontWeight = css.fontWeight || '';
                    this.$refs[this.cellType].$el.children[2].children[0].style.fontStyle = css.fontStyle || '';
                    this.$refs[this.cellType].$el.children[2].children[0].style.fontFamily = css.fontFamily || '';
                })
            }
            if (this.cellType == 'radio' || this.cellType == 'checkbox') {
                this.$nextTick(() => {
                    for (let i = 0; i < this.$refs[this.cellType].$el.children.length; i++) {
                        const element = this.$refs[this.cellType].$el.children[i];
                        element.style.color = css.color || '';
                        element.style.fontWeight = css.fontWeight || '';
                        element.style.fontStyle = css.fontStyle || '';
                        element.style.fontFamily = css.fontFamily || '';
                        element.style.display = 'flex';
                        element.style.alignItems = 'center';
                        element.children[0].style.display = 'flex';
                        element.children[1].style.fontSize = css.fontSize || '';
                        element.children[1].style.textDecoration = !!css.textDel ? 'line-through' : '';
                    }
                })
            }
            if (
                this.cellType == 'select' ||
                this.cellType == 'selectMultiple' ||
                this.cellType == 'treeSelect' ||
                this.cellType == 'treeSelectMultiple'
            ) {
                this.$nextTick(() => {
                    this.$refs[this.cellType].$el.children[0].children[0].style.fontSize = css.fontSize || '';
                    this.$refs[this.cellType].$el.children[0].children[0].style.color = css.color || '';
                    this.$refs[this.cellType].$el.children[0].children[0].style.fontWeight = css.fontWeight || '';
                    this.$refs[this.cellType].$el.children[0].children[0].style.fontStyle = css.fontStyle || '';
                    this.$refs[this.cellType].$el.children[0].children[0].style.fontFamily = css.fontFamily || '';
                    this.$refs[this.cellType].$el.children[0].children[0].style.textDecoration = !!css.textDel ? 'line-through' : '';
                    // this.$refs[this.cellType].$el.children[0].style.textDecoration = css.textDecoration || '';
                });
            }
            return temp;
        },
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
            const temp = {
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
            };
            if (!!styleTemp.fontSize) {
                Object.assign(temp, { fontSize: style.fontSize });
            }
            if (!!styleTemp.backgroundColor) {
                Object.assign(temp, { borderColor: style.backgroundColor, backgroundColor: style.backgroundColor });
            }
            if (!!styleTemp.color) {
                Object.assign(temp, { color: style.color });
            }
            if (!!styleTemp.fontWeight) {
                Object.assign(temp, { fontWeight: style.fontWeight });
            }
            if (!!styleTemp.fontStyle) {
                Object.assign(temp, { fontStyle: style.fontStyle });
            }
            if (!!styleTemp.textDecoration) {
                Object.assign(temp, { textDecoration: style.textDecoration });
            }
            if (!!styleTemp.fontFamily) {
                Object.assign(temp, { fontFamily: style.fontFamily });
            }
            
            // 按钮删除线
            this.$nextTick(() => {
                this.$refs[this.cellType].$el.children[0].style.textDecoration = !!styleTemp.textDel ? 'line-through' : '';
            })
            return temp;
        },
        // 单元格样式
        gridcellStyle(cellStyle) {
            const css = JSON.parse(JSON.stringify(cellStyle));
            const pos = {
                columnIndex: this.cell.columnIndex,
                rowIndex: this.cell.rowIndex,
            };
            if (typeof css['border-left'] != 'undefined') {
                const temp = this.$sheet.getPosCell(Object.assign({}, pos, { columnIndex : pos.columnIndex - 1}));
                // console.log('temp', temp);
                if (!!temp && typeof temp.s != 'undefined') {
                    const style = this.$sheet.getStyle(temp.s);
                    if (!!style.option.border && style.option.border.indexOf('r') != -1) {
                        delete css['border-left'];
                    }
                }
            }
            if (typeof css['border-top'] != 'undefined') {
                const temp = this.$sheet.getPosCell(Object.assign({}, pos, { rowIndex : pos.rowIndex - 1}));
                // console.log('temp', temp);
                if (!!temp && typeof temp.s != 'undefined') {
                    const style = this.$sheet.getStyle(temp.s);
                    if (!!style.option.border && style.option.border.indexOf('b') != -1) {
                        delete css['border-top'];
                    }
                }
            }
            return css;
        },
        handlePreview(){},
        // 上传成功后
        handleAvatarSuccess(res, file) {
            console.warn('handleAvatarSuccess', res, file);
            // [{name: 'food.jpg', url: 'https://xxx.cdn.com/xxx.jpg'}]

            this.fileList.push({name: file.name, url: `${(location && location.origin ? location.origin : '') + res.fileInfo.filePath}`, fileId: res.fileInfo.fileId });
            
            this.handleChange(this.fileList);
		},
        handleRemove(file, fileList) {
            console.log(file, fileList);
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
        beforeRemove(file, fileList) {
            const type = this.getUploadType(file.raw);
            if (type) {
                // if (!!file.response && typeof file.response.fileInfo != 'undefined') {
                //     const index = this.value.findIndex(item => item == file.response.fileInfo.fileId);
                //     if (index != -1) {
                //         this.value.splice(index, 1);
                //     }
                // }
                return this.$confirm(`确定移除 ${ file.name }？`);
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
                if (this.cellType == 'checkbox' || this.cellType == 'selectMultiple') {
                    this.value = typeof(this.cell.option['v']) == 'undefined' ? [] : this.cell.option.v instanceof Array ? this.cell.option.v : [this.cell.option.v + ''];
                }
                if (this.cellType == 'upload') {
                    if (this.value instanceof Array) {
                        this.fileList = this.value;
                    }
                }
                if (this.cellType == 'number') {
                    if (parseInt(this.value) == NaN) {
                        this.value = 0;
                    } else {
                        this.value = parseInt(this.value);
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
            this.$sheet.doCancelEdit();
            this.$sheet.$emit('selectCell');
            if (this.cellType == 'checkbox' || this.cellType == 'selectMultiple' || this.cellType == 'upload') {
                this.$sheet.doEditCellValue(e);
                return
            }
            if (this.cellType == 'datetime') {
                const temp = this.formatDate(e);
                this.$sheet.doEditCellValue(temp);
                return
            }
            this.$sheet.doEditCellValue(e + '');
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
        // 点击了单元格按钮
        handleCellBtnClick() {
            if (this.cellProps.t == 'text' && !!this.cellProps.bl) {
                window.open(this.cellProps.bl, '_blank');
            }
            if (this.cellProps.t != 'text') {
                this.$sheet.$emit('clikcCellBtn', this.cell.option);
            }
        },
        // 点击了单元格超链接
        handleCellLink() {
            this.$sheet.$emit('clikcCellLink', this.cellProps.cl || '');
        }
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

    .el-input .el-input__inner {
        background-color: rgba(238, 238, 238, 0.479) !important;
    }
    .el-button {
        padding: unset;
        border-radius: unset;
    }
    .el-date-editor .el-input__prefix .el-input__icon.el-icon-time,
    .el-date-editor .el-input__suffix .el-input__suffix-inner .el-input__icon {
        line-height: 100% !important;
        font-size: 14px !important;
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
