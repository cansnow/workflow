<template>
  <div>
    <el-form label-width="80px" label-position="left" size="small">
      <el-form-item label="数据集">
        <el-select v-model="title" @change="handleChangeTitle" placeholder="请选择" style="width: 100%">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="字段">
        <el-button type="text" size="medium" @click="handleAddField">
          <i class="mdi mdi-plus"></i>
        </el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :data="filedList" v-if="filedList.length > 0">
      <el-table-column
        prop="filed"
        label="字段"
        :show-overflow-tooltip="true"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="type"
        label="类型"
        align="center"
      >
        <template slot-scope="scope">
          <el-select v-model="scope.row.type" placeholder="请选择">
            <el-option label="单元格" value="cell" />
            <el-option label="参数" value="value" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="值" align="center" prop="value">
        <template slot-scope="scope">
          <div v-if="scope.row.type == 'cell'" class="rowColumn" @click="() => handleClick(scope)">
            <el-input
              :style="scope.row.ifClick ? { border: '1px solid #1890ff', borderRadius: '4px' } : ''"
              v-model="scope.row.value"
              :ref="'cell_' + scope.$index"
              placeholder="请选择单元格"
              clearable
            />
          </div>
          <el-input
            v-else
            v-model="scope.row.value"
            placeholder="请输入值"
            clearable
          />
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button type="text" @click="handleDelRow(scope)">
            <i class="mdi mdi-close"></i>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Dialog
      title="选择字段"
      :puncture="false"
      :appendToBody="true"
      width="40%"
      :dialogVisible="dialogVisible"
      @handleClose="handleClose"
      @handleIsOk="handleIsOk"
    >
      <Transfer />
		</Dialog>
  </div>
</template>

<script>
import Dialog from '../../dialog/index.vue';
import Transfer from './transfer.vue';
export default {
  components: { Dialog, Transfer },
  data() {
    return {
      title: '',
      filedList: [],
      options: [{
        value: '选项1',
        label: '黄金糕'
      }, {
        value: '选项2',
        label: '双皮奶'
      }, {
        value: '选项3',
        label: '蚵仔煎'
      }, {
        value: '选项4',
        label: '龙须面'
      }, {
        value: '选项5',
        label: '北京烤鸭'
      }],
      ifSelectCell: -1,
      dialogVisible: false,
    };
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    handleIsOk() {
      this.handleClose();
    },
    /** 删除行 scope: { row, column, $index } */
    handleDelRow(scope) {
      console.log('scope', scope);
      this.filedList.splice(scope.$index, 1);
    },
    /** 增加字段 */
    handleAddField() {
      this.filedList.push({
        filed: '名称',
        type: 'cell',
        cell: 'A1',
        ifClick: false,
      });
      this.dialogVisible = true;
    },
    /** 选中表 */
    handleChangeTitle(e) {
      console.log(this.title, e);
    },
    /** 获取回写规则 */
    getFieldData() {
      return {
        title: this.title,
        filedList: this.filedList,
      };
    },
    /** 重置数据 */
    resetData() {
      this.title = '';
      this.filedList = [];
    },
    /** 点击单元格 scope: { row, column, $index } */
    handleClick(scope) {
      this.filedList.forEach((item, index) => {
        if (index == scope.$index) {
          item.ifClick = !item.ifClick;
        } else {
          item.ifClick = false;
        }
      })
      const index = this.filedList.findIndex(item => item.ifClick);
      this.ifSelectCell = index;
      if (index == -1) {
        this.$refs['cell_' + scope.$index].blur();
      }
    },
    setCellValue() {

    }
  },
}
</script>

<style lang="scss">
  .rowColumn .el-input__inner:focus {
    outline: none;
    border-color: #DCDFE6;
  }

</style>
