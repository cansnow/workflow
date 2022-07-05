<template>
	<div class="meg-menu">
		<el-tooltip :content="tool.title" placement="top" v-for="(tool, name) in toolbars" :key="name" :hide-after="500">
			<Dropdown class="meg-menu-op meg-menu-op-color" v-if="name == 'color' || name == 'bgColor'">
				<i :class="tool.icon" @click="toolbarEvent(name,$event)"></i>
				<span class="meg-menu-pcol" :style="{ background: tool.value }" @click="toolbarEvent(name,$event)"></span>
				<Colorpick slot="content" v-model="tool.value" @input="toolbarEvent(name,$event)"></Colorpick>
			</Dropdown>	
			<Dropdown class="meg-menu-op" v-else-if="name == 'border'">
				<i @click="toolbarEvent(name,$event)" class="mdi mdi-border-all"></i>
				<m-Select slot="content" v-model="tool.value" :options="tool.options" @input="toolbarEvent(name,$event)"></m-Select>
			</Dropdown>

			<el-select v-else-if="tool.options" style="width: 180px;" v-model="tool.value" @change="toolbarEvent(name,$event)" placeholder="请选择">
				<el-option
					v-for="(option,index) in tool.options"
					:value="option.value ? option.value : option"
					:label="option.label"
					:key="index"
				>
					<!-- <i v-if="option.icon" :class="option.icon"></i> -->
					{{option.label ? option.label : option}}
				</el-option>
			</el-select>
			<el-button class="" :disabled="tool.disabled" :selected="tool.selected" :checked="tool.checked" @click="toolbarEvent(name)" :title="tool.title" v-else>
				<i :class="tool.icon"></i>
			</el-button>
		</el-tooltip>
	</div>
</template>

