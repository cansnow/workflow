<template>
	<div style="width: 300px;height: 100%; padding: 10px; border: 1px solid #ddd;">
		<div style="display: flex;">
			<div style="flex:1;">
				<el-tabs v-model="activeName" @tab-click="handleTabClick" stretch>
					<el-tab-pane label="属性" name="attribute" />
					<el-tab-pane label="数据" v-if="!cellIfBtn" name="dataPanel" />
					<el-tab-pane label="全局" name="overallPanel" />
				</el-tabs>
			</div>
			<div style="width: 40px; text-align: center; position: relative;">
				<div style="
					position: absolute;
					bottom: 14px;
					left: 0px;
					height: 2px;
					width: 100%;
					background-color: #dfe4ed;
					z-index: 1;"></div>
				<el-button type="text">
					<i class="mdi mdi-close" @click="closePanel"></i>
				</el-button>
			</div>
		</div>
		<!-- <el-button size="mini" @click="test">test</el-button> -->
		<div style="overflow: hidden; height: calc(100% - 64px);">
			<div style="overflow-y: scroll; text-align: left; height: 100%; width: 295px;">
				<div>
					<!-- 单元格表单 -->
					<FormPanel ref="FormPanel" v-show="activeName == 'attribute'"/>
					<!-- 数据绑定 -->
					<DataPanel ref="DataPanel" v-show="activeName == 'dataPanel'"/>
					<!-- 全局规则 -->
					<OverallPanel ref="OverallPanel" v-show="activeName == 'overallPanel'"/>
				</div>
			</div>
		</div>
	</div>   
</template>

<script>
import DataPanel from './src/dataPanel/index.vue';
import OverallPanel from './src/overallPanel/index.vue';
import FormPanel from './src/formPanel/index.vue';
export default {
	provide() {
			return {
					$rightPanel: this,
			};
	},
	components: {
		FormPanel,
		DataPanel,
		OverallPanel,
	},
	data() {
		return {
			activeName: 'attribute',
			selectCell: '',
			head: undefined,
			selection: undefined, // 当前位置
			cellIfBtn: false, // 单元格为按钮
		}
	},
	watch: {
		cellIfBtn(value) {
			if (value) {
				if (this.activeName == 'dataPanel') {
					this.activeName = 'attribute';
				}
			}
		}
	},
	methods: {
		// 获取到位置信息
		setSelectCell(data) {
			const { start, end } = data;
			this.selection = data;
			// columnIndex rowIndex
			// 判断end是在前还是在后，在上还是在下
			if (start.columnIndex == end.columnIndex && start.rowIndex == end.rowIndex) {
				this.selectCell = this.selectPostFormat(start);
			} else {
				let temp = '';
				// columnIndex 相等 rowIndex 相等
				if (start.columnIndex == end.columnIndex || start.rowIndex == end.rowIndex) {
					if (start.rowIndex > end.rowIndex || start.columnIndex > end.columnIndex) {
						temp = this.selectPostFormat(end) + ':' + this.selectPostFormat(start);
					} else {
						temp = this.selectPostFormat(start) + ':' + this.selectPostFormat(end);
					}
				} else {
					// end 在左下角，start 在右上角
					if (start.columnIndex > end.columnIndex && start.rowIndex < end.rowIndex) {
						temp = this.selectPostFormat({ columnIndex: end.columnIndex , rowIndex: start.rowIndex }) +
							':' + this.selectPostFormat({ columnIndex: start.columnIndex , rowIndex: end.rowIndex });
					}
					// start 在左下角，end 在右上角
					if (start.columnIndex < end.columnIndex && start.rowIndex > end.rowIndex) {
						temp = this.selectPostFormat({ columnIndex: start.columnIndex , rowIndex: end.rowIndex }) +
							':' + this.selectPostFormat({ columnIndex: end.columnIndex , rowIndex: start.rowIndex });
					}
					// start 在左上角，end 在右下角
					if (start.columnIndex > end.columnIndex && start.rowIndex > end.rowIndex) {
						temp = this.selectPostFormat(end) + ':' + this.selectPostFormat(start);
					}
					// end 在左上角，start 在右下角
					if (start.columnIndex < end.columnIndex && start.rowIndex < end.rowIndex) {
						temp = this.selectPostFormat(start) + ':' + this.selectPostFormat(end);
					}
				}
				this.selectCell = temp;
			}
		},
		// 格式化select坐标
		selectPostFormat(data) {
			return _.$Number2ABC(data.columnIndex) + (data.rowIndex + 1);
		},
		onSubmit() {
			console.log('submit!');
		},
		handleTabClick() {
			console.log('activeName', this.activeName);
		},
		// 表单数据更新
		formChange(data) {
			this.cellIfBtn = false;
			if (data.componentType == 'button') {
				this.cellIfBtn = true;
			}
			this.$emit('formChange', data);
		},
		// 重置表单信息
		resetForm() {
			this.cellIfBtn = false;
			// this.activeName = 'attribute';
			this.$refs.FormPanel.resetForm();
		},
		// 更新表单信息
		updataForm(data) {
			this.cellIfBtn = false;
			// this.activeName = 'attribute';
			if (data && typeof data != 'undefined' && typeof data.c != 'undefined' && data.c == 'button') {
				this.cellIfBtn = true;
			}
			this.$refs.FormPanel.updataForm(data);
		},
		// 显示已选单元格
		showSelectCells(data) {
			this.$emit('showSelectCells', data);
		},
		// 设置头部信息
		setHead(data) {
			this.head = data;			
		},
		// 全局ref
		getOverallPanelRef() {
			return this.$refs.OverallPanel;
		},
		// 表单ref
		getFormPanelRef() {
			return this.$refs.FormPanel;
		},
		// 数据
		getDataPanelRef() {
			return this.$refs.DataPanel;
		},
		// 关闭面板
		closePanel() {
			this.$emit('closePanel');
		},
	}
}
</script>

<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 150px;
    height: 150px;
    line-height: 150px;
    text-align: center;
  }
  .avatar {
    width: 150px;
    height: 150px;
    display: block;
  }
</style>