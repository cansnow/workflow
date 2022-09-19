<template>
  <div>
    <el-button size="mini" @click="open" style="margin-left: 6px">添加</el-button>
    <div style="padding-top: 10px">
      <el-tree
        draggable
        :allow-drop="() => false"
        :allow-drag="handleAllowDrag"
        :data="showData"
        :props="props"
        :node-key="props.value"
        @node-drag-start="handleDragStart"
      >
      <!-- @contextmenu.prevent="test" -->
        <span
          class="custom-tree-node"
          style="width: 100%"
          @contextmenu.prevent="(event) => showMenu(event, data)"
          slot-scope="{ node, data }">
          <span>{{ node.label }}</span>
        </span>
      </el-tree>
    </div>
    <Dialog
      title="数据管理"
      :puncture="false"
      :appendToBody="true"
      width="30%"
      :dialogVisible="dialogVisible"
      @handleClose="handleClose"
      @handleIsOk="handleIsOk"
    >
      <div :style="{ height: `${(screenHeight || 500) * 0.7 - 120}px`, overflowY: 'scroll' }">
        <el-input
          placeholder="输入关键字进行查询"
          v-model="filterText">
        </el-input>
        <el-tree
          :data="data"
          show-checkbox
          ref="selectTree"
          :props="props"
          :node-key="props.value"
          default-expand-all
          check-on-click-node
          @check="handleNodeCheck"
          :filter-node-method="filterNode"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <i v-if="data.resourcetype == 4" class="mdi mdi-database" />
            <i v-else class="mdi mdi-folder" />
            <span>{{ node.label }}</span>
          </span>
        </el-tree>
      </div>
		</Dialog>

    <Context-Menu v-model="contextMenuState" @click-item="clickItem" :items="curMenuItems" :style="contextMenuPos">
    </Context-Menu>
  </div>
</template>

<script>
import { getDBTable, getTableFieldByName } from '@/api/editManage';
import Dialog from '../dialog/index.vue';
import contextMenu from './mixins/contextMenu';
export default {
  inject: ['$rightPanel'],
  components: { Dialog },
  mixins: [contextMenu],
  data() {
    return {
      data: [],
      props: {
        value:'id',             // ID字段名
        label: 'resourcename',  // 显示名称
        children: 'children',   // 子级字段名
      },
      dialogVisible: false,
      checkData: [],
      showData: [],
      filterText: '',
      screenHeight: 0,
    };
  },
  watch: {
    showData: {
      handler(value) {
        this.$emit('dataPanelSelect', value);
      }
    },
    filterText(val) {
      this.$refs.selectTree.filter(val);
    }
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true;
      return data.resourcename.indexOf(value) !== -1;
    },
    // 显示右键菜单
    showMenu(event, data) {
      if (data.resourcetype == 4) {
        this.showDataMenu(event.pageY, event.pageX, data);
      }
    },
    async refreshItem() {
      console.log('refreshItem', this.tempData);
      let temp = {};
      if (this.tempData.type == 'id') {
        Object.assign(temp, { tid: this.tempData.id });
      } else {
        Object.assign(temp, { table: this.tempData.tableName });
      }
      const res = await getTableFieldByName(temp);
      const children = _.map(res.data.columns, item => {
        return {
          id: item.columnName,
          tableName: item.columnName,
          relativeData: item.columnName,
          resourcename: item.aliasName || item.columnName,
          resourcetype: 0,
          tempTableName: res.data.tableName,
          type: this.tempData.type,
        };
      });
      temp = JSON.parse(JSON.stringify(this.tempData));
      Object.assign(temp, { children });
      const index = this.showData.findIndex(fItem => fItem.id == this.tempData.id);
      if (index != -1) {
        this.showData.splice(index, 1, temp);
      }
    },
    deleteItem() {
      console.log('deleteItem');
      const index = this.showData.findIndex(fItem => fItem.id == this.tempData.id);
      if (index != -1) {
        this.showData.splice(index, 1);
      }
    },
    setSelectData(data) {
      this.showData = data;
    },
    handleAllowDrag(node) {
      return node.data.resourcetype == 0;
    },
    handleClose() {
      this.dialogVisible = false;
      // this.checkData.forEach(item => {
      //   this.$refs.selectTree.setChecked(item, false, true);
      // });
      this.$refs.selectTree.setCheckedNodes([]);
      this.checkData = [];
    },
    async handleIsOk() {
      for (let i = 0; i < this.checkData.length; i++) {
        const item = JSON.parse(JSON.stringify(this.checkData[i]));
        const index = this.showData.findIndex(fItem => fItem.id == item.id);
        if (index != -1) {
          this.showData.splice(index, 1);
        }
        let temp = {}; // { table: item.tableName }
        if (item.type == 'table') {
          Object.assign(temp, { table: item.tableName });
        } else {
          Object.assign(temp, { tid: item.id });
        }
        const res = await getTableFieldByName(temp);
        const children = _.map(res.data.columns, field => {
          return {
            id: field.columnName,
            tableName: field.columnName,
            relativeData: field.columnName,
            resourcename: field.aliasName || field.columnName,
            resourcetype: 0,
            tempTableName: res.data.tableName,
            type: item.type,
          };
        });
        Object.assign(item, { children });
        this.showData.push(item);
      }
      this.handleClose();
    },
    open() {
      this.dialogVisible = true;
    },
    handleNodeClick(data) {
      console.log('data', data);
    },
    // 勾选事件
    handleNodeCheck(data, state) {
      console.log('handleNodeCheck', data, state);
      if (state.checkedKeys.length > 0) {
        this.checkData = [];
        state.checkedNodes.forEach(item => {
          if (item.resourcetype == 4) {
            this.checkData.push(Object.assign({}, item, { tableName: item.tableName || item.id, type: !!item.tableName ? 'table' : 'id' }));
          }
        });
      } else {
        this.checkData.forEach(item => {
          this.$refs.selectTree.setChecked(item.tableName, false, true);
        });
        // this.$refs.selectTree.setCheckedNodes([]);
        this.checkData = [];
      }
    },
    handleDragStart(node, event) {
      console.log('handleDragStart node', node);
      const temp = node.data.tempTableName;
      // uuid,表名： node.parent.data.tableName
      const tableName = node.parent.data.tableName;
      event.dataTransfer.setData("Text", 'dataSetList.' + temp + '.' + node.key);
      event.dataTransfer.setData("TableName", tableName);
    },
  },
  mounted() {
    const _this = this;
    getDBTable().then((res) => {
      console.log('res', res);
      _this.data = res.data.map((item, index) => {
        if (index + 1 == res.data.length) {
          item.id = item.id + '_table';
        }
        return item;
      });
    });
    this.screenHeight = document.body.clientHeight;
    window.onresize = () => {
        return (() => {
            this.screenHeight = document.body.clientHeight;
        })()
    };
  },
}
</script>
