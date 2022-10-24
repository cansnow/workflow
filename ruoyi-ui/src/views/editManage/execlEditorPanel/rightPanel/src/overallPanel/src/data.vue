<template>
  <div>
    <el-form label-width="80px" label-position="left" size="small">
      <el-form-item label="数据集">
        <el-select v-model="title" @change="handleChangeTitle" placeholder="请选择" style="width: 100%">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            {{ item.label }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="名称">
        <el-input
          v-model="dataName"
          placeholder="请输入名称"
          clearable
        />
      </el-form-item>
      <el-form-item label="是否删除" v-show="del == 1 && false">
        <el-radio v-model="ifDel" label="1">是</el-radio>
        <el-radio v-model="ifDel" label="0">否</el-radio>
      </el-form-item>
      <el-form-item :label="dialogType == 1 ? '字段' : '参数'">
        <el-button type="text" size="medium" @click="handleAddField">
          <i class="mdi mdi-plus"></i>
        </el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table
      v-if="filedList.length > 0"
      :data="filedList"
    >
      <el-table-column 
        prop="key"
        label="主键" 
        width="55"
        align="center"
        v-if="dialogType == 1"
      >
        <template slot-scope="scope">
          <el-checkbox v-model="scope.row.key" />
        </template>
      </el-table-column>
      <el-table-column
        prop="filed"
        :label="dialogType == 1 ? '字段' : '参数'"
        :show-overflow-tooltip="true"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="type"
        label="类型"
        align="center"
      >
        <template slot-scope="scope">
          <el-select v-model="scope.row.type" @change="handleChangeType" placeholder="请选择">
            <el-option label="单元格" value="cell" />
            <el-option label="参数" value="parameter" />
            <el-option label="固定值" value="value" />
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
            @focus="handleChangeType"
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
      :title="`选择${ dialogType == 1 ? '字段' : '参数'}`"
      :puncture="false"
      :appendToBody="true"
      width="40%"
      :dialogVisible="dialogVisible"
      @handleClose="handleClose"
      @handleIsOk="handleIsOk"
    >
      <Transfer ref="transfer" :dataSrc="title" :dialogType="dialogType" :options="options" />
		</Dialog>
  </div>
</template>

<script>
import Dialog from '../../dialog/index.vue';
import Transfer from './transfer.vue';
export default {
  components: { Dialog, Transfer },
  props: {
    del: {
      type: Number,
      default: 1,
    },
    dialogType: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      title: '',
      ifDel: '0',
      filedList: [],
      options: [],
      optionsSrc: [],
      selectIndex: -1,
      dialogVisible: false,
      dataName: '', // 规则名称
      treeProps: {
        value:'tableName',             // ID字段名
        label: 'resourcename',         // 显示名称
        children: 'children'    // 子级字段名
      },
      index: -1, // 是否新增，-1新增
    };
  },
  computed: {
    $transfer() {
      return this.$refs.transfer;
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    handleIsOk() {
      // this.filedList = [];
      const temp = this.$transfer.getCheckList();
      _.map(temp, item => {
        const index = this.filedList.findIndex(f => f.filed == item);
        if (index == -1) {
          this.filedList.push({
            filed: item,
            type: 'cell',
            value: '',
            ifClick: false,
            key: false,
          });
        }
      });
      this.handleClose();
    },
    /** 删除行 scope: { row, column, $index } */
    handleDelRow(scope) {
      console.log('scope', scope);
      this.filedList.splice(scope.$index, 1);
    },
    /** 增加字段 */
    handleAddField() {
      if (this.title == '') {
        this.$modal.msgError('请选择数据集！');
        return;
      }
      this.dialogVisible = true;
      this.$nextTick(() => {
        if (this.filedList.length > 0) {
          const data = _.map(this.filedList, item => item.filed);
          this.$transfer.setCheckList(data);
        } else {
          this.$transfer.setCheckList([]);
        }
      });
    },
    /** 选中表 */
    handleChangeTitle(e) {
      console.log(this.title, e);
      this.filedList = [];
    },
    /** 获取回写规则 */
    getFieldData() {
      let ifId = false; // true 客户表，false我们的表
      // 修改title值
      // 获取表名
      let title = this.title;
      if (title.includes('_')) {
        title = title.split('_')[0];
        const table = this.optionsSrc.find(item => item.tableName == title);
        if (!!table && table.type == 'id') {
          title = table.children[0].tempTableName;
        }
        ifId = true;
      } else {
        // 判断是否客户表
        this.optionsSrc.forEach((item) => {
          if (item.type == 'id') {
            item.children.forEach((cItem) => {
              if (cItem.tempTableName == title) {
                ifId = true;
              }
            });
          }
        });
      }
      return {
        title: title,
        ifDel: this.ifDel,
        filedList: this.filedList,
        index: this.index,
        disabled: false,
        dataName: this.dataName,
        ifId,
      };
    },
    /** 重置数据 */
    resetData() {
      this.title = '';
      this.filedList = [];
      this.index = -1;
      this.ifDel = '0';
      this.dataName = '';
    },
    setFieldData(data) {
      this.$nextTick(() => {
        const temp = [];
        _.map(this.optionsSrc, item => {
          if (item.type == 'id') {
            temp.push({ key: item.children[0].tempTableName, value: item.tableName + '_id'});
            }
        });
        this.index = data.index;
        this.ifDel = data.ifDel || '0';
        
        // 格式化表
        const index = temp.findIndex(item => item.key == data.title);
        if (index == -1) {
          this.title = data.title;
        } else {
          this.title = temp[index].value;
        }
        this.filedList = data.filedList;
        this.dataName = data.dataName;
      })
    },
    /** 变更值类型 */
    handleChangeType() {
      this.filedList.forEach((item, index) => {
        item.ifClick = false;
      });
      this.selectIndex = -1;
    },
    /** 点击单元格 scope: { row, column, $index } */
    handleClick(scope) {
      this.filedList.forEach((item, index) => {
        if (index == scope.$index) {
          item.ifClick = !item.ifClick;
        } else {
          item.ifClick = false;
        }
      });
      const index = this.filedList.findIndex(item => item.ifClick);
      this.selectIndex = index;
      if (index == -1) {
        this.$refs['cell_' + scope.$index].blur();
      }
    },
    /** 设置单元格内的值 */
    setCellValue(cell) {
      console.log('setCellValue cell', cell);
      if (this.selectIndex != -1) {
        const pos = cell.split(':');
        const temp = this.filedList[this.selectIndex];
        if (temp.type == 'cell') {
          temp.value = pos[0];
        } else {
          this.selectIndex = -1;
          temp.ifClick = false;
        }
        this.filedList.splice(this.selectIndex, 1, temp);
      }
    },
    setOptions(data) {
      if (!!data && data instanceof Array && data.length > 0) {
        this.optionsSrc = data;
        this.options = _.map(data, item => {
          let temp = item.tableName;
          if (!!item.type && item.type == 'id') {
            temp += '_id';
          }
          return {
            value: temp,
            label: item.resourcename || item.tableName,
          };
        });
      }
    },
  },
}
</script>

<style lang="scss">
  .rowColumn .el-input__inner:focus {
    outline: none;
    border-color: #DCDFE6;
  }

</style>
