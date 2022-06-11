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
import testData from './testData';
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