<script>
import Dropdown from './dropdown/Dropdown.vue';
import mSelect from './select/Select.vue';
import Colorpick from './colorpick/Colorpick.vue';
export default {
	provide() {
		return {
			$menu: this
		};
	},
	components: {
		Dropdown,
		mSelect,
		Colorpick
	},
	data() {
		return {
			value:"",
			toolbars: {
				undo:{
					icon: 'mdi mdi-undo',
					title: '撤销',
					name: 'undo',
					disabled:false
				},
				redo:{
					icon: 'mdi mdi-redo',
					title: '恢复',
					name: 'redo',
					disabled:false
				},
				paste:{
					icon: 'mdi mdi-content-paste',
					title: '粘贴',
					name: 'paste'
				},
				cut:{
					icon: 'mdi mdi-content-cut',
					title: '剪切',
					name: 'cut'
				},
				copy:{
					icon: 'mdi mdi-content-copy',
					title: '复制',
					name: 'copy'
				},
				openFormatpainter:{
					icon: 'mdi mdi-brush-variant',
					title: '格式刷',
					name: 'openFormatpainter',
					selected: false
				},
				fontFamily:{
					title: '字体',
					name: 'fontFamily',
					value: 'fontFamily',
					options: ['宋体', '楷体', '仿宋', '微软雅黑', '黑体', 'Calibri', 'Consolas']
				},
				fontSize:{
					title: '字号',
					name: 'fontSize',
					options: [8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 26, 28, 36, 48, 72]
				},
				bold:{
					icon: 'mdi mdi-format-bold',
					title: '加粗',
					name: 'bold',
					checked: false
				},
				italic:{
					icon: 'mdi mdi-format-italic',
					title: '倾斜',
					name: 'italic',
					checked: false
				},
				underline:{
					icon: 'mdi mdi-format-underline',
					title: '下划线',
					name: 'underline',
					checked: false
				},
				strikethrough:{
					icon: 'mdi mdi-format-strikethrough-variant',
					title: '删除线',
					name: 'strikethrough',
					checked: false
				},
				bgColor:{
					icon: 'mdi mdi-format-color-fill',
					title: '背景颜色',
					name: 'bgColor'
				},
				color:{
					icon: 'mdi mdi-format-text-variant',
					title: '前景颜色',
					name: 'color'
				},
				border: {
					title:"边框",
					icon:"mdi mdi-border-all",
					value:"t",
					options:[
						{
							label: '上边框',
							value: 't'
						},
						{
							label: '下边框',
							value: 'b'
						},
						{
							label: '左边框',
							value: 'l'
						},
						{
							label: '右边框',
							value: 'r'
						},
						{
							label: '无边框',
							value: ''
						},
						{
							label: '全边框',
							value: 'blrt'
						}
					]
				},
				
				valigntop:{ title: '顶端对齐', name: 'valigntop', checked: false, icon: 'mdi mdi-align-vertical-top' },
				valignmiddle:{ title: '垂直居中', name: 'valignmiddle', checked: false, icon: 'mdi mdi-align-vertical-center' },
				valignbottom:{ title: '底端对齐', name: 'valignbottom', checked: false, icon: 'mdi mdi-align-vertical-bottom' },
				alignleft:{ title: '左对齐', name: 'alignleft', checked: false, icon: 'mdi mdi-align-horizontal-left' },
				aligncenter:{ title: '水平居中', name: 'aligncenter', checked: false, icon: 'mdi mdi-align-horizontal-center' },
				alignright:{ title: '右对齐', name: 'alignright', checked: false, icon: 'mdi mdi-align-horizontal-right' },

				whiteSpace:{
					icon: 'mdi mdi-format-text-wrapping-wrap',
					title: '自动换行',
					name: 'whiteSpace',
					checked: false
				},
				merge:{
					icon: 'mdi mdi-table-merge-cells',
					title: '合并后居中',
					name: 'merge'
				},
				cellFormat: {
					title: '单元格格式',
					name: 'cellFormat',
					value: 'routine',
					options: [
						{
							label: '常规',
							value: 'routine'
						},
						{
							label: '数值',
							value: 'number'
						},
						{
							label: '货币',
							value: 'currency'
						},
						{
							label: '会计专用',
							value: 'specialAccounting'
						},
						{
							label: '短日期',
							value: 'shortDate'
						},
						{
							label: '长日期',
							value: 'longDate'
						},
						{
							label: '时间',
							value: 'time',
						},
						{
							label: '百分比',
							value: 'percentage'
						},
						{
							label: '文本',
							value: 'text'
						},
						{
							label: '千分分割样式',
							value: 'separators'
						},
						{
							label: '特殊',
							value: 'special'
						},
						{
							label: '自定义',
							value: 'custom'
						},
					],
				},
			},
			curCell: null
		};
	},
	computed: {
		$sheet() {
			// TODO [0] 是当前sheet，会有多个sheet
			return this.$parent.getCurSheet()[0];
		},
		start() {
			return {rowIndex:0,columnIndex:0};
			// return this.$sheet.selection.start || {rowIndex:0,columnIndex:0};
		},
		//      $cell(){
		//        const cell = this.$sheet.getCurCell();
		//        return cell;
		//      },
	},
	watch: {
		start() {
			this.curCell = this.$sheet.getCurCell();
		},
		
		curCell() {
		    let style;
		    if (this.$cell) {
		        style = this.$sheet.getStyle(this.$cell.s);
		    }
			console.log(style);
		
		    if (this.items) {
		        this.items.map(item => {
					console.log(item);
					if (typeof item == 'string') {
					    this[item] = style ? style.getOption(item) : undefined;
					} else {
					    this[item.key] = style ? ( style.getOption(item.key) || item.value ) : item.value;
					}
		        });
		    }
		
		},
		'$parent.sheetIndex': {
			handler() {
				this.init()
			},
		},
	},
	mounted() {
		this.init();
		const _this = this;
		this.$parent.$on('selectCell', function() {
			const pos = _this.$sheet.selctionExpand.start;
			let cell = _this.$sheet.getPosCell(pos);
			const cellFormat = _this.toolbars.cellFormat;
			if(typeof(cell) != 'undefined' && typeof(cell.fc) != 'undefined') {
				cellFormat.value = cell.fc;
			} else {
				cellFormat.value = 'routine';
			}
			Object.assign(_this.toolbars, cellFormat);
			// TODO 样式设置回去
		});
	},
	methods: {
		init() {
			let _this = this;
			// 有sheet有变更时修改状态
			this.$sheet.$on('on-cellval-change', function() {
				_this.setUndoRedo();
			});
			_this.setUndoRedo();
		},
		toolbarEvent(e) {
			if(this.hasOwnProperty(e)){
				let args = Object.assign([],arguments);
				args.shift();
				this[e].apply(args)
			}else{
				if(e.substr(0,6)=='valign'){
					this.setVerticalAlign(e.substr(6));
					this.toolbars['valigntop'].checked = e.substr(6) == 'top';
					this.toolbars['valignmiddle'].checked = e.substr(6) == 'middle';
					this.toolbars['valignbottom'].checked = e.substr(6) == 'bottom';
					return ;
				}
				if(e.substr(0,5)=='align'){
					this.setTextAlign(e.substr(5));
					this.toolbars['alignleft'].checked = e.substr(5) == 'left';
					this.toolbars['aligncenter'].checked = e.substr(5) == 'center';
					this.toolbars['alignright'].checked = e.substr(5) == 'right';
					return ;
				}
				console.log(e);
			}
		},
		setUndoRedo() {
			this.toolbars['undo'].disabled = !(this.$sheet.undoList.length > 0);
			this.toolbars['redo'].disabled = !(this.$sheet.redoList.length > 0);
		},
		undo() {
			if (this.$sheet.undoList.length > 0) {
				this.$sheet.doUndo();
			}
			this.setUndoRedo();
		},
		redo() {
			if ((this.$sheet.redoList.length > 0)) {
				this.$sheet.doRedo();
			}
			this.setUndoRedo();
		},
		copy() {
			this.$sheet.doCopySelection();
		},
		cut() {
			this.$sheet.doCutSelection();
		},
		paste() {
			this.$sheet.doPasteSelection();
		},
		openFormatpainter() {
			this.$sheet.doOpenFormatpainter();
			this.toolbars['openFormatpainter'].selected = this.$sheet.curAction == 'formatpainter';
		},
		fontFamily(){
			this.$sheet.setFontFamily(this.toolbars['fontFamily'].value);
		},
		fontSize() {
			this.$sheet.setFontSize(this.toolbars['fontSize'].value);
		},
		bold() {
			this.toolbars['bold'].value = this.toolbars['bold'].value ? undefined : 'bold';
			this.$sheet.setFontWeight(this.toolbars['bold'].value);
		},
		italic() {
			this.toolbars['italic'].value = this.toolbars['italic'].value ? undefined : 'italic';
			this.$sheet.setFontStyle(this.toolbars['italic'].value);
		},
		underline(){
			console.log('under')
			this.setTextDecoration('underline');
		},
		strikethrough(){
			this.setTextDecoration('line-through');
		},
		setTextDecoration(decoration) {
			let key = decoration == 'line-through' ? 'strikethrough' : decoration;
			this.toolbars[key].value = this.toolbars[key].value === decoration ? undefined : decoration;
			this.$sheet.setTextDecoration(this.toolbars[key].value);
		},
		bgColor() {
			this.$sheet.setBackground(this.toolbars['bgColor'].value);
		},
		color() {
			this.$sheet.setFillColor(this.toolbars['color'].value);
		},
		border() {
			this.$sheet.setBorder(this.toolbars['border'].value);
		},
		
		setTextAlign(type) {
			this.textAlign = type;
			this.$sheet.setTextAlign(this.textAlign);
		},
		setVerticalAlign(type) {
			this.verticalAlign = type;
			this.$sheet.setVerticalAlign(this.verticalAlign);
		},
		whiteSpace() {
			this.toolbars['whiteSpace'].value = this.toolbars['whiteSpace'].value == 'normal' ? undefined : 'normal';
			this.$sheet.setSelctionExpandStyle({
				whiteSpace: this.toolbars['whiteSpace'].value
			});
		},
		merge() {
			this.$sheet.doMergeCell('center');
		},
		// 格式化单元格
		cellFormat() {
			console.log('cellFormat value', this.toolbars['cellFormat'].value);
			this.$sheet.setFormat(this.toolbars['cellFormat'].value);
		}
	}
};
</script>

