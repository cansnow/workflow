<template>
  <div class="overall">
    <div style="margin-left: 2px;">
      <template v-if="formList.length > 0">
        <div v-for="(fItem, index) in formList" :key="index" style="margin: 5px 0; display: flex;">
          <div style="flex: 1; display: flex; align-items: center;">
            <!-- 变量 -->
            <span>{{ fItem.range }}</span>
            <span>==</span>
            <span>{{ fItem.expression }}</span>
            <span style="margin-left: 5px">{{fItem.type == 'disable' ? '禁用' : '隐藏' }}</span>
          </div>
          <div>
            <el-button type="text" :disabled="fItem.disabled" @click="() => handleEdit(index)">
              <i class="mdi mdi-text-box-edit"></i>
            </el-button>
            <el-button type="text" :disabled="fItem.disabled" style="color: red;" @click="() => handleDel(index)">
              <i class="mdi mdi-delete"></i>
            </el-button>
          </div>
        </div>
      </template>
    </div>
    <el-button size="mini" style="width: 100%;" @click="open">添加</el-button>
    <el-form label-width="80px" label-position="top" size="small">
      <el-form-item label="最大列">
        <div class="rowColumn" @click="() => handleClick('column')">
          <el-input
            :style="ifClick && direction == 'column' ? { border: '1px solid #1890ff', borderRadius: '4px' } : ''"
            v-model="columnMax"
          ></el-input>
        </div>
      </el-form-item>
      <el-form-item label="最大行">
        <div class="rowColumn" @click="() => handleClick('row')">
          <el-input 
            :style="ifClick && direction == 'row' ? { border: '1px solid #1890ff', borderRadius: '4px' } : ''"
            v-model="rowMax"
          ></el-input>
        </div>
      </el-form-item>
    </el-form>
    <Dialog :title="title" :dialogVisible="dialogVisible" @handleClose="handleClose" @handleIsOk="handleIsOk">
      <Form ref="form" />
		</Dialog>
  </div>
</template>

<script>
import Form from './src/form.vue';
import Dialog from '../dialog/index.vue';
export default {
  inject: ['$rightPanel'],
  data() {
    return {
      formList: [],
      dialogVisible: false,
      title: '',
      index: -1,
      columnMax: '',
      rowMax: '',
      ifClick: false,
      direction: '',
    };
  },
  components: {
    Form,
    Dialog,
  },
  watch: {
    '$rightPanel.selectCell': {
      handler(newValue) {
        if (this.dialogVisible) {
          const form = this.$refs.form.getFormData();
          Object.assign(form, { range: newValue });
          this.$refs.form.setFormData(form);
        }
        if (this.ifClick) {
          const { start } = this.$rightPanel.selection;
          this.setRowMax(start.rowIndex);
          this.setColumnMax(start.columnIndex);
        }
      },
    },
    '$rightPanel.head': {
      handler(newValue) {
        if (this.ifClick) {
          if (newValue.direction == 'row' && this.direction == 'row') {
            this.setRowMax(newValue['rowIndex']);
          }
          if (newValue.direction == 'column' && this.direction == 'column') {
            this.setColumnMax(newValue['columnIndex']);
          }
        }
      }
    },
  },
  methods: {
    handleClose() {
      this.$refs.form.resetFormData();
      this.dialogVisible = false;
      if (this.index != -1) {
        const form = this.formList[this.index];
        Object.assign(form, { disabled: false });
        this.formList.splice(this.index, 1, form);
        this.index = -1;
      }
    },
    handleIsOk() {
      const form = this.$refs.form.getFormData();
      if (form.index == -1) {
        this.formList.push(this.$refs.form.getFormData());
      } else {
        Object.assign(form, { disabled: false });
        this.formList.splice(form.index, 1, form);
      }
      this.handleClose();
    },
    open() {
      if (!this.dialogVisible) {
        this.dialogVisible = true;
        this.title = '新建规则';
      }
    },
    handleDel(index) {
      this.formList.splice(index, 1);
    },
    handleEdit(index) {
      const temp = this.formList[index];
      Object.assign(temp, { index: index, disabled: true });
      this.$refs.form.setFormData(temp);
      this.formList.splice(index, 1, temp);
      this.index = index;
      this.showSelectCells(); // 显示选中单元格
      this.open();
      this.title = '编辑规则';
    },
    // 显示选中单元格
    showSelectCells() {
      const form = this.formList[this.index];
      const { range } = form;
      if (range != '') {
        /**
         * 将G9:G10
         * 转成
         * { start: { columnIndex, rowIndex },
         * end: { rowIndex, columnIndex } }
         */
        const list = range.split(':');
        // G9
        if (list.length <= 1) {
          const data = this.formatData(list[0]);
          const temp = { start: data, end: data };
          this.$rightPanel.setSelectCell(temp);
          this.$rightPanel.showSelectCells(temp);
        }
        // G9:G10
        if (list.length > 1) {
          const start = this.formatData(list[0]);
          const end = this.formatData(list[1]);
          const temp = { start, end };
          this.$rightPanel.setSelectCell(temp);
          this.$rightPanel.showSelectCells(temp);
        }
      }
    },
    // 格式化数据
    formatData(data) {
      let columnIndex = data.replace(/[^a-zA-Z]/g,'');
      let rowIndex = data.replace(/[^0-9]/g,'');
      columnIndex = _.$ABC2Number(columnIndex);
      rowIndex = rowIndex - 1;
      return { columnIndex, rowIndex };
    },
    handleClick(direction) {
      if (!this.ifClick) {
        this.direction = direction;
        this.ifClick = true;
      } else if (this.direction == direction) {
        this.direction = '';
        this.ifClick = false;
      } else if (this.direction != direction) {
        this.direction = direction;
      }
    },
    setColumnMax(index) {
      this.columnMax = _.$Number2ABC(index);
    },
    setRowMax(index) {
      this.rowMax = index + 1;
    },
  },
}
</script>

<style>
  .rowColumn .el-input__inner:focus {
    outline: none;
    border-color: #DCDFE6;
  }
</style>
