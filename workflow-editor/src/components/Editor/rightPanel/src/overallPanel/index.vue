<template>
  <div>
    <el-button size="mini" @click="open">添加</el-button>
    <div style="margin-left: 2px;">
      <template v-for="(fItem, index) in formList">
        <div :key="index + '_title'" style="margin: 5px 0; display: flex;">
          <div style="flex: 1; display: flex; align-items: center;">规则{{index + 1}}</div>
          <el-button type="text" @click="() => handleDel(index)">
            <i class="mdi mdi-close"></i>
          </el-button>
        </div>
        <el-form :key="index + '_form'" label-width="60px" label-position="left" size="small">
          <el-form-item label="表达式">
            <el-input v-model="fItem.expression"></el-input>
          </el-form-item>
          <el-form-item label="范围">
            <el-input v-model="fItem.range"></el-input>
          </el-form-item>
          <el-form-item label="类型">
            <div>{{fItem.type == 'disable' ? '禁用' : '隐藏' }}</div>
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>

<script>
import Form from './src/form.vue';
export default {
  inject: ['$rightPanel'],
  data() {
    return {
      formList: [
        {
          expression: '',
          range: '',
          type: 'disable',
        }
      ],
    };
  },
  methods: {
    open() {
      const h = this.$createElement;
      this.$msgbox({
        title: '消息',
        message: h(Form, { ref: 'form' }),
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
        this.formList.push(this.$refs.form.getFormData());
        this.$refs.form.resetFormData();
      });
    },
    handleDel(index) {
      this.formList.splice(index, 1);
    }
  },
}
</script>
