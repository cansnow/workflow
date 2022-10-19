<template>
	<div style="width: 100%;height: 100%;">
		<div id="ExcelEditor" ref="ExcelEditor">
			<div class="header">
				<breadcrumb
					id="breadcrumb-container"
					class="breadcrumb-container"
					:matched="matched"
				/>
				<div class="flex_row space-between">
					<!-- <el-button type="text" icon="arrow-left"></el-button> -->
					<!-- <el-input v-if="ifEditTitle" v-model="title" @change="handleChangeTitle" @blur="ifEditTitle = false" class="inputbb" placeholder="请输入内容"></el-input>
					<div v-else @dblclick="handleDblclickTitle">{{ title }}</div> -->
					<el-input v-if="ifEditName" v-model="templateName" @change="handleChangeName" @blur="ifEditName = false" class="inputbb" placeholder="请输入内容"></el-input>
					<div v-else @dblclick="handleDblclickName">{{ templateName }}</div>
				</div>
				<!-- <el-button-group>
					<el-button type="primary">设计</el-button>
					<el-button type="default">数据</el-button>
					<el-button type="default" @click="saveData">设置</el-button>
				</el-button-group> -->
				<rightMenu v-if="headRigth" />
				<div v-else style="color: white;">1</div>
				<!-- <el-button-group>
					<el-button type="primary" @click="handlePreview">预览</el-button>
					<el-button type="default" @click="viewData">查看代码</el-button>
					<el-button type="primary" @click="handleRelease" :disabled="dialogVisible">发布</el-button>
				</el-button-group> -->
			</div>
			<div class="flex_row" style="flex:1; position: relative;">
				<vspread
					ref="vspread"
					@updateOptions="updateOptions"
					@selectCell="handleSelectCell"
					@selectEnd="handleSelectEnd"
					@click-head="handleClickHead"
					@changSheet="handleChangeSheet"
					@menusHeigth="handleMenusHeigth"
					style="flex:1; width: calc(100vw - 300px)"
				/>
				<rightPanel
					v-show="showPanel"
					ref="rightPanel"
					@closePanel="showPanel = false"
					@formChange="handleFormChange"
					@showSelectCells="showSelectionCells"
					style="position: absolute; background-color: white; right: 30px; z-index: 999; border-radius: 6px; box-shadow: 0px 0px 5px #888888;"
					:style="{ height: `calc(100vh - 52px - ${menusHeigth}px - 120px)`, top: `calc(${menusHeigth}px + 38px`}"
				></rightPanel>
			</div>
		</div>

		<el-dialog :visible.sync="codeData.show" title="代码" width="600px" @close="codeData.show = false">
			<el-input v-model="codeData.data" :rows="20" type="textarea" readonly placeholder="Please input" />
		</el-dialog>
		<Dialog
			:dialogVisible="dialogVisible"
			@handleClose="handleClose"
			@handleIsOk="handleIsOk"
			:puncture="!ifPreview"
		>
			<Preview v-if="ifPreview" />
			<ReleaseForm v-else ref="relForm" :options="sheetTitles" />
		</Dialog>
	</div>
</template>

<script>
import style from '@mdi/font/css/materialdesignicons.min.css';
import vspread from '@/components/exceleditor';
import Breadcrumb from "./src/Breadcrumb";
import rightMenu from './src/rightMenu';
import rightPanel from './rightPanel';
import testData from './testData';
import Dialog from './src/dialog.vue';
import ReleaseForm from './src/releaseForm.vue';
import Preview from './preview/index.vue';

import { mapState } from "vuex";

import cache from '@/plugins/cache';

import { formatData } from './src/utils';

