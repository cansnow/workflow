<template>
  <div style="display: flex; flex-wrap: wrap; width: 100%;">
    <!-- 表达式 -->
    <div style="display: flex; width: 100%;">
      <div style="margin-right: 20px; width: 50px; line-height: 36px;">表达式</div>
      <div style="display: flex; flex-wrap: wrap; width: calc(100% - 70px);">
        <el-input v-model="form.expression"></el-input>
      </div>
    </div>
    <!-- 范围 -->
    <div style="display: flex; width: 100%; margin-top: 10px;">
      <div style="margin-right: 20px; width: 50px; line-height: 36px;">范围</div>
      <div style="display: flex; flex-wrap: wrap; width: calc(100% - 70px);">
        <el-input v-model="form.range"></el-input>
      </div>
    </div>
    <!-- 样式 -->
    <div style="display: flex; width: 100%; margin-top: 10px;">
      <div style="margin-right: 20px; width: 50px; line-height: 36px;">格式</div>
      <div style="display: flex; flex-wrap: wrap; width: calc(100% - 70px);">
        <div style="width: 100%; line-height: 32px; height: 32px; border: 1px #ddd solid; border-radius: 4px; padding-left: 10px;" :style="form.style">格式样式</div>
        <div style="margin-top: 10px; width: 100%; display: flex; justify-content: space-between;">
          <div v-for="(tool, name) in toolbars" :key="name">
            <Dropdown class="meg-menu-op meg-menu-op-color" v-if="name == 'color' || name == 'bgColor'">
              <i :class="tool.icon"></i>
              <span class="meg-menu-pcol" :style="{ background: tool.value }"></span>
              <Colorpick slot="content" v-model="tool.value"  @input="handleClick(name)"></Colorpick>
            </Dropdown>	
            <el-button v-else class="" :disabled="tool.disabled" :selected="tool.selected" :checked="tool.checked" @click="handleClick(name)" :title="tool.title">
              <i :class="tool.icon"></i>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import Dropdown from '@/components/exceleditor/components/menu/dropdown/Dropdown.vue';
import Colorpick from '@/components/exceleditor/components/menu/colorpick/Colorpick.vue';
export default {
  components: { Dropdown, Colorpick },
  data() {
    return {
      form: {
        expression: '',
        range: '',
        style: {},
      },
      toolbars: {
        bold:{
          icon: 'mdi mdi-format-bold',
          title: '加粗',
          name: 'bold',
          checked: false
        },
        italic:{
          icon: 'mdi mdi-format-italic',
          title: '倾斜',
          name: 'italic',
          checked: false
        },
        underline:{
          icon: 'mdi mdi-format-underline',
          title: '下划线',
          name: 'underline',
          checked: false
        },
        strikethrough:{
          icon: 'mdi mdi-format-strikethrough-variant',
          title: '删除线',
          name: 'strikethrough',
          checked: false
        },
        color:{
          icon: 'mdi mdi-format-text-variant',
          title: '前景颜色',
          name: 'color',
          value: '',
        },
        bgColor:{
          icon: 'mdi mdi-format-color-fill',
          title: '背景颜色',
          name: 'bgColor',
          value: '',
        },
      },
    };
  },
  methods: {
    getData() {},
    setData() {},
    resetData() {},
    handleClick(name) {
      const style = this.form.style;
      switch (name) {
        case 'color': 
          Object.assign(style, { color: this.toolbars[name].value });
          break;
        case 'bgColor':
          Object.assign(style, { backgroundColor: this.toolbars[name].value });
          break;
        case 'underline':
          Object.assign(style, { textDecoration: name });
          break;
        case 'bold':
          Object.assign(style, { fontWeight: name });
          break;
        case 'italic':
          Object.assign(style, { fontStyle: name });
          break;
      }
      Object.assign(this.form, { style });
    },
  },
}
</script>

<style lang="scss">
  .meg-menu-op-color {
    position: relative;
    i {
			font-size: 20px;
		}
  }

  .meg-menu-op {
    display: inline-block;
    border: none;
    min-width: 16px;
    min-height: 16px;
    text-align: center;
    border: 1px solid transparent;
    // font-size: 0px;
    padding: 2px;
    vertical-align: top;
    &:hover {
      border-color: #d3f0e0 !important;
      outline: 2px dashed transparent;
    }
    .meg-menu-pcol {
      display: inline-block;
      height: 4px;
      width: 16px;
      position: absolute;
      bottom: 2px;
      left: 2px;
    }
    &.display {
      opacity: 0.5 !important;
      &:hover {
        background-color: transparent !important;
        border-color: transparent !important;
      }
    }
  }
</style>
