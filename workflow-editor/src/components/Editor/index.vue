<template>
	<div style="width: 100%;height: 100%;">
		<div id="ExcelEditor" ref="ExcelEditor">
			<div class="header">
				<div class="flex_row space-between">
					<el-button type="text" icon="arrow-left"></el-button>
					<el-input v-model="title" class="inputbb" placeholder="请输入内容"></el-input>
				</div>
				<el-button-group>
					<el-button type="primary">设计</el-button>
					<el-button type="default">数据</el-button>
					<el-button type="default" @click="saveData">设置</el-button>
				</el-button-group>
				<el-button-group>
					<el-button type="default" @click="viewData">查看代码</el-button>
					<el-button type="primary">发布</el-button>
				</el-button-group>
			</div>
			<div class="flex_row" style="flex:1;">
				<vspread ref="vspread" @updateOptions="updateOptions" @selectCell="handleSelectCell" style="flex:1;" />
				<rightPanel ref="rightPanel" @formChange="handleFormChange"></rightPanel>
			</div>
		</div>

		<el-dialog :visible.sync="codeData.show" title="代码" width="600px" @close="codeData.show = false">
			<el-textarea v-model="codeData.data" :rows="20" type="textarea" readonly placeholder="Please input" />
		</el-dialog>
	</div>
</template>

<script>
import style from '@mdi/font/css/materialdesignicons.min.css';
import vspread from '@/components/exceleditor';
import rightPanel from './rightPanel';
import testData from './testData';
export default {
	name: 'ExcelEditor',
	data() {
		return {
			codeData: {
				show: false,
				data: ''
			},
			data:{},
			title: '未命名表单',
			options: {},
		};
	},
	computed: {
		// 点击单元格
		selection: function() {
			return this.$refs.vspread.getCurSheet()[0].selection;
		},
		curCell: function() {
			return this.$refs.vspread.getCurSheet()[0].getCurCell();
		},
	},
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
		saveData() {
			this.setCell({
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
		});
		},

		// 编辑单元格
		setCell(data) {
			this.$refs.vspread.getCurSheet()[0].setCellValue(this.selection.start, data);
		},

		// 修改表单
		handleFormChange(data) {
			let temp = {
				v: null,
				c: null,
				s: 's4',
			};
			switch(data.componentType) {
				case 'input': // 单元格输入
					// text number password datetime
					temp.c = data.inputType;
					temp.v = data.default.value;
					break;
				case 'select': // TODO 下拉树未做 单元格选择
					// checkbox radio select
					temp.c = data.selectType;
					if (
						temp.c == 'dropdownRadio' ||
						temp.c == 'dropdownCheckbox' ||
						temp.c == 'dropdownRadioTree' ||
						temp.c == 'dropdownCheckboxTree'
					) {
						temp.c = 'select';
					}
					const valueList = data.default.value.split(',');
					const options = [];
					valueList.forEach(item => {
						options.push({ value: item, label: item });
					});
					temp.options = options;
					break;
				case 'upload': // TODO 上传
					break;
				case 'image': // 图片
					temp.c = data.componentType;
					temp.v = data.imageUrl;
					break;
				case 'button': // 按钮
					temp.c = data.componentType;
					temp.v = data.buttonText;
					if (temp.v == '') {
						temp.v = data.buttonType == 'submit' ? '提交' : '重置';
					} 
					break;
				default:
					temp = null;
					break;
			}
			if (temp != null) {
				this.setCell(temp);
			} else {
				this.setCell({ v: null });
			}
		},
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
		},
		handleSelectCell() {
			console.log('handleSelectCell curCell', this.curCell);
			if(this.curCell != null) {
				this.$refs.rightPanel.updataForm(this.curCell);
			} else {
				this.$refs.rightPanel.resetForm();
			}
		},
	},
	components: { vspread, rightPanel },
	created: function() {
		this.$piniastore.setData(testData);
	}
};
</script>
<style>
#app {
	overflow: hidden;
}
</style>
<style lang="scss" scoped>
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
