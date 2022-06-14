<template>
	<div style="width: 300px;height: 100%; padding: 10px;">
		<div style="display: flex;">
			<div style="flex:1;">
				<el-tabs v-model="activeName" @tab-click="handleTabClick" stretch>
					<el-tab-pane label="属性" name="attribute" />
					<el-tab-pane label="数据" name="data" />
					<el-tab-pane label="全局" name="overall" />
				</el-tabs>
			</div>
			<div style="width: 50px; text-align: center;">
				<el-button type="text">
					<i class="mdi mdi-close"></i>
				</el-button>
			</div>
		</div>
		<div style="text-align: left;">
			<template v-if="activeName == 'attribute'">
				<el-form ref="form" :model="form" label-position="top" size="small">
					<template v-for="(fItem, index) in formConfig">
						<el-form-item :key="index" :label="fItem.label" v-if="fItem.ifShow">
							<!-- select -->
							<template v-if="fItem.type == 'select'">
								<el-select v-model="form[fItem.field]" :placeholder="fItem.props.placeholder" :style="fItem.props.style" @change="fItem.props.change">
									<el-option v-for="(oItem, oIndex) in fItem.props.options" :key="oIndex" :label="oItem.label" :value="oItem.value"></el-option>
								</el-select>
							</template>
							<!-- input -->
							<template v-if="fItem.type == 'input'">
								<el-input type="text" v-model="form[fItem.field]" :placeholder="fItem.props.placeholder" @focus="fItem.props.focus"></el-input>
							</template>
						</el-form-item>
					</template>

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

					<template v-if="form.componentType != 'Cell' && form.componentType != 'image' && form.componentType != 'button'">
						<el-form-item label="默认值" v-if="form.componentType == 'input' || form.componentType ==  'select'">
							<el-select v-model="form.default.type" placeholder="请选择" style="width: 100%">
								<el-option label="自定义" value="custom"></el-option>
								<el-option label="公式" value="formula"></el-option>
							</el-select>
							<el-input type="text" v-model="form.default.condition" placeholder="请输入"></el-input>
						</el-form-item>

						<el-form-item label="校验">
							<el-col :span="24">
								<el-checkbox label="必填" v-model="form.check.noNull"></el-checkbox>
							</el-col>
							<el-col :span="24">
								<el-checkbox label="不允许重复值" v-model="form.check.only"></el-checkbox>
							</el-col>
						</el-form-item>
					</template>

					<el-form-item label="权限">
						<el-row>
							<el-col :span="6">
								<el-checkbox label="可见" name="ifShow" v-model="form.power.ifShow"></el-checkbox>
							</el-col>
							<el-col :span="18">
								<el-input type="text" v-model="form.power.showCondition" placeholder="请输入条件"></el-input>
							</el-col>
						</el-row>
						<el-row v-if="form.componentType != 'Cell' && form.componentType != 'image' && form.componentType != 'button'">
							<el-col :span="6">
								<el-checkbox label="可编辑" name="ifEdit" v-model="form.power.ifEdit"></el-checkbox>
							</el-col>
							<el-col :span="18">
								<el-input type="text" v-model="form.power.editCondition" placeholder="请输入条件"></el-input>
							</el-col>
						</el-row>
					</el-form-item>
				</el-form>
			</template>
		</div>
	</div>
</template>

<script>
  export default {
    data() {
      return {
		activeName: 'attribute',
        form: {
          componentType: 'Cell',
		  power: {
			ifShow: true,
			showCondition: '',
			ifEdit: true,
			editCondition: '',
		  }, // 权限
		  check: {
			noNull: true,
			only: false,
		  },
		  imageUrl: '',

		  default: {
			type: 'custom',
			condition: '',
		  },

		  formFiled: '', //表单字段
		  inputType: 'input', //输入类型
		  selectType: 'radio', //选择类型
		  uploadType: 'word', //上传类型
		  buttonType: 'submit',
		  buttonText: '',
        },
		temp: '',
      }
    },
	emits: ['updateForm'],
	watch: {
		form(newValue) {
			console.log('form new value', newValue);
		},
	},
	computed: {
		formConfig: function() {
			const temp = [
				{
					label: '组件类型',
					type: 'select',
					field: 'componentType',
					ifShow: true,
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
						change: function(e) {
							console.log('component change', e);
						},
					},
				},
				{
					label: '表单字段',
					type: 'input',
					field: 'formFiled',
					ifShow: false,
					props: {
						placeholder: '请选择',
						style: { width: '100%' },
						focus: function(e) {
							console.log('focus', e);
						},
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
							{ label: "普通", value: "input" },
							{ label: "数字", value: "number" },
							{ label: "密码", value: "password" },
							{ label: "日期", value: "date" },
						],
						change: function(e) {
							console.log('component change', e);
						},
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
							{ label: "多选", value: "check" },
							{ label: "下拉单选", value: "dropdownRadio" },
							{ label: "下拉多选", value: "dropdownCheck" },
							{ label: "下拉单选树", value: "dropdownRadioTree" },
							{ label: "下拉多选树", value: "dropdownCheckTree" },
						],
						change: function(e) {
							console.log('component change', e);
						},
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
						change: function(e) {
							console.log('component change', e);
						},
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
						change: function(e) {
							console.log('component change', e);
						},
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
		formChange: function() {
			const temp2 = JSON.stringify(this.form);
			if (temp2 == this.temp) {
				return false;
			} else {
				// this.updateTemp();
				console.log('form change');
				return true;
			}
		},
	},
	created: function() {
		this.init();
	},
    methods: {
	  init() {
		this.temp = JSON.stringify(this.form);
	  },
      onSubmit() {
        console.log('submit!');
      },
	  handleTabClick() {
		console.log('activeName', this.activeName);
	  },
	  handleAvatarSuccess(res, file) {
        this.from.imageUrl = URL.createObjectURL(file.raw);
      },
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
      }
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