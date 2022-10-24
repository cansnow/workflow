<template>
  <div style="width: 40%; min-width: 500px; margin: auto;">
    <el-form ref="form" :model="form" label-width="80px">
      <el-form-item label="名称">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="工作簿" v-if="false">
        <el-select v-model="form.sheet" placeholder="请选择" style="width: 100%">
          <el-option
            v-for="(item, index) in options"
            :key="index"
            :label="item"
            :value="item">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input type="textarea" :rows="4" v-model="form.description"></el-input>
      </el-form-item>
      <el-form-item label="链接">
        <el-input v-model="form.link"></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.value"
            >{{ dict.label }}</el-radio
          >
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  dicts: ["sys_normal_disable"],
  props: {
    options: {
      type: Array,
      default: () => [],
    }
  },
  data() {
    return {
      form: {
        title: '',
        sheet: '',
        description: '',
        link: '',
        status: '0',
      }
    };
  },
  methods: {
    resetForm() {
      this.form = {
        title: '',
        sheet: '',
        description: '',
        link: '',
        status: '0',
      };
    },
    getFormData() {
      return this.form;
    },
    setData(data) {
      Object.assign(data, { status: data.status + '' });
      this.form = data;
    },
  },
}
</script>