// Api
import { addTemplate, updateTemplate, getTemplateInfoById, delTemplateById } from '@/api/editManage';
export default {
	name: 'excelDesign',
	components: { 
		vspread,
		rightPanel,
		Dialog,
		ReleaseForm,
		Preview,
		Breadcrumb,
		rightMenu,
	},
	beforeRouteEnter(to, from, next) {
		next(vm=>{
			console.log('to', to);
			console.log('from', from);
			vm.$nextTick(() => {
				const matched = cache.session.getJSON('DesignMatched');
				if (!!matched && matched.length > 0) {
					vm.matched = matched;
				} else {
					from.matched.forEach(item => {
						vm.matched.push({
							meta: { title: item.meta.title },
							path: item.path,
							redirect: item.redirect,
						});
					});
					if (vm.matched.length <= 0) {
						vm.matched = cache.session.getJSON('DesignMatched');
					} else {
						cache.session.setJSON('DesignMatched', vm.matched);
					}
					vm.matched.push({ path: '/design', meta: { title: '设计器' }});
				}
			});
		});
	},
	data() {
		return {
			codeData: {
				show: false,
				data: ''
			},
			data:{},
			title: '',
			options: {},
			sheetIndex: 0,
			sheetData: [], // sheet数据
			ifPreview: false,
			dialogVisible: false, // 发布弹窗
			updateInfo: {}, // 编辑信息
			sheetTitles: [],
			time: null, // 定时器
			menusHeigth: 40,
			showPanel: false,
			matched: [], // 路由
			tempId: '', // id
			ifEdit: false,
			ifEditTitle: false,
			ifEditName: false,
			templateName: '未命名表单', // 未命名表单
		};
	},
	computed: {
		...mapState({
      headRigth: state => state.settings.headRigth,
    }),
		// 点击单元格
		selection: function() {
			return this.$refs.vspread.getCurSheet()[0].selection;
		},
		$OverallPanel: function() {
			return this.$refs.rightPanel.getOverallPanelRef();
		},
		$DataPanel: function() {
			return this.$refs.rightPanel.getDataPanelRef();
		},
		$curSheet: function() {
			return this.$refs.vspread.getCurSheet()[0];
		},
	},
	watch: {
		ifEditTitle: {
			handler(newV) {
				if (!newV) {
					this.sheetTitles = [];
					this.$refs.vspread.data.forEach((item) => {
						this.sheetTitles.push(item.title);
					});
				}
			}
		},
	},
	methods: {
		handleMenusHeigth(v) {
			this.menusHeigth = v;
		},
		// 修改名称
		handleChangeName(e) {
			Object.assign(this.updateInfo, { title: e });
		},
		handleChangeTitle(e) {
				const data = this.$refs.vspread.data;
				// sheet title 不能同名，用于唯一标识
				const dIndex = data.findIndex(item => item.title == e);
				if (dIndex != -1) {
					this.title = data[this.sheetIndex].title;
					this.$message({
							message: 'sheet 不能同名!!!',
							type: 'warning'
					});
					return;
				}
				const index = this.sheetData.findIndex(item => item.title == data[this.sheetIndex].title);
				if (index != -1) {
					const temp = this.sheetData[index];
					temp.title = e;
					this.sheetData.splice(index, 1, temp); 
				}
				data[this.sheetIndex].title = e;
				this.$piniastore.setData(data);
				this.ifEditTitle = false;
		},
		// 双击sheet修改title
		handleChangeSheetTilte(e) {
			const index = this.sheetData.findIndex(item => item.title == this.title);
			if (index != -1) {
				this.title = e;
				Object.assign(this.sheetData[index], { title: e });
			}
		},
		// 点击sheet tabs
		handleChangeSheet(data) {
			this.title = data.title;
			this.sheetIndex = data.index;
			const index = this.sheetData.findIndex(item => item.title == data.title);
			const temp = {
				formList: [], // 权限规则
				dataList: [], // 回写规则
				searchList: [], // 搜索规则
				start: '', // 开始行列
				end: '', // 结束行列
				dataOptions: [], // 数据面板选项
				pos: 'center',
			};
			if (index == -1) {
				Object.assign(temp, { title: data.title, });
				this.sheetData.push(temp);
			} else {
				const tempData = this.sheetData[index];
				Object.assign(temp, {
					formList: tempData.formList, // 权限规则
					dataList: tempData.dataList, // 回写规则
					searchList: tempData.searchList, // 搜索规则
					dataOptions: tempData.dataOptions, // 数据面板选项
					start: tempData.start, // 开始行列
					end: tempData.end, // 结束行列
					pos: tempData.pos, // 渲染布局
				});
			}
			this.$DataPanel.setSelectData(temp.dataOptions);
			this.$OverallPanel.setData(temp);
			this.$OverallPanel.setFreezeColumn(data.freezeColumn);
			this.$OverallPanel.setFreezeRow(data.freezeRow);
			this.handleSelectCell();
		},
		updateOptions(value) {
			this.options = Object.assign(this.options, value);
		},
		//显示当前json
		viewData() {
			console.log('view');
			// this.codeData.data = JSON.stringify(this.$piniastore.data, ' ', 4);
			const temp = this.getCurSheetCells(this.sheetIndex);
			this.codeData.data = JSON.stringify(temp);
			this.codeData.show = true;
		},
		/** 获取当前sheet全部单元格数据 */
		getCurSheetCells(index = 0) {
			const _this = this;
			const cellsTemp = [];
			const sheet = this.$refs.vspread.getSheetByIndex(index);
			_.each(sheet.cells, (cell, key) => {
				_.each(cell, (col, index) => {
					if (col) {
						const temp = {};
						const selection = {
							start: {columnIndex: index, rowIndex: parseInt(key)}, // 开始位置
							end: {columnIndex: index, rowIndex: parseInt(key)},
						};
						const seletction2 = sheet.s_computedExtendSelection(selection);
						if (JSON.stringify(seletction2) != JSON.stringify(selection)) {
							Object.assign(temp, { merges: seletction2 });// 合并信息
						}
						Object.assign(temp, { pos: selection });
						// getAreaLayoutPos计算的高度宽度会有滚动位置参与计算，所以有问题
						// const pos = sheet.getAreaLayoutPos(selection);
						// 单元格的高度
						const pos = sheet.s_getSelectionRect(selection);
						Object.assign(temp, pos); // 位置 top left height width
						const rowInfo = sheet.getHideCurRow(selection.start.rowIndex); // 获取行信息
						if (!!rowInfo && !!rowInfo.h) {
							Object.assign(temp, { height: rowInfo.hpx, hh: rowInfo.h }); // 隐藏了行
						}
						const colInfo = sheet.getHideCurColumn(selection.start.columnIndex); // 获取列信息
						if (!!colInfo && !!colInfo.h) {
							Object.assign(temp, { width: colInfo.wpx, wh: colInfo.h }); // 隐藏了行
						}
						// console.log('col', col);
						if (typeof(col.s) != 'undefined') {
							const style = sheet.getStyle(col.s);
							// console.log('style.option', style);
							if (style) {
								Object.assign(temp, style.option); // 样式
							}
						}
						Object.assign(temp, col);
						cellsTemp.push(temp);
					}
				})
			});
			// console.log('cellsTemp', cellsTemp);
			return cellsTemp;
		},
		/** 发送后端数据 */
		getCurSaveData(index = 0) {
			const vspreadData =  this.$refs.vspread.data[index];

			const freezeColumn = vspreadData.data.freezeColumn;
			const freezeRow = vspreadData.data.freezeRow;

			const title = vspreadData.title;
			const cells = this.getCurSheetCells(index);

			const temp = { freezeColumn, freezeRow, title, cells };

			const sIndex = this.sheetData.findIndex(item => item.title == title);

			if (sIndex != -1) {
				Object.assign(temp, {
					formList: this.sheetData[sIndex].formList, // 权限规则
					dataList: this.sheetData[sIndex].dataList, // 回写规则
					searchList: this.sheetData[sIndex].searchList, // 搜索规则
					start: this.sheetData[sIndex].start,
					dataOptions: this.sheetData[sIndex].dataOptions, // 数据面板选项
					end: this.sheetData[sIndex].end,
					pos: this.sheetData[sIndex].pos,
				})
			}
			return temp;
		},
		//保存
		saveData() {},
		/** 预览表单 */
		handlePreview() {
			// this.$router.push({path:'/home',query: {id:'1'}})
			// this.$router.push({ path:'/Preview' });
			// this.dialogVisible = true;
			// this.ifPreview = true;
			let url = '';

			const data = []
			this.$refs.vspread.data.forEach((_, index) => {
				data.push(this.getCurSaveData(index));
			});
			// Object.assign(temp, { ifPreview: true });
			// this.$piniastore.setPreviewData(temp);
			cache.session.setJSON('PreviewData', data);

			if (!!this.tempId) {
				url = this.$router.resolve({ path: '/Preview/' + this.tempId });
			} else {
				url = this.$router.resolve({ path: '/Preview' });
			}
      window.open(url.href, '_blank');
		},
		/** 发布 */
		handleRelease() {
			// 编辑
			this.dialogVisible = true;
			if (!!this.tempId) {
				this.$nextTick(() => {
					const index = this.sheetTitles.findIndex(title => title == this.updateInfo.sheet);
					const sheet = index == -1 ? this.title : this.updateInfo.sheet;
					Object.assign(this.updateInfo, { sheet, title: this.updateInfo.title || this.title });
					this.$refs.relForm.setData(this.updateInfo);
				})
			} else {
				this.$nextTick(async () => {
					// 新增
					const data = []
					this.$refs.vspread.data.forEach((_, index) => {
						data.push(this.getCurSaveData(index));
					});
					this.updateInfo = {
						"description": "",
						"link": location && location.origin ? location.origin + '/Renderer' : '/Renderer',
						"sheet": this.title,
						"status": "0",
						"title": this.templateName,
						"data": JSON.stringify(data).replaceAll('<', '&lt;').replaceAll('>', '&gt;'),
					};
					const res = await addTemplate(this.updateInfo);
					if (res.code == 200) {
						this.tempId = res.data.id;
						Object.assign(this.updateInfo, {
							link: this.updateInfo.link + '/' + res.data.id,
							id: res.data.id,
						});						
					}
					this.$refs.relForm.setData(this.updateInfo);
				});
			}
			this.sheetTitles = [];
			this.$refs.vspread.data.forEach((item) => {
				this.sheetTitles.push(item.title);
			});
		},
		/** 取消发布 和 预览 */
		handleClose() {
			if (this.ifPreview) {
				this.ifPreview = false;
			} else {
				this.$refs.relForm.resetForm();
				// 删除记录, 要判断时新增的才删除，编辑不删除
				if (!!this.tempId && !this.ifEdit) {
					const _this = this;
					delTemplateById(this.tempId).then((res) => {
						if (res.code == 200) {
              _this.tempId = '';
            }
					});
				}
			}
			this.dialogVisible = false;
		},
		/** 确定发布 */
		async handleIsOk() {
			clearInterval(this.time); // 清空定时器
			const form = this.$refs.relForm.getFormData();
			const data = [];
			this.$refs.vspread.data.forEach((_, index) => {
				data.push(this.getCurSaveData(index));
			});
			Object.assign(form, { data: JSON.stringify(data).replaceAll('<', '&lt;').replaceAll('>', '&gt;'), });
			// 编辑
			if (!!this.tempId) {
				// 修改
				await updateTemplate(form);
			} else {
				// 新增
				// addTemplate(form).then((result) => {
				// 	console.log('result', result);
				// });
			}
			// this.handleClose();
			window.close();
			// 关闭失败时跳转路径
			const _this = this;
			setTimeout(() => {
				_this.$router.push({ path: '/editManage/excelList' });
			}, 1000);
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
			const _this = this;
			// 定时保存
			this.time = setInterval(() => {
				console.log('autoSave');
				if (!!_this.tempId) {
					const form = _this.updateInfo;
					Object.assign(form, { sheet: _this.title, title: form.title || this.title });
					let data = [];
					if (typeof _this.$refs.vspread != 'undefined') {
						_this.$refs.vspread.data.forEach((_, index) => {
							data.push(_this.getCurSaveData(index));
						});
						Object.assign(form, { data: JSON.stringify(data).replaceAll('<', '&lt;').replaceAll('>', '&gt;'), id: _this.tempId });
						updateTemplate(form).then((result) => {
							console.log('result', result);
						});
					}
				} 
				// else {
				// 	const temp = _this.getCurSaveData(_this.sheetIndex);
				// 	Object.assign(temp, { ifPreview: true });
				// 	cache.session.setJSON('PreviewData', temp);
				// }
			}, 1000 * 30);
		},
		// 当前单元格
		curCell() {
			return this.$refs.vspread.getCurSheet()[0].getCurCell();
		},
		// 选择单元格
		handleSelectCell() {
			this.$nextTick(function() {
				if (!this.showPanel) {
					this.showPanel = true;
				}
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
			let temp = {};
			const curCell = this.curCell();
			if (curCell) {
				['s', 'fs', 'f', 'fc', 'sv', 'fcv', 'd'].forEach(pItem => {
					if (typeof(curCell[pItem]) != 'undefined') {
						temp[pItem] = curCell[pItem];
					}
				});
			}
			
			Object.assign(temp, {
				v: null,
				c: 'Cell',
				p: {
						r: {
								s: true, // show 可见
								r: ['', ''], // [0] 可见规则， [1] 可编辑规则
								w: true, // 可编辑
						}, // rule 权限
				},
			});
			switch(data.componentType) {
				case 'input': // 单元格输入
					// text number password datetime
					temp.c = data.inputType;
					temp.v = data.default;
					break;
				case 'select': // 单元格选择
					// checkbox radio select selectM
					temp.c = data.selectType;
					let options = [];
					if (data.selectSrc == 'custom') {
						// 格式判断，如果是复杂结构，则直接转JSON对象
						if (data.defaultSelect.includes('[')) {
							options = JSON.parse(data.defaultSelect);
						} else {
							// 非树结构
							const valueList = data.defaultSelect.split(',');
							valueList.forEach(item => {
								options.push({ value: item, label: item });
							});
						}
						// // 树结构 将字符串转JSON对象
						// if (
						// 	data.selectType == 'treeSelect' ||
						// 	data.selectType == 'treeSelectMultiple'
						// ) {
						// 	if (!!data.defaultSelect) {
						// 		options = JSON.parse(data.defaultSelect);
						// 	}
						// }
					} else {
						// api
						// apiValue
						// apiLabel
						if (data.apiValue || data.apiLabel) {
							_.map(data.defaultSelect, item => {
								options.push({ 
									value: item[data.apiValue || data.apiLabel],
									label: item[data.apiLabel || data.apiValue]
								});
							});
						}
						// options = data.defaultSelect;
					}
					
					// 多选 v 是数组
					if (
						data.selectType == 'treeSelectMultiple' ||
						data.selectType == 'selectMultiple' ||
						data.selectType == 'checkbox'
					) {
						const value = data.default.split(',');
						temp.v = value;
					} else {
						temp.v = data.default;
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
						if (data.buttonType == 'text') {
							temp.v = data.buttonLink || '超链接';
						} else if (data.buttonType == 'delete') {
							temp.v = '删除';
						} else if (data.buttonType == 'search') {
							temp.v = '搜索';
						} else {
							temp.v = data.buttonType == 'submit' ? '提交' : '重置';
						}
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
								ex: data.checkExtend || '', // 扩展校验
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
				if (data.componentType == 'upload') {
					Object.assign(props, { t: data.uploadType });
				}
				if (data.componentType == 'select') {
					if (data.selectSrc != 'custom') {
						Object.assign(props, { api: data.api, apiValue: data.apiValue, apiLabel: data.apiLabel, apiT: data.apiT });
					}
					Object.assign(props, { ds: data.selectSrc });
				}

				if (
					data.componentType == 'input' ||
					data.componentType == 'select'
				) {
					Object.assign(props, { e: data.extendType, f: data.formFiled, tn: data.tableName });
				}
				if (data.componentType == 'input') {
					Object.assign(props, { ph: data.ph });
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
								r: [data.power.showCondition, ''], // [0] 可见规则， [1] 可编辑规则
								w: true,
						}, // rule 权限
				};
				// 单元格
				if (data.componentType == 'Cell') {
					// 扩展
					Object.assign(props, { e: data.extendType, f: data.formFiled, tn: data.tableName });
					// 超链接 ct 单元格类型 cl 单元格超链接
					Object.assign(props, { ct: data.cellType, cl: data.cellLink });
				}

				if (data.componentType == 'button') {
					Object.assign(props, { t: data.buttonType });
					if (data.buttonType == 'text') {
						Object.assign(props, { bl: data.buttonLink });
					}
				}
				Object.assign(temp, { p: props });
			}
			// 单元格数据，有公式
			if (data.componentType == 'Cell') {
				Object.assign(temp, { v: data.default });
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
		},
		// 双击title
		handleDblclickTitle() {
			console.log('handleDblclickTitle');
			this.ifEditTitle = true;
		},
		// 双击name
		handleDblclickName() {
			this.ifEditName = true;
		},
	},
	mounted() {
		// 定时保存 
		// this.autoSave();
		const _this = this;
		this.$OverallPanel.$on('freezeColumn', function(index) {
			_this.$curSheet.setFreezeColumn(index);
			const data = _this.$refs.vspread.data;
			data[_this.sheetIndex].data.freezeColumn = index;
			_this.$piniastore.setData(data);
		});
		this.$OverallPanel.$on('freezeRow', function(index) {
			_this.$curSheet.setFreezeRow(index);
			const data = _this.$refs.vspread.data;
			data[_this.sheetIndex].data.freezeRow = index;
			_this.$piniastore.setData(data);
		});
		// 监听全局
		this.$OverallPanel.$on('ovserallData', function(data) {
			const index = _this.sheetData.findIndex(item => item.title == _this.title);
			if (index != -1) {
				const temp = _this.sheetData[index];
				Object.assign(temp, {
					formList: data.formList.length > 0 ?  data.formList : temp.formList,
					dataList: data.dataList.length > 0 ?  data.dataList : temp.dataList,
					searchList: data.searchList.length > 0 ? data.searchList : temp.searchList,
					start: data.start == '' ? temp.start : data.start,
					end: data.end == '' ? temp.end : data.end,
					pos: data.pos || temp.pos || 'center',
				});
				_this.sheetData.splice(index, 1, temp);
			}
		});
		this.$DataPanel.$on('dataPanelSelect', function(data) {
			const index = _this.sheetData.findIndex(item => item.title == _this.title);
			if (index != -1) {
				const temp = _this.sheetData[index];
				Object.assign(temp, {
					dataOptions: data instanceof Array && data.length > 0 ? data : temp.dataOptions,
				});
				_this.sheetData.splice(index, 1, temp);
			}
		});
		this.$nextTick(() => {
			this.matched = cache.session.getJSON('DesignMatched') || [];
			if (!!this.matched && this.matched.length > 0) {
				this.matched.push({ path: '/design', meta: { title: '设计器' }});
			}
		});
		// 点击预览
		this.$refs.vspread.$on('handlePreview', function() {
			_this.handlePreview();
		});
		// 点击发布
		this.$refs.vspread.$on('handleRelease', function() {
			_this.handleRelease();
		});
		// 点击保存
		this.$refs.vspread.$on('handleSave', async function() {
			const loading = _this.$loading({
				lock: true,
				text: 'Loading',
				// spinner: 'el-icon-loading',
				// background: 'rgba(0, 0, 0, 0.7)'
			});
			const data = []
			_this.$refs.vspread.data.forEach((_, index) => {
				data.push(_this.getCurSaveData(index));
			});
			if (!!_this.tempId) {
				Object.assign(_this.updateInfo, { data: JSON.stringify(data).replaceAll('<', '&lt;').replaceAll('>', '&gt;'), });
				// 修改
				await updateTemplate(_this.updateInfo).finally(() => {
					loading.close();
				});
				_this.$modal.msgSuccess('保存成功！');
			} else {
				_this.updateInfo = {
					"description": "",
					"link": location && location.origin ? location.origin + '/Renderer' : '/Renderer',
					"sheet": _this.title,
					"status": "0",
					"title": _this.templateName,
					"data": JSON.stringify(data).replaceAll('<', '&lt;').replaceAll('>', '&gt;'),
				};
				const res = await addTemplate(_this.updateInfo).finally(() => {
					loading.close();
				});
				if (res.code == 200) {
					_this.tempId = res.data.id;
					Object.assign(_this.updateInfo, {
						link: _this.updateInfo.link + '/' + res.data.id,
						id: res.data.id,
					});						
				}
			}
		});

		// 双击修改sheet 修改title名
		this.$refs.vspread.$on('handleChangeSheetTilte', this.handleChangeSheetTilte);
	},
	created:  function() {
		this.tempId = this.$route.params.id;
		this.ifEdit = !!this.$route.params.id;
		const tempId = this.$route.params.id;
		const _this = this;
		this.$piniastore.setData(testData);
		// 编辑
		if (!!tempId) {
			getTemplateInfoById(tempId).then((res) => {
				// &lt; < &gt; >
				const data = JSON.parse(res.data.data.replaceAll('&lt;', '<').replaceAll('&gt;', '>'));
				_this.updateInfo = res.data;
				_this.templateName = _this.updateInfo.title || '未命名表单';
				Object.assign(_this.updateInfo, {
					link: location && location.origin ? location.origin + '/Renderer/' + tempId : '/Renderer/' + tempId,
				});
				if (data instanceof Array && res.code == 200) {
					const sheetList = [];
					_this.sheetData = [];
					_.each(data, (item, index) => {
						const temp = formatData(item.cells);
						// _this.$OverallPanel.start = item.start;
						// _this.$OverallPanel.end = item.end;
						// 编辑不需要限制
						Object.assign(temp, {
							rowCount: 200,
							columnCount: 20,
							maxRowCount: 100000,
							maxColumnCount: 200,
							freezeColumn: item.freezeColumn,
							freezeRow: item.freezeRow,
						});
						sheetList.push({
							title: item.title,
							data: temp,
						});
						if (index == 0) {
							_this.title = item.title;
						}
						_this.sheetData.push({
							title: item.title,
							data: temp,
							start: item.start,
							end: item.end,
							freezeColumn: item.freezeColumn,
							freezeRow: item.freezeRow,
							formList: item.formList || [],
							dataList: item.dataList || [],
							searchList: item.searchList || [],
							dataOptions: item.dataOptions || [],
							pos: item.pos || 'center',
						});
					});
					_this.$DataPanel.setSelectData(_this.sheetData[0].dataOptions);
					_this.$OverallPanel.setData(_this.sheetData[0]);
					_this.$piniastore.setData(sheetList);
				}
			});
		} else {
			// _this.updateInfo = {
			// 	"description": "",
			// 	"link": location && location.origin ? location.origin + '/Renderer' : '/Renderer',
			// 	"sheet": _this.title,
			// 	"status": "0",
			// 	"title": "",
			// 	"data": ""
			// };
			// addTemplate(_this.updateInfo)
			// 	.then((res) => {
			// 		if (res.code == 200) {
			// 			_this.$store.dispatch('setTemplateId', res.data.id);
			// 			Object.assign(_this.updateInfo, {
			// 				link: _this.updateInfo.link + '/' + res.data.id,
			// 			});						
			// 		}
			// 	});
		}
	},
	beforeDestroy() {
		console.log('beforeDestroy');
	},
	destroyed() {
		console.log('destroyed');
	},
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
		align-items: center;
	}
}
</style>
