<template>
  <div style="display: flex; border: 1px solid #ddd; border-radius: 4px;">
    <div style="max-height: 300px; overflow-y: scroll; width: 50%; padding: 15px;">
      <!-- <el-input
        v-model="search" 
        style="width: 100%;"
        @change="handleChangeSearch"
        placeholder="请输入字段"
        clearable
      /> -->
      <div style="margin: 5px 0;">{{ dataSrc }} > 字段</div>
      <!-- 数据 -->
      <div style="margin: 5px 0;">
        <el-checkbox-group v-model="checkList">
          <el-col :span="24" :key="index" v-for="(item, index) in data">
            <el-checkbox 
              :label="item.value"
              :disabled="item.disabled">
              {{ item.label }}
            </el-checkbox>
          </el-col>
        </el-checkbox-group>
      </div>
    </div>
    <div style="max-height: 300px; overflow-y: scroll; width: 50%; padding: 15px; border-left: 1px solid #ddd">
      <div style="margin: 5px 0;">已选<span style="margin: 0 5px;">{{ checkList.length }}</span>个</div>
      <!-- 已选数据 -->
      <div>
        <div style="display: flex; align-items: center; height: 19px" :key="index" v-for="(item, index) in checkList">
          <div style="flex: 1;">
            {{ getNameByValue(item) }}
          </div>
          <el-button type="text" @click="handleDel(item)">
            <i class="mdi mdi-close"></i>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getTableFieldByName } from '@/api/editManage';
export default {
  name: 'Transfer',
  props: {
    dataSrc: {
      type: String,
      default: '数据源',
    },
  },
  data() {
    return {
      data: [],
      checkList: [],
      search: '',
    };
  },
  watch: {
    dataSrc: {
      handler(value) {
        const _this = this;
        getTableFieldByName({ table: value })
          .then((res) => {
            console.log('res', res);
            _this.data = [];
            _this.checkList = [];
            _.map(res.data.columns, item => {
              _this.data.push({
                label: item.aliasName || item.columnName,
                value: item.columnName,
                disabled: false,
              });
            });
            // if (res.code == 200) {
            //   _this.data = [];
            //   _this.checkList = [];
            //   _.map(res.rows, item => {
            //     _this.data.push({
            //       label: item.columnComment || item.columnName,
            //       value: item.columnName,
            //       disabled: false,
            //     });
            //   });
            // }
          });
      },
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      const _this = this;
      getTableFieldByName({ table: this.dataSrc })
        .then((res) => {
          console.log('res', res);
          _this.data = [];
          _this.checkList = [];
          _.map(res.data.columns, item => {
            _this.data.push({
              label: item.aliasName || item.columnName,
              value: item.columnName,
              disabled: false,
            });
          });
          if (res.code == 200) {
            // _this.data = [];
            // _this.checkList = [];
            // _.map(res.rows, item => {
            //   _this.data.push({
            //     label: item.columnComment || item.columnName,
            //     value: item.columnName,
            //     disabled: false,
            //   });
            // });
          }
        });
    },
    /** 查询 */
    handleChangeSearch() {},
    /** 删除已选 */
    handleDel(value) {
      const index = this.checkList.findIndex(item => item == value);
      if (index != -1) {
        this.checkList.splice(index, 1);
      }
    },
    /** 已选名称 */
    getNameByValue(value) {
      return this.data.filter(item => item.value == value)[0].label;
    },
    /** 获取全部已选 */
    getCheckList() {
      return this.checkList;
    },
  },
}
</script>
