<template>
  <div class="preview">
    <div class="preview_title">{{ title }}</div>
    <Sheet
      style="height: calc(100vh - 40px)"
      :ref="'sheet_'+index"
      :options="data"
      :sheetIndex="index"
    />
  </div>
</template>

<script>
import Sheet from './Sheet.vue';
import '../../helpers/lodashMixins';
import testData from './testData';
export default {
  components: { Sheet },
  data() {
    return {
      data: testData[0].data,
      index: 0,
      title: testData[0].title,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
        let _this = this;
        _this.update(this.$piniastore.$state);
        this.$piniastore.$subscribe((mutation, state) => {
            _this.update(state);
        });
        this.index = 0;
    },
    update(state) {
        const tempData = JSON.parse(JSON.stringify(testData[0].data));
        Object.assign(tempData, state.previewData);
        this.data = tempData;
        this.title = tempData.title || 'sheet';
    },
  },
}
</script>

<style lang="scss">
.preview {
  height: 100vh;
  width: 60vw;
  margin: auto;
  // border: 1px solid #ddd;

  &_title {
    display: flex; 
    align-items: center;
    justify-content: center;
    height: 40px;
    border-bottom: 1px solid #ddd;
  }
  // .meg-gdlinewrap-column {
  //   .meg-gdline {
  //       border-right: unset !important;
  //   }
  // }

  // .meg-gdlinewrap-row {
  //     flex-direction: column;

  //     .meg-gdline {
  //         border-bottom: unset !important;
  //     }
  // }
}  
</style>