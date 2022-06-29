<template>
  <div>
    <template v-for="(item, index) in radioList">
      <el-radio :key="index" v-model="radio" :label="item.label">{{ item.text }}</el-radio>
    </template>
  </div>
</template>

<script>
export default {
  props: ['selectList', 'select'],
  data() {
    return {
      radioList: this.selectList,
      radio: this.select,
    }
  },
  methods: {
    // 设置单选数据
    setRadioList(data) {
      this.radioList.splice(0, this.radioList.length);
      if (data instanceof Array) {
        if (data.length > 0) {
          data.forEach(item => {
            this.radioList.push(item);
          });
          this.radio = data[0].label;
        }
      } else {
        this.radioList.push(data);
        this.radio = data.label;
      }
    },
    // 获取选择项
    getSeletItem() {
      return this.radio;
    }
  },
}
</script>