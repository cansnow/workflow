<template>
	<div style="width: 100%;height: 100%;">
		<div id="ExcelEditor" ref="ExcelEditor">
			<div class="header">
				<div class="flex_row space-between">
					<el-button type="text" icon="arrow-left"></el-button>
					<el-input v-model="title" @change="handleChangeTitle" class="inputbb" placeholder="请输入内容"></el-input>
				</div>
				<!-- <el-button-group>
					<el-button type="primary">设计</el-button>
					<el-button type="default">数据</el-button>
					<el-button type="default" @click="saveData">设置</el-button>
				</el-button-group> -->
				<el-button-group>
					<el-button type="default" @click="viewData">查看代码</el-button>
					<el-button type="primary">发布</el-button>
				</el-button-group>
			</div>
			<div class="flex_row" style="flex:1;">
				<vspread
					ref="vspread"
					@updateOptions="updateOptions"
					@selectCell="handleSelectCell"
					@selectEnd="handleSelectEnd"
					@click-head="handleClickHead"
					@changSheet="handleChangeSheet"
					style="flex:1;"
				/>
				<rightPanel ref="rightPanel" @formChange="handleFormChange" @showSelectCells="showSelectionCells"></rightPanel>
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
	name: 'excelDesign',
	data() {
		return {
			codeData: {
				show: false,
				data: ''
			},
			data:{},
			title: '未命名表单',
			options: {},
			sheetIndex: 0,
		};
	},
	computed: {
		// 点击单元格
		selection: function() {
			return this.$refs.vspread.getCurSheet()[0].selection;
		},
		// title(newValue) {
		// 	this.$refs.vspread.getCurSheet()[0].data[sheetIndex];
		// 	this.$piniastore.setData(allData);
		// },
	},
	methods: {
		handleChangeTitle(e) {
				const data = this.$refs.vspread.data;
				data[this.sheetIndex].title = e;
				this.$piniastore.setData(data);
		},
		handleChangeSheet(data) {
			this.title = data.title;
			this.sheetIndex = data.index;
		},
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
		},
		// 当前单元格
		curCell() {
			return this.$refs.vspread.getCurSheet()[0].getCurCell();
		},
		// 选择单元格
		handleSelectCell() {
			this.$nextTick(function() {
				const curCell = this.curCell();
				console.log('handleSelectCell curCell', curCell);
				if(curCell != null) {
					this.$refs.rightPanel.updataForm(curCell);
				} else {
					this.$refs.rightPanel.resetForm();
				}
			});
		},
		// 编辑单元格
		setCell(data) {
			const curSheet = this.$refs.vspread.getCurSheet()[0];
			curSheet.setUpdateCellType(null);
			curSheet.doEditCell();
			setTimeout(function() {
				curSheet.doCancelEdit();
				curSheet.setUpdateCellType(data.c);
				curSheet.doEditCellValue(data);
			}, 100);
		},
		// 修改表单
		handleFormChange(data) {
			let temp = {
				v: null,
				c: 'Cell',
				p: {
						r: {
								s: true, // show 可见
								r: [''], // [0] 可见规则， [1] 可编辑规则
						}, // rule 权限
				},
			};
			switch(data.componentType) {
				case 'input': // 单元格输入
					// text number password datetime
					temp.c = data.inputType;
					temp.v = data.default.value;
					break;
				case 'select': // 单元格选择
					// checkbox radio select selectM
					temp.c = data.selectType;
					// 非树结构
					const valueList = data.default.value.split(',');
					let options = [];
					valueList.forEach(item => {
						options.push({ value: item, label: item });
					});
					// 树结构 将字符串转JSON对象
					if (
						data.selectType == 'treeSelect' ||
						data.selectType == 'treeSelectMultiple'
					) {
						options = JSON.parse(data.default.value);
					}
					// 多选 v 是数组
					if (
						data.selectType == 'treeSelectMultiple' ||
						data.selectType == 'selectMultiple'
					) {
						temp.v = [];
					}
					temp.options = options;
					break;
				case 'upload': // TODO 上传
					temp.c = data.componentType;
					temp.v = [];
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
			}
			// componentType 是 input select upload
			if (
				data.componentType == 'input' ||
				data.componentType == 'select' ||
				data.componentType == 'upload'
			) {
				// TODO upload t
				const props = {
						f: data.formFiled, // filed 字段
						vd: {
								r: data.check.noNull, // Required 必填
								u: data.check.only, // unique 唯一
						}, // validate 验证
						r: {
								s: data.power.ifShow, // show 可见
								w: data.power.ifEdit, // write 可写
								r: [
									data.power.showCondition, 
									data.power.editCondition,
								], // [0] 可见规则， [1] 可编辑规则
						}, // rule 权限
				};
				if (temp.componentType == 'upload') {
					Object.assign(props, { t: data.uploadType });
				}
				Object.assign(temp, { p: props });
			}
			// componentType 是 image button
			if (
				data.componentType == 'image' ||
				data.componentType == 'button' ||
				data.componentType == 'Cell'
			) {
				const props = {
						r: {
								s: data.power.ifShow, // show 可见
								r: [data.power.showCondition], // [0] 可见规则， [1] 可编辑规则
						}, // rule 权限
				};
				Object.assign(temp, { p: props });
			}
			// 单元格数据，有公式
			if (data.componentType == 'Cell') {
				Object.assign(temp, { v: data.cellValue });
			}
			this.setCell(temp);
		},
		handleSelectEnd() {
			this.$nextTick(function() {
				this.$refs.rightPanel.setSelectCell(this.selection);
			});
		},
		// 显示已选单元格
		showSelectionCells(data) {
			const { start, end} = data;
			this.$refs.vspread.getCurSheet()[0].setSelectArea(start, end);
		},
		// 点击了头部
		handleClickHead() {
			this.$refs.rightPanel.setHead(this.$refs.vspread.getCurSheet()[0].direction);
		}
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
