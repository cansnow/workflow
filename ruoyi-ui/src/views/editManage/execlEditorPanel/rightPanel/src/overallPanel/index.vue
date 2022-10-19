<template>
  <div class="overall">
    <div class="overall_title">渲染布局</div>
    <el-form label-width="80px" label-position="left" size="mini">
      <el-form-item label="布局方向">
        <el-select v-model="pos" placeholder="请选择" @change="handlePosChenge">
          <el-option label="居左" value="right"></el-option>
          <el-option label="居中" value="center"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 有效区域 -->
    <div class="overall_title">有效区域</div>
    <el-form label-width="80px" label-position="left" size="mini">
      <el-form-item label="开始">
        <div class="rowColumn" @click="() => handleClick('start')">
          <el-input
            :style="ifClick && direction == 'start' ? { border: '1px solid #1890ff', borderRadius: '4px' } : ''"
            v-model="start"
            ref="start"
          ></el-input>
        </div>
      </el-form-item>
      <el-form-item label="结束">
        <div class="rowColumn" @click="() => handleClick('end')">
          <el-input
            :style="ifClick && direction == 'end' ? { border: '1px solid #1890ff', borderRadius: '4px' } : ''"
            v-model="end"
            ref="end"
          ></el-input>
        </div>
      </el-form-item>
    </el-form>
    <!-- 权限规则 -->
    <div class="overall_title" style="display: flex; align-items: center;">
      <span style="flex: 1;">权限规则</span>
      <el-button type="text" @click="open(0)">
        <i class="mdi mdi-plus"></i>
      </el-button>
    </div>
    <div style="margin-left: 2px;">
      <template v-if="formList.length > 0">
        <div v-for="(fItem, index) in formList" :key="index" style="margin: 5px 0; display: flex;">
          <div style="flex: 1; display: flex; align-items: center;">
            <!-- 变量 -->
            <div style="flex: 1;">
              <span>{{ fItem.expression }}</span>
              <span style="margin-left: 5px">{{ fItem.range }}</span>
            </div>
            <el-tag type="danger" size="mini" style="margin-right: 5px;" v-if="fItem.type == 'disable'">禁用</el-tag>
            <el-tag type="info" size="mini" style="margin-right: 5px;" v-else>隐藏</el-tag>
          </div>
          <div>
            <el-button type="text" :disabled="fItem.disabled && dialogVisible" @click="() => handleEdit(index)">
              <i class="mdi mdi-text-box-edit"></i>
            </el-button>
            <el-button type="text" :disabled="fItem.disabled && dialogVisible" style="color: red;" @click="() => handleDel(index)">
              <i class="mdi mdi-delete"></i>
            </el-button>
          </div>
        </div>
      </template>
    </div>
    <!-- 回写规则 -->
    <div class="overall_title" style="display: flex; align-items: center;">
      <span style="flex: 1;">回写规则</span>
      <el-button type="text" @click="open(1)">
        <i class="mdi mdi-plus"></i>
      </el-button>
    </div>
    <div style="margin-left: 2px;">
      <template v-if="dataList.length > 0">
        <div v-for="(dItem, index) in dataList" :key="index" style="margin: 5px 0; display: flex; justify-content: space-between;">
          <span style="display: flex; align-items: center; max-width: 60%; overflow: hidden;">{{ dItem.dataName || dItem.title }}</span>
          <div>
            <el-button type="text" :disabled="dItem.disabled && dialogVisible" @click="() => handleEditData(index)">
              <i class="mdi mdi-text-box-edit"></i>
            </el-button>
            <el-button type="text" :disabled="dItem.disabled && dialogVisible" @click="handleSort">
              <i class="mdi mdi-sort"></i>
            </el-button>
            <el-button type="text" :disabled="dItem.disabled && dialogVisible" @click="() => handleDelData(index)">
              <i class="mdi mdi-close"></i>
            </el-button>
          </div>
        </div>
      </template>
    </div>
    <!-- 查询规则 -->
    <div class="overall_title" style="display: flex; align-items: center;">
      <span style="flex: 1;">查询规则</span>
      <el-button type="text" @click="open(2)">
        <i class="mdi mdi-plus"></i>
      </el-button>
    </div>
    <div style="margin-left: 2px;">
      <template v-if="searchList.length > 0">
        <div v-for="(dItem, index) in searchList" :key="index" style="margin: 5px 0; display: flex; justify-content: space-between;">
          <span style="display: flex; align-items: center; max-width: 60%; overflow: hidden;">{{ dItem.dataName || dItem.title }}</span>
          <div>
            <el-button type="text" :disabled="dItem.disabled && dialogVisible" @click="() => handleEditSearch(index)">
              <i class="mdi mdi-text-box-edit"></i>
            </el-button>
            <el-button type="text" :disabled="dItem.disabled && dialogVisible" @click="handleSearchSort">
              <i class="mdi mdi-sort"></i>
            </el-button>
            <el-button type="text" :disabled="dItem.disabled && dialogVisible" @click="() => handleDelSearch(index)">
              <i class="mdi mdi-close"></i>
            </el-button>
          </div>
        </div>
      </template>
    </div>
    <div class="overall_title">冻结行列</div>
    <el-form label-width="80px" label-position="left" size="mini">
      <el-form-item label="冻结列">
        <el-input-number :min="0" style="width: 100%" v-model="freezeColumn" @change="(e) => handleChange(e, 'column')"></el-input-number>
      </el-form-item>
      <el-form-item label="冻结行">
        <el-input-number :min="0" style="width: 100%" v-model="freezeRow" @change="(e) => handleChange(e, 'row')"></el-input-number>
      </el-form-item>
    </el-form>
    <Dialog
      :title="title"
      :dialogVisible="dialogVisible"
      :width="dialogType == 0 ? '30%' : '40%'"
      @handleClose="handleClose"
      @handleIsOk="handleIsOk">
      <Form v-show="dialogType == 0" ref="form" />
      <Data v-show="dialogType == 1 || dialogType == 2" :dialogType="dialogType" :del="dialogType" ref="Data" />
		</Dialog>
  </div>
