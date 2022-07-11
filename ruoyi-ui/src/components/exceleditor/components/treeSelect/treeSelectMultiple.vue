<template>
  <el-select :value="valueTitle" :clearable="clearable" :disabled="disabled" @clear="clearHandle">
    <el-option :value="valueTitle" :label="valueTitle" class="options">
      <el-tree
        id="tree-option"
        ref="selectTree"
        show-checkbox
        :accordion="accordion"
        :data="options"
        :props="props"
        :node-key="props.value"
        :default-expanded-keys="defaultExpandedKey"
        :default-checked-keys="defaultCheckedKeys"
        check-on-click-node
        :filter-node-method="filterNode"
        @check="handleNodeCheck"
      >
      </el-tree>
    </el-option>
  </el-select>
</template>
 
<script>
export default {
  name: "el-tree-select",
  props: {
    /* 配置项 */
    props: {
      type: Object,
      default: () => {
        return {
          value: "id", // ID字段名
          label: "label", // 显示名称
          children: "children", // 子级字段名
        };
      },
    },
    /* 选项列表数据(树形结构的对象数组) */
    options: {
      type: Array,
      default: () => {
        return [];
      },
    },
    /* 初始值 */
    value: {
      type: Array,
      default: () => {
        return [];
      },
    },
    /* 可清空选项 */
    clearable: {
      type: Boolean,
      default: () => {
        return true;
      },
    },
    /* 自动收起 */
    accordion: {
      type: Boolean,
      default: () => {
        return false;
      },
    },

    disabled: {
      type: Boolean,
      default: () => { return false },
    }
  },
  data() {
    return {
      filterText: "", // 过滤文本
      valueId: '', // 初始值
      valueTitle: "", // 显示的值
      defaultExpandedKey: [], // 展开项
      selectData:[], // 勾选数据
      defaultCheckedKeys: [], // 勾选项
    };
  },
  mounted() {
    this.initHandle();
  },
  methods: {
    // 初始化值
    initHandle() {
      // window.console.log(this.$refs.selectTree)
      this.valueTitle = '';
      this.valueId = '';
      this.defaultCheckedKeys = [];
      this.defaultExpandedKey = [];
      if (Array.isArray(this.value) && this.value.length !== 0) {
        // 显示已选项
        const tiltes = [];
        this.value.forEach(item => {
          tiltes.push(item[this.props.label]);
          this.defaultCheckedKeys.push(item[this.props.value]);
        });
        this.setChecked(this.defaultCheckedKeys, true, false);
        if (tiltes.length > 0) {
          this.valueTitle = tiltes.join(',');
        }
        this.selectData = JSON.parse(JSON.stringify(this.value));
        this.defaultExpandedKey = [...(this.value.map(item => item[this.props.value]))]; // 设置默认展开
      }
      this.initScroll();
    },
    // 初始化滚动条
    initScroll() {
      this.$nextTick(() => {
        let scrollWrap = document.querySelectorAll(
          ".el-scrollbar .el-select-dropdown__wrap"
        )[0];
        let scrollBar = document.querySelectorAll(
          ".el-scrollbar .el-scrollbar__bar"
        );
        scrollWrap.style.cssText =
          "margin: 0px; max-height: none; overflow: hidden;";
        scrollBar.forEach((ele) => (ele.style.width = 0));
      });
    },
    // 清除选中
    clearHandle() {
      this.valueTitle = "";
      this.valueId = "";
      this.defaultExpandedKey = [];
      this.defaultCheckedKeys = [];
      // 清空全部勾选项
      const checkeds = [];
      this.selectData.forEach(item => {
        checkeds.push(item[this.props.value]);
      });
      this.setChecked(checkeds, false, true);
      this.selectData = []
      this.clearSelected();
      this.$emit("getValue", null);
    },
    /* 清空选中样式 */
    clearSelected() {
      let allNode = document.querySelectorAll("#tree-option .el-tree-node");
      allNode.forEach((element) => element.classList.remove("is-current"));
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    // 设置勾选项
    setChecked(data, checked = false, deep = true) {
      if (Array.isArray(data) && data.length > 0) {
        data.forEach(item => {
          this.$refs.selectTree.setChecked(item, checked, deep);
        });
      } else {
        this.$refs.selectTree.setChecked(data, checked, deep);
      }
    },
    // 勾选事件
    handleNodeCheck(data, state) {
      console.log('handleNodeCheck', data, state);
      // data checkedKeys checkedNodes 
      // 1.data[this.props.value] 不在 checkedKeys 中，取消勾中，否则勾中
      // 2.在this.selectdata中增加和删除
      // 3.修改this.valueTitle, checkedNodes[item][this.props.value]
      if (state.checkedKeys.length > 0) {
        const tiltes = [];
        this.selectData = [];
        state.checkedNodes.forEach(item => {
          this.selectData.push(item);
          tiltes.push(item[this.props.label]);
        });
        this.valueTitle = tiltes.join(',');
      } else {
        this.clearHandle(); // 清空
      }
    },
  },
  watch: {
    value() {
      // this.valueId = this.value;
      this.initHandle();
    },
    filterText(val) {
      this.$refs.selectTree.filter(val);
    },
    // options(val) {
    //   window.console.log(val)
    // }
  },
};
</script>
 
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-scrollbar .el-scrollbar__view .el-select-dropdown__item {
  height: auto;
  max-height: 274px;
  padding: 0;
  overflow: hidden;
  overflow-y: auto;
}
.el-select-dropdown__item.selected {
  font-weight: normal;
}
ul li >>> .el-tree .el-tree-node__content {
  height: auto;
  padding: 0 20px;
}
.el-tree-node__label {
  font-weight: normal;
}
.el-tree >>> .is-current .el-tree-node__label {
  color: #409eff;
  font-weight: 700;
}
.el-tree >>> .is-current .el-tree-node__children .el-tree-node__label {
  color: #606266;
  font-weight: normal;
}
.selectInput {
  padding: 0 5px;
  box-sizing: border-box;
}
/* 开发禁用 */
/* .el-tree-node:focus>.el-tree-node__content{
    background-color:transparent;
    background-color: #f5f7fa;
    color: #c0c4cc;
    cursor: not-allowed;
  }
  .el-tree-node__content:hover{
    background-color: #f5f7fa;
  } */
</style>
