<template>
  <div class="meg-workbook_tabs" style="background-color: #f5f7fa; width: 100vw; display: flex;">
      <div style="max-width: 50vw;">
          <el-tabs type="border-card" :tab-position="tabPosition" v-model="tabIndex" closable @tab-remove="(targetName) => handleTabsEdit(targetName, 'del')">
              <!-- :addable="true" @edit="handleTabsEdit" -->
              <el-tab-pane v-for="(sheet,index) in data" :key="'_'+index" :name="'_'+index">
                  <template slot="label">
                      <el-input
                          @keydown.native.stop="() => {}"
                          @keydown.native.enter.stop="() => noEdit()"
                          :ref="'label_' + index"
                          v-show="ifEditTitle && tabIndex == '_' + index"
                          v-model="sheet.title"
                          @change="(e) => handleChangeTitle(e, index)"
                          @blur="() => noEdit()"
                          placeholder="请输入内容"
                          style="width: 80px;"
                      ></el-input>
                      <span
                          v-show="!ifEditTitle || tabIndex != '_' + index"
                          @dblclick="() => handleDblclickTitle('label_' + index)"
                          @contextmenu.prevent="(event) => showMenu(event, { index: '_' + index })"
                      >
                          {{sheet.title}}
                      </span>
                  </template>
              </el-tab-pane>
          </el-tabs>
      </div>
      <div style="padding-left: 10px; display: flex; align-items: center;">
          <el-button @click="() => handleTabsEdit('', 'add')" type="text"><i class="el-icon-plus"></i></el-button>
      </div>
      <Context-Menu v-model="contextMenuState" @click-item="clickItem" :items="curMenuItems" :style="contextMenuPos">
      </Context-Menu>
  </div>
</template>

<script>
import contextMenu from './mixins/contextMenu';
export default {
  mixins: [contextMenu],
  props: ['data', 'ifEditTitle', 'sheetIndex', 'tabPosition'],
  data() {
    return {
      tabIndex: this.sheetIndex
    }
  },
  watch: {
    tabIndex(newV) {
      this.$emit('tab-click', newV);
    },
  },
  methods: {
    // 显示右键菜单
    showMenu(event, data) {
      this.tabIndex = data.index;
      this.showDataMenu(event.pageY, event.pageX, data);
    },
    // 退出编辑
    noEdit() {
      this.$emit('noEdit')
    },
    handleTabsEdit(targetName, action) {
      this.$emit('handleTabsEdit', { targetName, action });
    },
    handleChangeTitle(event, index) {
      this.$emit('handleChangeTitle', { event, index });
    },
    handleDblclickTitle(refV) {
      this.$emit('handleDblclickTitle', { tempTitle: _.map(this.data, item => item.title) });
      this.$nextTick(() => {
          setTimeout(() => {
              this.$refs[refV][0].focus();
          }, 200);
      });
    },
    // 新增
    addItem() {
      this.handleTabsEdit('', 'add');
    },
    // 重命名
    renameItem() {
      // this.tabIndex = this.tempData.index;
      this.handleDblclickTitle('label_' + this.tempData.index.substring(1));
    },
    // 删除
    deleteItem() {
      this.handleTabsEdit(this.tempData.index, 'del');
    },
    // 创建副本
    copyItem() {
      this.$emit('copyTabsItem', this.tempData.index.substring(1));
    },
  },
  
}
</script>

<style>
    .meg-workbook_tabs .el-tabs--border-card > .el-tabs__content {
        padding: 0px !important;
    }

    /* .meg-workbook_tabs .el-tabs--border-card > .el-tabs__header > .el-tabs__nav-wrap {
        width: calc(100vw - 30px) !important;
    } */

    .meg-workbook_tabs .el-tabs--border-card {
        box-shadow: unset !important;
    }


    .meg-workbook_tabs .el-tabs--border-card > .el-tabs__header.is-bottom {
        margin: unset !important;
    }

    .meg-workbook_tabs .el-tabs--border-card > .el-tabs__header > .el-tabs__nav-wrap > .el-tabs__nav-scroll > .el-tabs__nav > .el-tabs__item.is-bottom.is-closable{
        border-right-color: #dcdfe6;
    }
</style>
