<template>
  <el-select :value="valueTitle" :clearable="clearable" @clear="clearHandle">
    <el-option :value="valueTitle" :label="valueTitle" class="options">
      <el-tree
        id="tree-option"
        ref="selectTree"
        :accordion="accordion"
        :data="options"
        :props="props"
        :node-key="props.value"
        :default-expanded-keys="defaultExpandedKey"
        :filter-node-method="filterNode"
        @node-click="handleNodeClick"
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
  },
  data() {
    return {
      filterText: "",
      valueId: '', // 初始值
      valueTitle: "",
      defaultExpandedKey: [],
      selectData:[]
    };
  },
  mounted() {
    this.initHandle();
  },
  methods: {
    // 初始化值
    initHandle() {
      // window.console.log(this.$refs.selectTree)
      if (Array.isArray(this.value) && this.value.length !== 0) {
        // window.console.log(this.$refs.selectTree.getNode(10036), '***')
        // 显示已选项
        this.valueTitle = '';
        const tiltes = [];
        this.value.forEach(item => {
          // this.valueTitle += `${item[this.props.label]}，`
          tiltes.push(item[this.props.label]);
        });
        if (tiltes.length > 0) {
          this.valueTitle = tiltes.join(',');
        }
        this.selectData = JSON.parse(JSON.stringify(this.value));
        // this.$refs.selectTree.setCurrentKey(this.valueId); // 设置默认选中
        this.defaultExpandedKey = [...(this.value.map(item => item[this.props.value]))]; // 设置默认展开
      } else {
        this.valueTitle = '';
        this.defaultExpandedKey = [];
        this.valueId = '';
        // this.clearSelected();
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
    // 切换选项
    handleNodeClick(node) {
      // window.console.log('切换选项',  node)
      if (Array.isArray(node[this.props.children]) && node[this.props.children].length !== 0) {
        return;
      } else {
        // let val ='';
        // window.console.log(node)
        let delIndex = -1;
        if (!this.selectData.some((item, index) => {
          if (item[this.props.value] == node[this.props.value]) {
            delIndex = index;
          }
          return item[this.props.value] == node[this.props.value]
        })) {
          this.selectData.push(node)
          // this.selectData.forEach(el=> val += `${el[this.props.label]}，`);
          // this.valueTitle = val;
          this.valueId = node[this.props.value];
          this.$emit("getValue", this.selectData);
          this.defaultExpandedKey = [];
        } else {
          // 删除已选
          this.selectData.splice(delIndex, 1);
          this.$emit("getValue", this.selectData);
        }
      }
    },
    // 清除选中
    clearHandle() {
      this.valueTitle = "";
      this.valueId = "";
      this.defaultExpandedKey = [];
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
