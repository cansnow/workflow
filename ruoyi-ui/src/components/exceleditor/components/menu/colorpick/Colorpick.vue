<template>
  <div style="display: flex;">
    <div class="meg-colorpick">
      <div class="meg-colorpick-head">主题颜色</div>
      <table cellspacing="0" cellpadding="0">
        <tr v-for="(row,index) in colors" :key="index">
          <td v-for="(t,i) in row" :key="i">
            <span class="meg-color-wrap" @click="handlerClick(t)">
              <span class="meg-color-item" :style="{background: t}"></span>
            </span>
          </td>
        </tr>
      </table>
      <div class="meg-colorpick-head">标准颜色</div>
      <table cellspacing="0" cellpadding="0">
        <tr>
          <td v-for="(t,i) in sColors" :key="i">
            <span class="meg-color-wrap" @click="handlerClick(t)">
              <span class="meg-color-item" :style="{background: t}"></span>
            </span>
          </td>
        </tr>
      </table>

      <div class="meg-colorpick-clear" @click="handlerClick(undefined)">
        清除颜色
      </div>
    </div>

    <!-- 颜色 -->
    <div style="padding: 6px; background-color: white; border: 1px solid #ddd; border-left: unset;" @click.stop="() => {}">
      <ColorPicker ref='colorPicker' v-model="curColor" @change="handlerChangeColor" size="mini"></ColorPicker>
    </div>
  </div>

</template>
<script>

  import ColorPicker from '../../color-picker/src/main.vue';


  const colors =
    `#ffffff,#000000,#e6e7e7,#445369,#4472c4,#ed7c31,#a5a5a5,#ffc001,#5b9bd5,#70ad47
#f2f2f2,#808080,#d0cece,#d6dce5,#d9e1f2,#fce4d6,#ededed,#fff2cc,#ddebf7,#e2efda
#d9d9d9,#595959,#aeaaaa,#acb9ca,#b4c6e7,#f8ccad,#bdbdbd,#ffe798,#bdd7ee,#c7e0b4
#bfbfbf,#404040,#757171,#8497b0,#8ea9db,#f5b083,#c9c9c9,#ffd966,#9bc2e7,#a9d08e
#a6a6a6,#262626,#3a3838,#333f4e,#305496,#c65811,#7b7b7b,#bf8e00,#2f75b5,#538236
#808080,#0d0d0d,#161616,#222b36,#203764,#833c0c,#525252,#7f6000,#1f4d78,#375624`.split('\n').map(line => line.split(','))

  const sColors = `#c00d00,#ff1400,#ffc001,#ffff00,#92d04f,#01b050,#00b0f0,#0070c0,#00215f,#6f30a0`.split(',')

  export default{
    props:['value'],
    data(){
      return {
        colors,
        sColors,
        curColor: '',
      }
    },
    watch: {
      value: {
        handler(newV) {
          if (!!newV) {
            this.curColor = newV;
          }
        }
      },
      curColor: {
        handler(newV) {
          if (newV != this.value) {
            // document.dispatchEvent(new MouseEvent('click'));
          }
        }
      },
    },
    methods:{
      handlerClick(color){
        this.curColor = color;
        this.$emit('input',color);
      },
      handlerChangeColor(color) {
        this.$emit('input',color);
        document.dispatchEvent(new MouseEvent('click'));
      },
    },
    components: { ColorPicker },
  }
</script>
<style lang="scss">
  .meg-colorpick {
    background: #fff;
    border: 1px solid #cacaca;
    .meg-color-wrap {
      display: inline-block;
      border: 1px solid #e1e1e1;
      padding: 0px 1px;
      margin: 1px 2px;
      &:hover {
        border-color: #217346;
      }
      .meg-color-item {
        width: 12px;
        height: 11px;
        display: inline-block;
      }
    }
    .meg-colorpick-head ,.meg-colorpick-clear{
      height: 24px;
      line-height: 24px;
    }
    .meg-colorpick-clear{
      cursor: pointer;
      &:hover{
        color: #217346;
      }
    }
  }


</style>
