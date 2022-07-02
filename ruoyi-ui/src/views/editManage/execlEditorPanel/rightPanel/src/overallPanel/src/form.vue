<template>
  <div>
    <el-form :model="form" label-width="80px" size="small">
      <el-form-item label="表达式">
        <el-input v-model="form.expression"></el-input>
      </el-form-item>
      <el-form-item label="范围">
        <el-input v-model="form.range"></el-input>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type" placeholder="请选择活动区域">
          <el-option label="禁用" value="disable"></el-option>
          <el-option label="隐藏" value="hide"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        expression: '',
        range: '',
        type: 'disable',
        index: -1,
      },
    };
  },
  watch: {
    'form.index': {
      handler(newValue) {
        console.log('newValue', newValue);
      },
    },
  },
  methods: {
    getFormData() {
      return this.form;
    },
    resetFormData() {
      this.form = {
        expression: '',
        range: '',
        type: 'disable',
        index: -1,
        disabled: false,
      };
    },
    setFormData(data) {
      Object.assign(this.form, data);
    },
  },
}
</script>