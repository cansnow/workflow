<template>
	<div class="meg-menu">
		<el-tooltip :content="tool.title" placement="top" v-for="(tool, name) in toolbars" :key="name" :hide-after="500">
			<Dropdown class="meg-menu-op meg-menu-op-color" v-if="name == 'color' || name == 'bgColor' || name == 'borderColor'">
				<i :class="tool.icon" @click="toolbarEvent(name,$event)"></i>
				<span class="meg-menu-pcol" :style="{ background: tool.value }" @click="toolbarEvent(name,$event)"></span>
				<Colorpick slot="content" v-model="tool.value" @input="toolbarEvent(name,$event)"></Colorpick>
			</Dropdown>	
			<Dropdown class="meg-menu-op" v-else-if="name == 'border' || name == 'formula'">
				<i @click="toolbarEvent(name,$event)" v-if="name == 'border'" :class="tool.icon"></i>
				<span style="font-size: 14px;" v-else>{{ tool.title }}</span>
				<m-Select slot="content" v-model="tool.value" :options="tool.options" @input="toolbarEvent(name,$event)"></m-Select>
			</Dropdown>

			<el-select v-else-if="tool.options" :style="tool.style ? tool.style : ''" v-model="tool.value" @change="toolbarEvent(name,$event)" placeholder="请选择">
				<el-option
					v-for="(option,index) in tool.options"
					:value="option.value ? option.value : option"
					:label="option.label ? option.label : option"
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
				// paste:{
				// 	icon: 'mdi mdi-content-paste',
				// 	title: '粘贴',
				// 	name: 'paste'
				// },
				// cut:{
				// 	icon: 'mdi mdi-content-cut',
				// 	title: '剪切',
				// 	name: 'cut'
				// },
				// copy:{
				// 	icon: 'mdi mdi-content-copy',
				// 	title: '复制',
				// 	name: 'copy'
				// },
				openFormatpainter:{
					icon: 'mdi mdi-brush-variant',
					title: '格式刷',
					name: 'openFormatpainter',
					selected: false
				},
				resetStyle: {
					icon: 'mdi mdi-eraser-variant',
					title: '清除格式',
					name: 'resetStyle',
					selected: false
				},
				fontFamily:{
					title: '字体',
					name: 'fontFamily',
					style: { width: '150px' },
					value: 'fontFamily',
					options: ['宋体', '楷体', '仿宋', '微软雅黑', '黑体', 'Calibri', 'Consolas']
				},
				fontSize:{
					title: '字号',
					name: 'fontSize',
					style: { width: '100px' },
					value: 11,
					options: [8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 26, 28, 36, 48, 72]
				},
				fontSizePlus: {
					title: '增大字体',
					name: 'fontSizePlus',
					icon: 'mdi mdi-format-annotation-plus',
				},
				fontSizeMinus: {
					title: '减小字体',
					name: 'fontSizeMinus',
					icon: 'mdi mdi-format-annotation-minus',
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
					name: 'bgColor',
					value: '',
				},
				color:{
					icon: 'mdi mdi-format-text-variant',
					title: '前景颜色',
					name: 'color',
					value: '',
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
				borderStyle: {
					value: 'solid',
					title:"边框样式",
					name: 'borderStyle',
					style: { width: '80px' },
					options:[
						{
							label: '点状',
							value: 'dotted'
						},
						{
							label: '实线',
							value: 'solid'
						},
						// {
						// 	label: '双线',
						// 	value: 'double'
						// },
						{
							label: '虚线',
							value: 'dashed'
						},
					],
				},
				borderColor: {
					icon: 'mdi mdi-border-color',
					title: '边框颜色',
					name: 'borderColor',
					value: '',
				},
				borderBold: {
					value: '1',
					title:"边框尺寸",
					name: 'borderBold',
					style: { width: '80px' },
					options:[
						{
							label: '细线',
							value: '1'
						},
						{
							label: '粗线线',
							value: '2'
						},
					],
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
					name: 'merge',
					checked: false,
				},
				cellFormat: {
					title: '单元格格式',
					name: 'cellFormat',
					value: 'routine',
					style: { width: '130px' },
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
							label: '千位分割样式',
							value: 'separators'
						},
						{
							label: '正号',
							value: 'plus',
						},
						{
							label: '负号',
							value: 'minus',
						},
						// {
						// 	label: '特殊',
						// 	value: 'special'
						// },
						// {
						// 	label: '自定义',
						// 	value: 'custom'
						// },
					],
				},
				increase:{
					icon: 'mdi mdi-decimal-increase',
					title: '增加小数点',
					name: 'increase'
				},
				decrease:{
					icon: 'mdi mdi-decimal-decrease',
					title: '减少小数点',
					name: 'decrease'
				},
				formula: {
					title:"公式",
					name: 'formula',
					value:"t",
					options:[
						{
							label: '求和',
							value: 'sum'
						},
						{
							label: '平均值',
							value: 'average'
						},
						{
							label: '计数',
							value: 'count'
						},
						{
							label: '最大值',
							value: 'max'
						},
						{
							label: '最小值',
							value: 'min'
						},
						// {
						// 	label: '全部函数',
						// 	value: 'all'
						// },
					]
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
			if(typeof(cell) != 'undefined' && cell && typeof(cell.fc) != 'undefined') {
				cellFormat.value = cell.fc;
			} else {
				cellFormat.value = 'routine';
			}
			
			// 样式设置回去
			const fontFamily = _this.toolbars.fontFamily
			const fontSize = _this.toolbars.fontSize;
			const bgColor = _this.toolbars.bgColor;
			const colorTemp = _this.toolbars.color;
			const borderStyle = _this.toolbars.borderStyle;

			const borderColor = _this.toolbars.borderColor;
			const borderBold = _this.toolbars.borderBold;

			const valigntop = _this.toolbars.valigntop;
			const valignmiddle = _this.toolbars.valignmiddle;
			const valignbottom = _this.toolbars.valignbottom;
			valigntop.checked = false;
			valignmiddle.checked = false;
			valignbottom.checked = false;

			const alignleft = _this.toolbars.alignleft;
			const aligncenter = _this.toolbars.aligncenter;
			const alignright = _this.toolbars.alignright;
			alignleft.checked = false;
			aligncenter.checked = false;
			alignright.checked = false;

			const merge = _this.toolbars.merge;
			merge.checked = false;

			const whiteSpace = _this.toolbars.whiteSpace;
			whiteSpace.checked = false;

			const bold = _this.toolbars.bold;
			bold.checked = false;
			const italic = _this.toolbars.italic;
			italic.checked = false;

			// 下划线
			const underline = _this.toolbars.underline;
			underline.checked = false;
			// 删除线
			const strikethrough = _this.toolbars.strikethrough;
			strikethrough.checked = false;


			fontSize.value = 11;
			fontFamily.value = 'fontFamily';
			bgColor.value = '';
			colorTemp.value = '';
			borderStyle.value = 'solid';
			borderColor.value = '';
			borderBold.value = '1';
			if (!!cell && typeof cell.s != 'undefined') {
				const style = _this.$sheet.getStyle(cell.s);
				// 设置字号
				if (!!style.option.fontSize) {
					fontSize.value = style.option.fontSize;
				}
				// 设置字体
				if (!!style.option.fontFamily) {
					fontFamily.value = style.option.fontFamily;
				}
				// 设置背景色
				if (!!style.option.backgroundColor) {
					bgColor.value = style.option.backgroundColor;
				}
				// 设置字体颜色
				if (!!style.option.color) {
					colorTemp.value = style.option.color;
				}
				// 设置边框样式
				if (!!style.option.borderStyle) {
					borderStyle.value = style.option.borderStyle;
				}
				// 设置边框颜色
				if (!!style.option.borderColor) {
					borderColor.value = style.option.borderColor;
				}
				// 设置边框尺寸
				if (!!style.option.borderBold) {
					borderBold.value = style.option.borderBold;
				}
				// 设置水平
				if (!!style.option.textAlign) {
					alignleft.checked = style.option.textAlign == 'left';
					aligncenter.checked = style.option.textAlign == 'center';
					alignright.checked = style.option.textAlign == 'right';
				}
				// 设置垂直
				if (!!style.option.verticalAlign) {
					valigntop.checked = style.option.verticalAlign == 'top';
					valignmiddle.checked = style.option.verticalAlign == 'middle';
					valignbottom.checked = style.option.verticalAlign == 'bottom';
				}
				// 设置换行
				if (!!style.option.whiteSpace) {
					whiteSpace.checked = true;
				}
				// 设置粗体
				if (!!style.option.fontWeight) {
					bold.checked = true;
				}
				// 设置斜体
				if (!!style.option.fontStyle) {
					italic.checked = true;
				}
				// 设置下划线
				if (!!style.option.textDecoration) {
					underline.checked = true;
				}
				// 设置删除线
				if (!!style.option.textDel) {
					strikethrough.checked = true;
				}
			}
			// 设置合并
			const selection = _this.$sheet.selection;
			const seletction2 = _this.$sheet.s_computedExtendSelection(selection);
			if (JSON.stringify(seletction2) != JSON.stringify(selection)) {
				merge.checked = true;
			}
			Object.assign(_this.toolbars, {
				cellFormat,
				fontSize,
				fontFamily,
				bgColor,
				color:
				colorTemp,
				borderStyle,
				valigntop,
				valignmiddle,
				valignbottom,
				alignleft,
				aligncenter,
				alignright,
				merge,
				whiteSpace,
				bold,
				italic,
				underline,
				strikethrough,
				borderColor,
				borderBold,
			});
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
		resetStyle() {
			this.$sheet.resetStyle();
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
			// this.setTextDecoration('line-through');
			this.setTextDel('line-through');
		},
		setTextDel(decoration) {
			this.toolbars['strikethrough'].value = this.toolbars['strikethrough'].value === decoration ? undefined : decoration;
			this.$sheet.setTextDel(this.toolbars['strikethrough'].value);
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
		borderColor() {
			this.$sheet.setBorderColor(this.toolbars['borderColor'].value);
		},
		borderBold() {
			this.$sheet.setBorderBold(this.toolbars['borderBold'].value);
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
		},
		// 增大字体
		fontSizePlus() {
			const fontSize = this.toolbars['fontSize'];
			const len = fontSize.options.length;
			const index = fontSize.options.findIndex(item => item == fontSize.value);
			if (index == len - 1) {
				return;
			}
			fontSize.value = fontSize.options[index + 1];
			Object.assign(this.toolbars['fontSize'], { fontSize });
			this.$sheet.setFontSize(fontSize.value);
		},
		// 减小字体
		fontSizeMinus() {
			const fontSize = this.toolbars['fontSize'];
			const index = fontSize.options.findIndex(item => item == fontSize.value);
			if (index == 0) {
				return;
			}
			fontSize.value = fontSize.options[index - 1];
			Object.assign(this.toolbars['fontSize'], { fontSize });
			this.$sheet.setFontSize(fontSize.value);
		},
		// 边框样式
		borderStyle() {
			this.$sheet.setBorderStyle(this.toolbars['borderStyle'].value);
		},
		// 减少小数位数
		decrease() {
			this.$sheet.setDecimal('decrease');
		},
		// 增加小数位数
		increase() {
			this.$sheet.setDecimal('increase');
		},
		// 公式
		formula() {
			this.$sheet.doEditCell();
			console.log('formula', this.toolbars['formula'].value);
			this.$sheet.setFormaluValue(this.toolbars['formula'].value);
		},
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
	// font-size: 0px;
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