</template>

<script>
import Form from './src/form.vue';
import Data from './src/data.vue';
import Dialog from '../dialog/index.vue';
export default {
  inject: ['$rightPanel'],
  components: {
    Form,
    Data,
    Dialog,
  },
  data() {
    return {
      pos: 'center', // 渲染布局方向
      formList: [], // 权限规则
      dataList: [], // 回写规则
      searchList: [], // 查询规则
      dialogVisible: false,
      title: '',
      index: -1,
      columnMax: '',
      rowMax: '',
      dialogType: 0,
      start: '', // 开始
      end: '', // 结束
      ifClick: false,
      direction: '',
      freezeColumn: 0,
      freezeRow: 0,
      options: [], // 回写规则数据集
    };
  },
  computed: {
    // 数据面板
		$DataPanel: function() {
			return this.$rightPanel.getDataPanelRef();
		},
  },
  watch: {
    '$rightPanel.selectCell': {
      handler(newValue) {
        if (this.dialogVisible) {
          // 权限规则
          if (this.dialogType == 0) {
            const form = this.$refs.form.getFormData();
            Object.assign(form, { range: newValue });
            this.$refs.form.setFormData(form);
          }
          // 回写规则
          if (this.dialogType == 1 || this.dialogType == 2) {
            this.$refs.Data.setCellValue(newValue);
          }
        }
        // 设置回写单元格
        if (this.ifClick) {
          const { start } = this.$rightPanel.selection;
          this.setRowMax(start.rowIndex);
          this.setColumnMax(start.columnIndex);
          if (this.direction == 'start') {
            this.setArea('start', start);
          }
          if (this.direction == 'end') {
            this.setArea('end', start);
          }
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
    start() {
      this.$emit('ovserallData', this.getData());
    },
    end() {
      this.$emit('ovserallData', this.getData());
    },
    dialogVisible(newValue) {
      if (newValue && (this.dialogType == 1 || this.dialogType == 2)) {
        this.$nextTick(() => {
          this.$refs.Data.setOptions(this.options);
        });
      }
    },
  },
  methods: {
    // 修改布局方向
    handlePosChenge(e) {
      // console.log('this.pos', this.pos, e);
      this.$emit('ovserallData', this.getData());
    },
    // 设置数据
    setData(data) {
      this.formList = _.map(data.formList, item => {
        item.disabled = false;
        return item;
      });
      this.dataList = data.dataList;
      this.searchList = data.searchList;
      this.start = data.start;
      this.end = data.end;
      this.freezeColumn = data.freezeColumn;
      this.freezeRow = data.freezeRow;
      this.pos = data.pos;
    },
    // 获取数据
    getData() {
      return {
        formList: this.formList,
        dataList: this.dataList,
        searchList: this.searchList,
        start: this.start,
        end: this.end,
        pos: this.pos,
      }
    },
    /** 颠倒数组 */
    handleSort() {
      this.dataList.reverse();
    },
    handleEditSearch(index) {
      if (this.dialogVisible) {
        return;
      }
      this.open(2);
      this.$nextTick(() => {
        const temp = this.searchList[index];
        Object.assign(temp, { index: index, disabled: true });
        this.$refs.Data.setFieldData(temp);
        this.searchList.splice(index, 1, temp);
        this.title = '编辑查询规则';
      });
    },
    handleSearchSort() {
      this.searchList.reverse();
    },
    handleDelSearch(index) {
      this.searchList.splice(index, 1);
      this.$emit('ovserallData', this.getData());
    },
    /** 取消 */
    handleClose() {
      if (this.dialogType == 0) {
        this.$refs.form.resetFormData();
        if (this.index != -1) {
          const form = this.formList[this.index];
          Object.assign(form, { disabled: false });
          this.formList.splice(this.index, 1, form);
          this.index = -1;
        }
      } else {
        this.$refs.Data.resetData();
        if (this.dialogType == 1) {
          this.dataList = _.map(this.dataList, item => {
            return Object.assign({}, item, { disabled: false });
          });
        } else {
          this.searchList = _.map(this.searchList, item => {
            return Object.assign({}, item, { disabled: false });
          });
        }
      }
      this.dialogVisible = false;
      this.$emit('ovserallData', this.getData());
    },
    /** 确认 */
    handleIsOk() {
      if (this.dialogType == 0) {
        const form = this.$refs.form.getFormData();
        if (form.index == -1) {
          this.formList.push(form);
        } else {
          Object.assign(form, { disabled: false });
          this.formList.splice(form.index, 1, form);
        }
      } else {
        if (this.dialogType == 1) {
          const data = this.$refs.Data.getFieldData();
          if (data.index == -1) {
            this.dataList.push(data);
          } else {
            this.dataList.splice(data.index, 1, data);
          }
        } else {
          const data = this.$refs.Data.getFieldData();
          if (data.index == -1) {
            this.searchList.push(data);
          } else {
            this.searchList.splice(data.index, 1, data);
          }
        }
      }
      this.handleClose();
    },
    /** 打开弹窗 */
    open(type) {
      if (!this.dialogVisible) {
        this.dialogType = type;
        this.dialogVisible = true;
        // this.title = (type == 0 ? '新建' : '回写') + '规则';
        switch(type) {
          case 0:
            this.title = '新建规则';
            break;
          case 1: 
            this.title = '回写规则';
            break;
          case 2:
            this.title = '查询规则';
            break;
        }
      }
    },
    /** 删除权限规则 */
    handleDel(index) {
      this.formList.splice(index, 1);
      this.$emit('ovserallData', this.getData());
    },
    // 编辑回写规则
    handleEditData(index) {
      if (this.dialogVisible) {
        return;
      }
      this.open(1);
      this.$nextTick(() => {
        const temp = this.dataList[index];
        Object.assign(temp, { index: index, disabled: true });
        this.$refs.Data.setFieldData(temp);
        this.dataList.splice(index, 1, temp);
        this.title = '编辑回写规则';
      });
    },
    /** 删除回写规则 */
    handleDelData(index) {
      this.dataList.splice(index, 1);
      this.$emit('ovserallData', this.getData());
    },
    /** 编辑权限规则 */
    handleEdit(index) {
      if (this.dialogVisible) {
        return;
      }
      this.open(0);
      this.$nextTick(() => {
        const temp = this.formList[index];
        Object.assign(temp, { index: index, disabled: true });
        this.$refs.form.setFormData(temp);
        this.formList.splice(index, 1, temp);
        this.index = index;
        this.showSelectCells(); // 显示选中单元格
        this.title = '编辑规则';
      });
    },
    // 显示选中单元格
    showSelectCells() {
      const form = this.formList[this.index];
      const { range } = form;
      if (range != '') {
        /**
         * 将G9:G10
         * 转成
         * { 
         *  start: { columnIndex, rowIndex },
         *  end: { rowIndex, columnIndex } 
         * }
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
        this.$refs[direction].blur();
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
    // 设置区域
    setArea(filed, pos) {
      this[filed] = _.$Number2ABC(pos.columnIndex) + (pos.rowIndex + 1);
    },
    getFormatData(pos) {
      return _.$Number2ABC(pos.columnIndex) + (pos.rowIndex + 1);
    },
    setFreezeColumn(columnIndex) {
        this.freezeColumn = columnIndex;
    },
    setFreezeRow(rowIndex) {
        this.freezeRow = rowIndex;
    },
    // 设置冻结行列
    handleChange(e, type) {
      if (type == 'column') {
        this.$emit('freezeColumn', e > 0 ? e : 0);
      } else {
        this.$emit('freezeRow', e > 0 ? e : 0);
      }
    },
  },
  mounted() {
    const _this = this;
    this.options = this.$DataPanel.showData || [];
		this.$DataPanel.$on('dataPanelSelect', function(data) {
      _this.options = data;
		});
  },
}
</script>

<style>
  .rowColumn .el-input__inner:focus {
    outline: none;
    border-color: #DCDFE6;
  }

  .overall_title {
    padding: 5px 0;
    font-weight: bold;
  }
</style>
