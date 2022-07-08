<template>
  <div>
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
							@change="change"
							placeholder="请输入"
						/>
				</el-form-item>
				<!-- <el-form-item label="公式" v-if="form.cellFormula">
					<el-input
							type="text"
							v-model="form.formula"
							@change="change"
							disabled
							placeholder="请输入"
						/>
				</el-form-item> -->
			</template>
      <!-- 为单元格 没有上传图片 -->
      <template v-if="form.componentType != 'Cell'">
        <!-- TODO action 有问题 需要修改 -->
        <el-form-item label="请选择图片" v-if="form.componentType == 'image'">
          <el-upload
            class="avatar-uploader"
            action="https://jsonplaceholder.typicode.com/posts/"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </template>
      <!-- 为单元格 图片 按钮 没有以下表单字段 -->
      <template v-if="form.componentType != 'Cell' && form.componentType != 'image' && form.componentType != 'button'">
				<template v-if="form.componentType == 'select'">
					<el-form-item label="选项">
						<el-select v-model="form.selectSrc" @change="change" placeholder="请选择" style="width: 100%">
							<el-option label="自定义" value="custom" />
							<el-option label="数据集" value="api" />
						</el-select>
						<el-select v-if="form.selectSrc == 'api'" v-model="form.api" @change="change" placeholder="请选择" style="width: 100%">
							<el-option label="数据集1" value="api1" />
							<el-option label="数据集2" value="api2" />
						</el-select>
						<el-input v-else type="textarea" :rows="4" v-model="form.defaultSelect" placeholder="请输入" @change="change" />
					</el-form-item>
				</template>
        <el-form-item label="默认值" v-if="form.componentType == 'input' || form.componentType == 'select'">
          <!-- <el-select v-model="form.default.type" @change="change" placeholder="请选择" style="width: 100%">
            <el-option label="自定义" value="custom" />
            <el-option label="公式" value="formula" />
          </el-select> -->
          <el-input type="text" v-model="form.default" placeholder="请输入" @change="change" />
        </el-form-item>

        <el-form-item label="校验">
          <el-col :span="24">
            <el-checkbox label="必填" v-model="form.check.noNull" @change="change" />
          </el-col>
          <el-col :span="24">
            <el-checkbox label="不允许重复值" v-model="form.check.only" @change="change" />
          </el-col>
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
export default {
  inject: ['$rightPanel'],
  data() {
    return {
      form: {
				componentType: 'Cell',
				cellValue: '',
				cellFormula: false,
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
				imageUrl: '', // 图片
				// default: {
				// 	type: 'custom',
				// 	value: '',
				// }, // 默认值
				default: '', // 默认值
				selectSrc: 'custom', // 选项
				api: 'api1', // 数据集
				defaultSelect: '', // 自定义选项
				formFiled: '', //表单字段
				inputType: 'text', //输入类型
				selectType: 'radio', //选择类型
				uploadType: 'word', //上传类型
				buttonType: 'submit',
				buttonText: '',
			},
      
    };
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
				{
					label: '表单字段',
					type: 'inputPopup',
					field: 'formFiled',
					ifShow: false,
					props: {
						placeholder: '请选择',
						click: this.setFormFiled,
					},
				},
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
	},
  methods: {
    // 表单信息修改
    change() {
			this.$rightPanel.formChange(this.form);
		},
    // 重置表单信息
		resetForm() {
			this.form = {
				componentType: 'Cell',
				cellValue: '',
				cellFormula: false,
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
				imageUrl: '', // 图片
				// default: {
				// 	type: 'custom',
				// 	value: '',
				// }, // 默认值
				default: '', // 默认值
				selectSrc: 'custom', // 选项
				api: 'api1', // 数据集
				defaultSelect: '', // 自定义选项
				formFiled: '', //表单字段
				inputType: 'text', //输入类型
				selectType: 'radio', //选择类型
				uploadType: 'word', //上传类型
				buttonType: 'submit',
				buttonText: '',
			};
		},
    // 上传成功
    handleAvatarSuccess(res, file) {
			this.form.imageUrl = URL.createObjectURL(file.raw);
			this.change();
		},
    // 开始上传
		beforeAvatarUpload(file) {
			const isJPG = file.type === 'image/jpeg';
			const isLt2M = file.size / 1024 / 1024 < 2;

			if (!isJPG) {
				console.error('上传头像图片只能是 JPG 格式!');
			}
			if (!isLt2M) {
				console.error('上传头像图片大小不能超过 2MB!');
			}
			return isJPG && isLt2M;
		},
    // 更新表单信息
		updataForm(data) {
			let temp = {
				componentType: data.c || 'Cell',
			};

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
					// 单选 多选 下拉，转成字符串，树直接转JSON字符串
					let defValue = '';
					if (data.options.length > 0) {
						const labels = [];
						data.options.forEach(item => {
							labels.push(item.label);
						});
						defValue = labels.join(',');
					}
					// 树
					defValue = data.c != 'treeSelect' && data.c != 'treeSelectMultiple' ? defValue : JSON.stringify(data.options);
					Object.assign(temp, {
						componentType: 'select',
						selectType: data.c,
						default: cellValue,
						defaultSelect: defValue,
						selectSrc: 'custom',
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
					power: {
						ifShow: data.p.r.s, // show 可见
						ifEdit: data.p.r.w, // write 可写
						showCondition: data.p.r.r[0], // [0] 可见规则， [1] 可编辑规则
						editCondition: data.p.r.r[1],
					}, // rule 权限
				});
				if (temp.componentType == 'upload') {
					Object.assign(temp, { uploadType: data.p.t });
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
					Object.assign(power, { ifShow: true, showCondition: '' });
				} else {
					Object.assign(power, { 
						ifShow: data.p.r.s, // show 可见
						showCondition: data.p.r.r[0], // [0] 可见规则， [1] 可编辑规则
					});
				}
				Object.assign(temp, {
					power: power// rule 权限
				});
			}
			// Cell 单元格
			if (temp.componentType == 'Cell') {
				Object.assign(temp, { default: cellValue, cellFormula, formula });
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
