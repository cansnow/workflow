import {
	defineStore
} from 'pinia'

const useMainStore = defineStore({
	// store
	// 它用于 devtools 并允许恢复状态
	id: 'main',
	// 一个返回新状态的函数
	state: () => ({
		data:{},
		counter: 0,
		name: 'Eduardo',
		previewData: {
			title: '', // 表单名称
			cells: [], // 单元格
			start: '', // 开始行
			end: '', // 开始列
			formList: [], // 权限规则
			dataList: [], // 回写规则
			freezeColumn: 0, // 冻结行
			freezeRow: 0, // 冻结列
		}, // 预览数据
	}),
	// getters
	getters: {
		doubleCount() {
			return this.counter * 2
		},
		//在getters中使用 其它getters 
		doubleCountPlusOne() {
			return this.doubleCount * 2 + 1
		},
	},
	// actions
	actions: {
		setData(v){
			this.data = v;
		},
		setPreviewData(data) {
			this.previewData = data;
		},
		reset() {
			// `this` 使 store 实例
			this.counter = 0
		},
	},
})
export default useMainStore