<template>
  <div>
    <!-- 颜色 -->
    <div style="display: flex;">
      <sv-panel ref="sl" :color="color" style="margin-right: 4px; width: 230px; height: 160px;"></sv-panel>
      <hue-slider ref="hue" :color="color" vertical style="float: right; height: 160px;"></hue-slider>
    </div>
    <!-- 透明度 -->
    <alpha-slider v-if="showAlpha" ref="alpha" :color="color"></alpha-slider>
    <!-- 预设颜色块 -->
    <predefine v-if="predefine" :color="color" :colors="predefine"></predefine>
    <!-- 按钮组 -->
    <div style="display: flex;">
      <el-input
        v-model="customInput"
        @keyup.native.enter="handleConfirm"
        @blur="handleConfirm"
        :validate-event="false"
        size="mini">
      </el-input>
      <el-button
        size="mini"
        type="text"
        style="color: #1890ff;"
        @click="$emit('clear')">
        {{ t('el.colorpicker.clear') }}
      </el-button>
      <el-button
        plain
        size="mini"
        @click="confirmValue">
        {{ t('el.colorpicker.confirm') }}
      </el-button>
      <!-- <div style="display: flex; height: 28px; justify-content: flex-end;">
        
      </div> -->
    </div>
  </div>
</template>

<script>
  import SvPanel from './sv-panel';
  import HueSlider from './hue-slider';
  import AlphaSlider from './alpha-slider';
  import Predefine from './predefine';
  import Popper from 'element-ui/src/utils/vue-popper';
  import Locale from 'element-ui/src/mixins/locale';
  import ElInput from 'element-ui/packages/input';
  import ElButton from 'element-ui/packages/button';

  export default {
    name: 'el-color-picker-dropdown',

    mixins: [Popper, Locale],

    components: {
      SvPanel,
      HueSlider,
      AlphaSlider,
      ElInput,
      ElButton,
      Predefine
    },

    props: {
      color: {
        required: true
      },
      showAlpha: Boolean,
      predefine: Array
    },

    data() {
      return {
        customInput: ''
      };
    },

    computed: {
      currentColor() {
        const parent = this.$parent;
        return !parent.value && !parent.showPanelColor ? '' : parent.color.value;
      }
    },

    methods: {
      confirmValue() {
        this.$emit('pick');
      },

      handleConfirm() {
        this.color.fromString(this.customInput);
      }
    },

    mounted() {
      this.$parent.popperElm = this.popperElm = this.$el;
      this.referenceElm = this.$parent.$el;
    },

    watch: {
      showPopper(val) {
        if (val === true) {
          this.$nextTick(() => {
            const { sl, hue, alpha } = this.$refs;
            sl && sl.update();
            hue && hue.update();
            alpha && alpha.update();
          });
        }
      },

      currentColor: {
        immediate: true,
        handler(val) {
          this.customInput = val;
        }
      }
    }
  };
</script>
