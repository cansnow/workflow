<template>
	<div style="width: 300px;height: 100%; padding: 10px;">
		<div style="display: flex;">
			<div style="flex:1;">
				<el-tabs v-model="activeName" @tab-click="handleTabClick" stretch>
					<el-tab-pane label="属性" name="attribute" />
					<el-tab-pane label="数据" name="dataPanel" />
					<el-tab-pane label="全局" name="overallPanel" />
				</el-tabs>
			</div>
			<div style="width: 50px; text-align: center;">
				<el-button type="text">
					<i class="mdi mdi-close"></i>
				</el-button>
			</div>
		</div>
		<div style="overflow: hidden; height: calc(100vh - 120px);">
			<div style="overflow-y: scroll; text-align: left; height: 100%; width: 317px;">
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
		}
	},
	methods: {
		onSubmit() {
			console.log('submit!');
		},
		handleTabClick() {
			console.log('activeName', this.activeName);
		},
		// 表单数据更新
		formChange(data) {
			this.$emit('formChange', data);
		},
		// 数据绑定更新
		dataChange(data) {
			this.$emit('dataChange', data);
		},
		// 全局规则更新
		overallChange(data) {
			this.$emit('overallChange', data);
		},
		// 重置表单信息
		resetForm() {
			this.$refs.FormPanel.resetForm();
		},
		// 更新表单信息
		updataForm(data) {
			this.$refs.FormPanel.updataForm(data);
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