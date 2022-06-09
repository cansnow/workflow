<template>
	<div id="ExcelEditor" ref="ExcelEditor"><vspread :options="options" :menu="menu" :autoCreate="autoCreate" @updateOptions="updateOptions" /></div>
</template>

<script>
import style from '@mdi/font/css/materialdesignicons.min.css'
import vspread from '@/components/exceleditor';
//选项
const options = {
	// 行的信息
	rows: [
		{
			hpx: 100, //行高
			h: false, //是否隐藏
			s: 's1' //对应RCStyles中的样式ID
		}
	],
	// 列的信息
	columns: [
		{
			wpx: 100, //列宽
			h: false, //是否隐藏
			s: 's1' //对应RCStyles中的样式ID
		}
	],
	// 合并单元格的信息
	merges: [
		{
			start: {
				rowIndex: 0,
				columnIndex: 0
			},
			end: {
				rowIndex: 1,
				columnIndex: 1
			}
		}
	],
	// 单元格信息
	cells: {
		0:[
			{
				v: 123, //单元格的值
				s: 's2' //样式ID
			},
			null,
			{
				s: 's2',
				fs: 'A1' //公式
			}
		],
		1:[
			null,
			null,
			null,
			null,
			null,
			{
				v: 123, //单元格的值,
				e:'text',
				s: 's2' //样式ID
			},
		]		
	},
	// 样式信息
	styles: {
		s2: {
			border: 'blrt',
			backgroundColor: '#ffff00',
			color: '#ff1400',
			fontFamily: '微软雅黑',
			fontSize: 12,
			fontStyle: 'italic',
			fontWeight: 'bold',
			textDecoration: 'underline',
			textAlign: 'center',
			verticalAlign: 'middle',
			whiteSpace: 'normal'
		},
		s3: {
			border: 'blrt',
			backgroundColor: '#ccc',
			color: '#ff1400',
			fontFamily: '微软雅黑',
			fontSize: 12,
			fontStyle: 'italic',
			fontWeight: 'bold',
			textDecoration: 'underline',
			textAlign: 'center',
			verticalAlign: 'middle',
			whiteSpace: 'normal'
		}
	},
	//行列样式
	RCStyles: {
		s1: {
			border: undefined,
			backgroundColor: undefined,
			color: '#ff1400',
			fontFamily: '微软雅黑',
			fontSize: 12,
			fontStyle: 'italic',
			fontWeight: 'bold',
			textDecoration: 'underline',
			textAlign: 'center',
			verticalAlign: 'middle',
			whiteSpace: 'normal'
		}
	},
	//行的数量
	rowCount: 200,
	//列的数量
	columnCount: 20,
	//允许的最大行
	maxRowCount: 10000,
	//允许的最大列
	maxColumnCount: 200
};

export default {
	name: 'ExcelEditor',
	data() {
		return {
			options,
			//是否显示菜单
			menu: true,
			//是否自动补齐行列
			autoCreate: true
		};
	},
	computed: {},
	methods: {
		updateOptions(value) {
			this.options = Object.assign(this.options, value);
		}
	},
	components: { vspread },
	mounted: function() {}
};
</script>

<style lang="scss" scoped>
@import '../myExcel-master/css/font-awesome.4.6.0.css';
#ExcelEditor {
	width: 100%;
	height: 100%;
}
</style>
