<template>
	<div style="width: 100%;height: 100%;">
		<div id="ExcelEditor" ref="ExcelEditor">
			<div class="header">
				<div class="flex_row space-between">
					<a-button type="link" icon="left"></a-button>
					<a-input v-model="title" class="inputbb" placeholder="请输入内容"></a-input>
				</div>
				<a-button-group>
					<a-button type="primary">设计</a-button>
					<a-button type="default">数据</a-button>
					<a-button type="default">设置</a-button>
				</a-button-group>
				<a-button-group>
					<a-button type="default" @click="viewData">查看代码</a-button>
					<a-button type="primary">发布</a-button>
				</a-button-group>
			</div>
			<div class="flex_row" style="flex:1;">
				<vspread :menu="menu" :autoCreate="autoCreate" @updateOptions="updateOptions" style="flex:1;" />
				<rightPanel></rightPanel>
			</div>
		</div>

		<a-modal :visible="codeData.show" title="代码" width="600px" @close="codeData.show = false">
			<a-textarea v-model="codeData.data" :rows="20" type="textarea" readonly placeholder="Please input" />
		</a-modal>
	</div>
</template>

<script>
import style from '@mdi/font/css/materialdesignicons.min.css';
import vspread from '@/components/exceleditor';
import rightPanel from './rightPanel';
export default {
	name: 'ExcelEditor',
	data() {
		return {
			codeData: {
				show: false,
				data: ''
			},
			title: '未命名表单',
			options: {},
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
		},
		//显示当前json
		viewData() {
			console.log('view');
			this.codeData.data = JSON.stringify(this.$piniastore.data, ' ', 4);
			this.codeData.show = true;
		},
		//保存
		saveData() {},
		//发布
		postData() {
			const loadingInstance = ElLoading.service({ fullscreen: true });
			Api.saveDesign(this.data).then(res => {
				loadingInstance.close();
				ElMessage({
					message: '保存成功',
					type: 'success'
				});
			});
		},
		//自动保存
		autoSave() {
			setTimeout(this.$piniastore.data, 1000 * 30);
		}
	},
	components: { vspread, rightPanel },
	mounted: function() {
		let data = {
			// 行的信息
			rows: [
				{ hpx: 100, h: false, s: 's1' },
				null,
				null,
				null,
				null,
				null,
				null,
				{ hpx: 56 },
				null,
				null,
				{ hpx: 153 },
				{ hpx: 91 },
				null,
				{ hpx: 145 },
				{ hpx: 85 },
				{ hpx: 128 }
			],
			// 列的信息
			columns: [{ wpx: 100, h: false, s: 's1' }, { wpx: 254 }, { wpx: 461 }],

			// 合并单元格的信息
			merges: [
				{
					start: {
						rowIndex: 0,
						columnIndex: 0
					},
					end: {
						rowIndex: 4,
						columnIndex: 1
					}
				}
			],
			// 单元格信息
			cells: {
				'0': [
					{
						v: 123,
						s: 's2'
					},
					null,
					{
						s: 's2',
						fs: 'A1',
						f: '=RC[-2]',
						v: 123
					},
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null
				],
				'7': [
					null,
					{
						v: '提交',
						c: 'button',
						s: 's4'
					},
					{
						v: 'cansnow',
						c: 'text',
						s: 's4'
					},
					null
				],
				'10': [
					null,
					{
						v: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
						c: 'image',
						s: 's5'
					},
					{
						v: '选项2,选项1',
						c: 'checkbox',
						options: [
							{
								value: '选项1',
								label: '黄金糕'
							},
							{
								value: '选项2',
								label: '双皮奶'
							},
							{
								value: '选项3',
								label: '蚵仔煎'
							},
							{
								value: '选项4',
								label: '龙须面'
							},
							{
								value: '选项5',
								label: '北京烤鸭'
							}
						],
						s: 's4'
					},
					{
						v: '选项4',
						c: 'radio',
						options: [
							{
								value: '选项1',
								label: '黄金糕'
							},
							{
								value: '选项2',
								label: '双皮奶'
							},
							{
								value: '选项3',
								label: '蚵仔煎'
							},
							{
								value: '选项4',
								label: '龙须面'
							},
							{
								value: '选项5',
								label: '北京烤鸭'
							}
						],
						s: 's4'
					}
				],
				'13': [
					null,
					null,
					{
						v: '661223',
						c: 'password',
						s: 's4'
					},
					null,
					null
				],
				'14': [
					null,
					{
						v: null,
						c: 'datetime',
						s: 's4'
					},
					{
						v: 2611,
						c: 'number',
						s: 's4'
					},
					null,
					null,
					null
				],
				'15': [
					null,
					null,
					{
						v: '选项2',
						c: 'select',
						options: [
							{
								value: '选项1',
								label: '黄金糕'
							},
							{
								value: '选项2',
								label: '双皮奶'
							},
							{
								value: '选项3',
								label: '蚵仔煎'
							},
							{
								value: '选项4',
								label: '龙须面'
							},
							{
								value: '选项5',
								label: '北京烤鸭'
							}
						],
						s: 's4'
					},
					null,
					null,
					null,
					null
				],
				'16': [
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					{
						v: null,
						s: 's4'
					},
					{
						v: null,
						s: 's4'
					}
				],
				'17': [
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					{
						v: null,
						s: 's4'
					},
					{
						v: null,
						s: 's4'
					}
				],
				'18': [null, null, null, null, null, null, null, null, null, null, null, null, null],
				'19': [null, null, null, null, null, null, null, null, null, null, null, null, null],
				'20': [null, null, null, null, null, null, null, null, null, null, null, null, null],
				'21': [null, null, null, null, null, null, null, null, null, null, null, null, null]
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
		this.$piniastore.setData(data);
	}
};
</script>
<style>
#app {
	overflow: hidden;
}
</style>
<style lang="scss" scoped>
@import '../myExcel-master/css/font-awesome.4.6.0.css';
.flex_row {
	display: flex;
	flex-direction: row;
}
.flex_col {
	display: flex;
	flex-direction: column;
}
.space-between {
	justify-content: space-between;
}
.inputbb {
	border-width: 0 0 1px 0;
}

.scroll-light::-webkit-scrollbar {
	width: 8px;
	height: 8px;
	background-color: #eee;
}

.scroll-light::-webkit-scrollbar-track {
	background-color: #fff;
}

.scroll-light::-webkit-scrollbar-thumb {
	border-radius: 4px;
	background-color: rgba(0, 0, 0, 0.4);
}

.scroll-light::-webkit-scrollbar-thumb:hover {
	background-color: rgba(0, 0, 0, 0.6);
}

.scroll-dark::-webkit-scrollbar {
	width: 8px;
	height: 8px;
	background-color: transparent;
}

.scroll-dark::-webkit-scrollbar-track {
	background-color: transparent;
}

.scroll-dark::-webkit-scrollbar-thumb {
	border-radius: 4px;
	background-color: rgba(0, 0, 0, 0.2);
}

.scroll-dark::-webkit-scrollbar-thumb:hover {
	background-color: rgba(0, 0, 0, 0.5);
}
#ExcelEditor {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	.meg-workbook {
		flex: 1;
		border-top: 1px solid #ddd;
	}
	> .header {
		padding: 5px 15px;
		display: flex;
		justify-content: space-between;
	}
}
</style>
