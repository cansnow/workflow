<template>
  <div>
		<!-- <div
			style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: #ffffff8a; pointer-events: none;"
			@mousedown="test"
		></div> -->
    <el-form ref="form" :model="form" label-position="top" size="small">
      <!-- 动态表单字段 -->
      <template v-for="(fItem, index) in formConfig">
        <el-form-item :key="index" :label="fItem.label" v-if="fItem.ifShow">
          <!-- select -->
          <template v-if="fItem.type == 'select'">
            <el-select 
              v-model="form[fItem.field]" 
              :placeholder="fItem.props.placeholder" 
              :style="fItem.props.style" 
              @change="fItem.props.change"
            >
              <el-option 
                v-for="(oItem, oIndex) in fItem.props.options"
                :key="oIndex"
                :label="oItem.label"
                :value="oItem.value"
              />
            </el-select>
          </template>
          <!-- input -->
          <template v-if="fItem.type == 'input'">
            <el-input
              type="text"
              v-model="form[fItem.field]"
              :placeholder="fItem.props.placeholder"
              @focus="fItem.props.focus"
              @change="fItem.props.change" 
            />
          </template>
          <!-- 弹窗选择字段 -->
          <template v-if="fItem.type == 'inputPopup'">
            <div class="el-input el-input--small" @click="fItem.props.click">
                <div class="el-input__inner">
                  <!-- <span>{{ form[fItem.field] }}</span> -->
                  <span :style="{ color: form[fItem.field] + '' != '' ? '' : '#c4c4cc'}">
                    {{ form[fItem.field] || fItem.props.placeholder }}
                  </span>
                </div>
            </div>
          </template>
        </el-form-item>
      </template>
			<!-- 单元格值 -->
			<template v-if="form.componentType == 'Cell'">
				<el-form-item label="默认值">
					<el-input
						type="text"
						v-model="form.default"
						@change="defaultChange"
						placeholder="请输入"
					/>
				</el-form-item>
				<el-form-item label="扩展">
					<el-select v-model="form.extendType" @change="change" placeholder="请选择" style="width: 100%">
						<el-option label="无" value="none" />
						<el-option label="向下" value="bottom" />
						<el-option label="向右" value="right" />
					</el-select>
				</el-form-item>
				<el-form-item label="超链接">
					<el-select v-model="form.cellType" @change="change" placeholder="请选择" style="width: 100%">
						<el-option label="否" value="Cell" />
						<el-option label="是" value="Link" />
					</el-select>
				</el-form-item>
				<!-- 是超链接 -->
				<el-form-item label="超链接地址" v-if="form.cellType == 'Link'">
					<el-input
						type="text"
						v-model="form.cellLink"
						@change="change"
						placeholder="请输入"
					/>
				</el-form-item>
			</template>
      <!-- 为单元格 没有上传图片 -->
      <template v-if="form.componentType != 'Cell'">
        <!-- TODO action 有问题 需要修改 -->
        <el-form-item label="请选择图片" v-if="form.componentType == 'image'">
          <el-upload
            class="avatar-uploader"
            :action="uploadFileUrl"
						:headers="headers"
            :show-file-list="false"
						name="uploadFile"
            :data="{ type: 1 }"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="form.imageUrl" :src="form.imageUrl | getImgUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </template>
      <!-- 为单元格 图片 按钮 没有以下表单字段 -->
      <template v-if="form.componentType != 'Cell' && form.componentType != 'image' && form.componentType != 'button'">
				<template v-if="form.componentType == 'select'">
					<el-form-item label="选项">
						<el-select v-model="form.selectSrc" @change="handleSelectSrc" placeholder="请选择" style="width: 100%">
							<el-option label="自定义" value="custom" />
							<el-option label="数据集" value="api" />
						</el-select>
						<!-- TODO -->
						<TreeSelect
							v-if="form.selectSrc == 'api' && false"
							style="width: 100%"
							:props="treeProps"
							:options="options"
							:value="form.api"
							:clearable="true"
							:accordion="true"
							:ifIcons="true"
							@getValue="getTreeSelectValue($event)"
						/>
						<el-select v-if="form.selectSrc == 'api'" v-model="form.api" @change="handleApiChange" placeholder="请选择" style="width: 100%">
							<el-option :label="item.resourcename" :value="item.tableName" v-for="(item, index) in options" :key="index" />
						</el-select>
						<!-- 提示 -->
						<template v-else>
							<el-tooltip placement="top" :hide-after="500">
								<template slot="content">
									<div>【单选】、【多选】、【下拉单选】、【下拉多选】自定义数据格式：</div>
									<div>简单数据以逗号分割, 如: 1,2,3、选项1,选项2,选项3</div>
									<div>复杂格式以对象数组形式, 格式如下: </div>
									<div>[{"label":"选项1","value":"1"},{"label":"选项2","value":"2"}]</div>
									<div>【下拉树】自定义数据格式，格式如下: </div>
									<div>[{"id":1,"label":"一级 1","children":[{"id":4,"label":"二级 1-1","children":[{"id":9,"label":"三级 1-1-1"},{"id":10,"label":"三级 1-1-2"}]}]}]</div>
								</template>
								<el-input type="textarea" :rows="4" v-model="form.defaultSelect" placeholder="请输入" @change="change" />
							</el-tooltip>
						</template>
					</el-form-item>
					<template v-if="form.selectSrc == 'api' && !!form.api">
						<el-form-item label="选项value">
							<el-select v-model="form.apiValue" @change="change" placeholder="请选择" style="width: 100%">
								<el-option :label="item.resourcename" :value="item.relativeData" v-for="(item, index) in apiOptions[form.api] || []" :key="index" />
							</el-select>
						</el-form-item>
						<el-form-item label="选项label">
							<el-select v-model="form.apiLabel" @change="change" placeholder="请选择" style="width: 100%">
								<el-option :label="item.resourcename" :value="item.relativeData" v-for="(item, index) in apiOptions[form.api] || []" :key="index" />
							</el-select>
						</el-form-item>
					</template>
				</template>
				<template v-if="form.componentType == 'input' || form.componentType == 'select'">
					<el-form-item label="默认值">
						<!-- <el-select v-model="form.default.type" @change="change" placeholder="请选择" style="width: 100%">
							<el-option label="自定义" value="custom" />
							<el-option label="公式" value="formula" />
						</el-select> -->
						<el-input type="text" v-model="form.default" placeholder="请输入" @change="defaultChange" />
					</el-form-item>

					<el-form-item label="扩展">
						<el-select v-model="form.extendType" @change="change" placeholder="请选择" style="width: 100%">
							<el-option label="无" value="none" />
							<el-option label="向下" value="bottom" />
							<el-option label="向右" value="right" />
						</el-select>
					</el-form-item>

					<el-form-item label="占位符" v-if="form.componentType == 'input'">
						<el-input type="text" v-model="form.ph" placeholder="请输入" @change="change" />
					</el-form-item>
				</template>

        <el-form-item label="校验">
          <el-col :span="24">
            <el-checkbox label="必填" v-model="form.check.noNull" @change="change" />
          </el-col>
          <el-col :span="24">
            <el-checkbox label="不允许重复值" v-model="form.check.only" @change="change" />
          </el-col>
        </el-form-item>

				<!-- 扩展校验 -->
				<el-form-item label="校验">
					<el-select v-model="form.checkExtend" @change="change" placeholder="请选择" style="width: 100%">
						<el-option label="手机" value="phone" />
						<el-option label="身份证号" value="idCard" />
						<el-option label="邮件" value="email" />
						<el-option label="数值大小" value="number" />
						<el-option label="文本长度" value="length" />
						<!-- <el-option label="唯一" value="only" />
						<el-option label="必填" value="noNull" /> -->
					</el-select>
				</el-form-item>
      </template>

      <el-form-item label="权限">
        <el-row>
          <el-col :span="6">
            <el-checkbox label="可见" name="ifShow" v-model="form.power.ifShow" @change="change" />
          </el-col>
          <el-col :span="18">
            <el-input type="text" v-model="form.power.showCondition" placeholder="请输入条件" @change="change" />
          </el-col>
        </el-row>
        <!-- 为单元格 图片 按钮 没有以下表单字段 -->
        <el-row v-if="form.componentType != 'Cell' && form.componentType != 'image' && form.componentType != 'button'">
          <el-col :span="6">
            <el-checkbox label="可编辑" name="ifEdit" v-model="form.power.ifEdit" @change="change" />
          </el-col>
          <el-col :span="18">
            <el-input type="text" v-model="form.power.editCondition" placeholder="请输入条件" @change="change" />
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import Radio from './src/radio.vue';
import TreeSelect from '@/components/exceleditor/components/treeSelect/index.vue';
import { getTableFieldByName, getDBData } from '@/api/editManage';
import { getToken } from "@/utils/auth";
export default {
  inject: ['$rightPanel'],
	components: { TreeSelect },
  data() {
    return {
      form: {
				componentType: 'Cell',
				cellValue: '',
				cellFormula: false,
				cellType: 'Cell',
				cellLink: '',
				formula: '',
				power: {
					ifShow: true,
					showCondition: '',
					ifEdit: true,
					editCondition: '',
				}, // 权限
				check: {
          noNull: true,
          only: false,
				}, // 校验
				checkExtend: '', // 校验扩展
				imageUrl: '', // 图片
				// default: {
				// 	type: 'custom',
				// 	value: '',
				// }, // 默认值
				default: '', // 默认值
				selectSrc: 'custom', // 选项
				api: '', // 数据集
				apiValue: '', // 数据集值
				apiLabel: '', // 数据值显示
				apiT: '', // 数据表名
				defaultSelect: '', // 自定义选项
				formFiled: '', //表单字段
				inputType: 'text', //输入类型
				selectType: 'radio', //选择类型
				uploadType: 'word', //上传类型
				buttonType: 'submit',
				buttonText: '',
				buttonLink: '', // 超链接地址
				extendType: 'none', // 扩展
				tableName: '', // 表名
				ph: '', // 占位符
			},
			options: [],
			apiOptions: {},
			treeProps: {
        value:'id',             // ID字段名
        label: 'resourcename',         // 显示名称
        children: 'children'    // 子级字段名
      },
			uploadFileUrl: process.env.VUE_APP_BASE_API + "/workflow/fileInfo/upload", // 上传的图片服务器地址
			headers: {
					Authorization: "Bearer " + getToken(),
			},
			selection: undefined,
    };
  },
	filters: {
			getImgUrl(value) {
					const url = location && location.origin ? location.origin + '/uploads' + value : '/uploads' + value;
					return url;
			}
	},
  computed: {
		// 表单配置项
		formConfig: function() {
			const temp = [
				{
					label: '组件类型',
					type: 'select',
					field: 'componentType',
					ifShow: true, // 是否显示
					props: {
						placeholder: '请选择组件类型',
						style: { width: '100%' },
						options: [
							{ label: "单元格", value: "Cell" },
							{ label: "输入", value: "input" },
							{ label: "选择", value: "select" },
							{ label: "上传", value: "upload" },
							{ label: "图片", value: "image" },
							{ label: "按钮", value: "button" },
						],
						change: this.change,
					},
				},
				// {
				// 	label: '表单字段',
				// 	type: 'inputPopup',
				// 	field: 'formFiled',
				// 	ifShow: false,
				// 	props: {
				// 		placeholder: '请选择',
				// 		click: this.setFormFiled,
				// 	},
				// },
				{
					label: '输入类型',
					type: 'select',
					field: 'inputType',
					ifShow: false,
					props: {
						placeholder: '请选择类型',
						style: { width: '100%' },
						options: [
							{ label: "普通", value: "text" },
							{ label: "数字", value: "number" },
							{ label: "密码", value: "password" },
							{ label: "日期", value: "datetime" },
						],
						change: this.change,
					},
				},
				{
					label: '选择类型',
					type: 'select',
					field: 'selectType',
					ifShow: false,
					props: {
						placeholder: '请选择类型',
						style: { width: '100%' },
						options: [
							{ label: "单选", value: "radio" },
							{ label: "多选", value: "checkbox" },
							{ label: "下拉单选", value: "select" },
							{ label: "下拉多选", value: "selectMultiple" },
							{ label: "下拉单选树", value: "treeSelect" },
							{ label: "下拉多选树", value: "treeSelectMultiple" },
						],
						change: this.change,
					},
				},
				{
					label: '上传类型',
					type: 'select',
					field: 'uploadType',
					ifShow: false,
					props: {
						placeholder: '请选择上传类型',
						style: { width: '100%' },
						options: [
							{ label: "文档", value: "word" },
							{ label: "图片", value: "image" },
							{ label: "压缩包", value: "zip" },
							{ label: "自定义", value: "custom" },
						],
						change: this.change,
					},
				},
				{
					label: '按钮类型',
					type: 'select',
					field: 'buttonType',
					ifShow: false,
					props: {
						placeholder: '请选择上传类型',
						style: { width: '100%' },
						options: [
							{ label: "提交", value: "submit" },
							{ label: "重置", value: "reset" },
							{ label: "超链接", value: "text" },
							// { label: "删除", value: "delete" },
							{ label: "搜索", value: "search" },
						],
						change: this.change,
					},
				},
				{
					label: '文本',
					type: 'input',
					field: 'buttonText',
					ifShow: false,
					props: {
						placeholder: '请输入',
						style: { width: '100%' },
						focus: function() {},
						change: this.change,
					},
				},
				{
					label: '超链接地址',
					type: 'input',
					field: 'buttonLink',
					ifShow: false,
					props: {
						placeholder: '请输入',
						style: { width: '100%' },
						focus: function() {},
						change: this.change,
					},
				},
			];
			temp.forEach(item => {
				if (item.field != 'componentType') {
					item.ifShow = this.form.componentType != 'Cell'
					if (item.ifShow) {
						if (item.field == 'inputType') item.ifShow = this.form.componentType == 'input';
						if (item.field == 'selectType') item.ifShow = this.form.componentType == 'select';
						if (item.field == 'uploadType') item.ifShow = this.form.componentType == 'upload';
						if (
							item.field == 'buttonType' ||
							item.field == 'buttonText'
						) 
							item.ifShow = this.form.componentType == 'button';
						if (item.field == 'buttonLink') {
							item.ifShow = this.form.buttonType == 'text';
						}
						if (item.field == 'formFiled') {
							item.ifShow = 
								this.form.componentType == 'upload' || 
								this.form.componentType == 'input' ||
								this.form.componentType == 'select';
						}
					}
				}
			});
			return temp;
		},
		// 数据面板
		$DataPanel: function() {
			return this.$rightPanel.getDataPanelRef();
		},
	},
	watch: {
		options: {
			handler(value) {
				_.map(value, item => {
					this.apiOptions[item.tableName] = item.children;
				});
			}
		},
	},
  methods: {
		handleApiChange() {
			const _this = this;
			const tableName = this.apiOptions[this.form.api][0].tempTableName;
			const temp = {};
			if (!!tableName) {
				Object.assign(temp, { table: tableName });
			} else {
				Object.assign(temp, { table: this.form.api });
			}
			getDBData(temp).then((res) => {
				console.log('res', res);
				const data = res.data.dataSetList;
				if (!!data && data instanceof Array && data.length > 0) {
					_this.form.defaultSelect = data[0].valueList;
				}
			});
			this.form.apiValue = '';
			this.form.apiLabel = '';
			this.form.apiT = tableName || this.form.api;
			this.change();
		},
		handleSelectSrc() {
			this.options = this.$DataPanel.showData || [];
			_.map(this.options, item => {
				this.apiOptions[item.tableName] = item.children;
			});
			this.change();
		},
		getTreeSelectValue(e) {
			console.log('getTreeSelectValue e',e);
			console.log('$DataPanel', this.$DataPanel);
			console.log('$DataPanel showData', this.$DataPanel.showData);
			const _this = this;
			Object.assign(this.form, { api: e[0] });
			getTableFieldByName({ table: e[0] })
				.then((res) => {
					console.log('res', res);
					const data = [];
					_.map(res.data.columns, item => {
						data.push({
							label: item.aliasName || item.columnName,
							value: item.columnName,
						});
					});
					Object.assign(_this.form, { defaultSelect: data });
					_this.change();
				});
    },
		// 默认值赋值
		defaultChange() {
			Object.assign(this.form, { formFiled: this.form.default });
			this.change();
		},
    // 表单信息修改
    change() {
			this.$rightPanel.formChange(this.form, this.selection);
		},
    // 重置表单信息
		resetForm(selection) {
			this.form = {
				componentType: 'Cell',
				cellValue: '',
				cellFormula: false,
				cellType: 'Cell',
				cellLink: '',
				formula: '',
				power: {
					ifShow: true,
					showCondition: '',
					ifEdit: true,
					editCondition: '',
				}, // 权限
				check: {
          noNull: false,
          only: false,
				}, // 校验
				checkExtend: '', // 校验扩展
				imageUrl: '', // 图片
				// default: {
				// 	type: 'custom',
				// 	value: '',
				// }, // 默认值
				default: '', // 默认值
				selectSrc: 'custom', // 选项
				api: '', // 数据集
				apiValue: '', // 数据集值
				apiLabel: '', // 数据值显示
				apiT: '', // 数据表名
				defaultSelect: '', // 自定义选项
				formFiled: '', //表单字段
				inputType: 'text', //输入类型
				selectType: 'radio', //选择类型
				uploadType: 'word', //上传类型
				buttonType: 'submit',
				buttonText: '',
				buttonLink: '', // 超链接地址
				extendType: 'none', // 扩展
				tableName: '', // 表名
				ph: '', // 占位符
			};
			if (!!selection) {
				this.selection = selection;
			} else {
				this.selection = undefined;
			}
		},
    // 上传成功
    handleAvatarSuccess(res, file) {
			console.warn('handleAvatarSuccess', res, file);
			this.form.imageUrl = res.fileInfo.filePath;//URL.createObjectURL(file.raw);
			this.change();
		},
    // 开始上传
		beforeAvatarUpload(file) {
			const isJPG = file.type === 'image/jpeg';
			const isLt2M = file.size / 1024 / 1024 < 1;

			if (!isJPG) {
				console.error('上传图片只能是 JPG 格式!');
			}
			if (!isLt2M) {
				console.error('上传图片大小不能超过 1MB!');
			}
			return isJPG && isLt2M;
		},
    // 更新表单信息
		updataForm(data, selection) {
			let temp = {
				componentType: data.c || 'Cell',
			};
			this.selection = selection; // 单元格
			// 默认值
			const cellFormula = typeof(data.f) != 'undefined';
			const formula = cellFormula ? _.$parseRefs(data.f, this.$rightPanel.selection.start) : '';
			let cellValue = typeof(data.v) == 'undefined' ? '' : data.v;
			cellValue = cellFormula ? formula : cellValue;

			switch(data.c) {
				case 'button': // 按钮
					Object.assign(temp, {
						buttonType: data.p.t || 'submit', // == '重置' ? 'reset' : 'submit',
						buttonText: data.v,
					});
					break;
				case 'image': // 图片
					Object.assign(temp, { imageUrl: data.v });
					break;
				case 'radio': // 选择
				case 'checkbox':
				case 'select':
				case 'selectMultiple':
				case 'treeSelect': // 下拉树单选
				case 'treeSelectMultiple': // 下拉树多选
					let defValue = '';
					if (data.p.ds == 'custom') {
						// 单选 多选 下拉，转成字符串，树直接转JSON字符串
						if (data.options.length > 0) {
							// const labels = [];
							// data.options.forEach(item => {
							// 	labels.push(item.label);
							// });
							// defValue = labels.join(',');
							defValue = !!data.options ? JSON.stringify(data.options) : '';
						}
						// 树
						// defValue = data.c != 'treeSelect' && data.c != 'treeSelectMultiple' ? defValue : JSON.stringify(data.options);
						Object.assign(temp, { defaultSelect: defValue });
					} else {
						Object.assign(temp, { 
							defaultSelect: data.options,
							api: data.p.api,
							apiValue: data.p.apiValue,
							apiLabel: data.p.apiLabel,
							apiT: data.p.apiT,
						});
					}
					
					Object.assign(temp, {
						componentType: 'select',
						selectType: data.c,
						default: cellValue,
						selectSrc: data.p.ds,
					});
					break;
				case 'text': // 输入
				case 'password':
				case 'datetime':
				case 'number':
					Object.assign(temp, {
						componentType: 'input',
						inputType: data.c,
						default: cellValue,
					});
					break;
			}
			// componentType 是 input select upload
			if (
				temp.componentType == 'input' ||
				temp.componentType == 'select' ||
				temp.componentType == 'upload'
			) {
				Object.assign(temp, {
					formFiled: data.p.f, // 字段
					check: {
						noNull: data.p.vd.r, // Required 必填
						only: data.p.vd.u, // unique 唯一
					}, // validate 验证
					checkExtend: data.p.vd.ex || '', // 校验扩展
					power: {
						ifShow: data.p.r.s, // show 可见
						ifEdit: data.p.r.w, // write 可写
						showCondition: data.p.r.r[0], // [0] 可见规则， [1] 可编辑规则
						editCondition: data.p.r.r[1],
					}, // rule 权限
				});
				if (
					temp.componentType == 'input' ||
					temp.componentType == 'select'
				) {
					Object.assign(temp, {
						extendType: typeof data.p.e != 'undefined' ? data.p.e : 'none',
						formFiled: temp.default,
						tableName: typeof data.p.tn != 'undefined' ? data.p.tn : '', // 表名
					});
				}
				if (temp.componentType == 'upload') {
					Object.assign(temp, { uploadType: data.p.t });
				}
				// 占位符
				if (temp.componentType == 'input') {
					Object.assign(temp, { ph: data.p.ph });
				}
			}
			// componentType 是 image button
			if (
				temp.componentType == 'image' ||
				temp.componentType == 'button' ||
				temp.componentType == 'Cell'
			) {
				const power = {};
				if (typeof(data.p) == 'undefined') {
					Object.assign(power, { ifShow: true, showCondition: '', ifEdit: true, editCondition: '' });
				} else {
					Object.assign(power, { 
						ifShow: data.p.r.s, // show 可见
						ifEdit: data.p.r.w, // write 可写
						showCondition: data.p.r.r[0], // [0] 可见规则， [1] 可编辑规则
						editCondition: data.p.r.r[1],
					});
					if (temp.componentType == 'Cell') {
						// 单元格扩展
						Object.assign(temp, { 
							extendType: typeof data.p.e != 'undefined' ? data.p.e : 'none',
							formFiled: typeof data.p.f != 'undefined' ? data.p.f : '',
							tableName: typeof data.p.tn != 'undefined' ? data.p.tn : '', // 表名
						});
						// 单元格超链接
						Object.assign(temp, { 
							cellType: typeof data.p.ct != 'undefined' ? data.p.ct : 'Cell',
							cellLink: typeof data.p.cl != 'undefined' ? data.p.cl : '',
						});
					}
				}
				if (temp.componentType == 'button' && temp.buttonType == 'text') {
					Object.assign(temp, { buttonLink: data.p.bl || '' });
				}
				Object.assign(temp, {
					power: power// rule 权限
				});
			}
			// Cell 单元格
			if (temp.componentType == 'Cell') {
				Object.assign(temp, { default: cellValue, cellFormula, formula, formFiled: cellValue });
			}
			Object.assign(this.form, temp);
		},
    // 表单字段
		selectField() {
			const h = this.$createElement;
      this.$msgbox({
        title: '选择字段',
        message: 
				h(
					Radio,
					{ 
						ref: 'radio',
						props: { 
							selectList: [
								{ label: '1', text: 'title' },
								{ label: '2', text: 'title2' }
							],
							select: '1',
						}
					}
				),
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '执行中...';
            setTimeout(() => {
              done();
              setTimeout(() => {
                instance.confirmButtonLoading = false;
              }, 300);
            }, 3000);
          } else {
            done();
          }
        }
      }).then(() => {
				Object.assign(this.form, { formFiled: this.$refs.radio.getSeletItem() });
      });
		},
    // 打开字段弹窗
		setFormFiled() {
			this.selectField();
		},
  },
	mounted() {
    const _this = this;
    // getDBTable().then((res) => {
    //   console.log('res', res);
    //   _this.options = res.data;
    // });
		this.$DataPanel.$on('dataPanelSelect', function(data) {
			_this.options = data;
		});
  },
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
