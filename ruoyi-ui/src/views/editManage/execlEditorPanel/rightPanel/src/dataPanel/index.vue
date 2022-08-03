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
      <el-tree
        :data="data"
        show-checkbox
        ref="selectTree"
        default-expand-all
        :props="props"
        :node-key="props.value"
        check-on-click-node
        @check="handleNodeCheck"
      >
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <i v-if="data.resourcetype == 4" class="mdi mdi-database" />
          <i v-else class="mdi mdi-folder" />
          <span>{{ node.label }}</span>
        </span>
      </el-tree>
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
      data: [
        {
          id: 1,
          label: '省份',
          children: [
            {
              label: '城市',
              children: [
                { label: '区域1' },
                { label: '区域2' },
                { label: '区域3' },
              ]
            },
            { label: '城市2' },
            { label: '城市3' },
          ]
        },{
          id: 30,
          label: 'dafds',
        }
      ],
      props: {
        value:'tableName',             // ID字段名
        label: 'resourcename',  // 显示名称
        children: 'children',   // 子级字段名
      },
      dialogVisible: false,
      checkData: [],
      showData: [],
    };
  },
  watch: {
    showData: {
      handler(value) {
        this.$emit('dataPanelSelect', value);
      }
    }
  },
  methods: {
    // 显示右键菜单
    showMenu(event, data) {
      if (data.resourcetype == 4) {
        this.showDataMenu(event.pageY, event.pageX, data);
      }
    },
    async refreshItem() {
      console.log('refreshItem');
      const res = await getTableFieldByName({ table: this.tempData.tableName });
      const children = _.map(res.data.columns, item => {
        return {
          tableName: item.columnName,
          relativeData: item.columnName,
          resourcename: item.aliasName || item.columnName,
          resourcetype: 0,
        };
      });
      const temp = JSON.parse(JSON.stringify(this.tempData));
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
      this.checkData.forEach(item => {
        this.$refs.selectTree.setChecked(item, false, true);
      });
      this.checkData = [];
    },
    getData(data, index) {
      const _this = this;
      const temp = JSON.parse(JSON.stringify(data[index]));
      getTableFieldByName({ table: temp.tableName }).then((res) => {
        const children = [];
        _.map(res.data.columns, item => {
          children.push({
            tableName: item.columnName,
            relativeData: item.columnName,
            resourcename: item.aliasName || item.columnName,
            resourcetype: 0,
          });
        });
        Object.assign(temp, { children });
        _this.showData.push(temp);
        if (data.length - 1 > index) {
          setTimeout(() => {
            this.getData(data, index + 1);
          }, 1000);
        }
      });
    },
    async handleIsOk() {
      // this.getData(this.checkData, 0);
      for (let i = 0; i < this.checkData.length; i++) {
        const item = JSON.parse(JSON.stringify(this.checkData[i]));
        const index = this.showData.findIndex(fItem => fItem.id == item.id);
        if (index != -1) {
          this.showData.splice(index, 1);
        }
        const res = await getTableFieldByName({ table: item.tableName });
        const children = _.map(res.data.columns, item => {
          return {
            tableName: item.columnName,
            relativeData: item.columnName,
            resourcename: item.aliasName || item.columnName,
            resourcetype: 0,
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
            this.checkData.push(item);
          }
        });
      } else {
        this.checkData.forEach(item => {
          this.$refs.selectTree.setChecked(item, false, true);
        });
        this.checkData = [];
      }
    },
    handleDragStart(node, event) {
      console.log('handleDragStart node', node);
      event.dataTransfer.setData("Text", 'dataSetList.' + node.parent.key + '.' + node.key);
    },
  },
  mounted() {
    const _this = this;
    getDBTable().then((res) => {
      console.log('res', res);
      _this.data = res.data;
    });
  },
}
</script>