<style lang="scss">
.meg-menu-op-color {
	position: relative;
}

.meg-menu-op {
	display: inline-block;
	border: none;
	min-width: 16px;
	min-height: 16px;
	text-align: center;
	border: 1px solid transparent;
	font-size: 0px;
	padding: 2px;
	vertical-align: top;
	&:hover {
		border-color: #d3f0e0 !important;
		outline: 2px dashed transparent;
	}
	.meg-menu-pcol {
		display: inline-block;
		height: 4px;
		width: 16px;
		position: absolute;
		bottom: 2px;
		left: 2px;
	}
	&.display {
		opacity: 0.5 !important;
		&:hover {
			background-color: transparent !important;
			border-color: transparent !important;
		}
	}
}

.meg-menu {
	height: 40px;
	font-size: 12px;
	color: #666;
	user-select: none;
	border-bottom: 1px solid #ddd;
	
	border-right: 1px solid #e1e1e1;
	padding: 0px 6px;
	position: relative;
	align-items: center;
	display: flex;
	button {
		border: 0;
		min-width: 40px;
		padding: 0;
		background: transparent;
		color: #999;
		i {
			font-size: 20px;
		}
		&[checked],
		&[selected],&:hover{
			color: #409EFF;
		}
	}
	.meg-menu-dropdown {
		min-width: 40px;
		padding: 0;
		.meg-menu-dropdown-handler {
			font-size: 20px;
			height: 100%;
			width: 100%;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-around;
			.meg-menu-op .meg-menu-pcol {
				left: 19px;
				bottom: 2px;
			}
		}
	}
	.el-select {
		min-width: 100px;
	}
	.el-input__inner {
		border: 0;
	}
}
</style>
