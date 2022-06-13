<template>
  <div class="chart" style="height: 300px; width: 100%"></div>
</template>

<script>
import * as echarts from 'echarts'
import resize from './mixins/resize'
require('echarts/theme/macarons')
export default {
  name: 'PieChart',
  mixins: [resize],
  data() {
    return {
      chart: null,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['行业', '技术', '外汇', '金', '预测'],
        },
        series: [
          {
            name: '每周情况',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: [
              { value: 320, name: '行业' },
              { value: 240, name: '技术' },
              { value: 149, name: '外汇' },
              { value: 100, name: '金' },
              { value: 59, name: '预测' },
            ],
            animationEasing: 'cubicInOut',
            animationDuration: 2600,
          },
        ],
      })
    },
  },
}
</script>

<style>
</style>