<template>
	<Group name="对齐方式">
		<button title="顶端对齐" :selected="verticalAlign == 'top'" @click="setVerticalAlign('top')"><i class="mdi mdi-align-vertical-top"></i></button>
		<button title="垂直居中" :selected="verticalAlign == 'middle'" @click="setVerticalAlign('middle')"><i class="mdi mdi-align-vertical-center"></i></button>
		<button title="底端对齐" :selected="verticalAlign == 'bottom'" @click="setVerticalAlign('bottom')"><i class="mdi mdi-align-vertical-bottom"></i></button>
		<button title="左对齐" :selected="textAlign == 'left'" @click="setTextAlign('left')"><i class="mdi mdi-align-horizontal-left"></i></button>
		<button title="水平居中" :selected="textAlign == 'center'" @click="setTextAlign('center')"><i class="mdi mdi-align-horizontal-center"></i></button>
		<button title="右对齐" :selected="textAlign == 'right'" @click="setTextAlign('right')"><i class="mdi mdi-align-horizontal-right"></i></button>
		<!--<button title="减少缩进">
		   <i class="mdi mdi-align-vertical-top"></i>
      </button>
      <button title="增加缩进">
		   <i class="mdi mdi-align-vertical-top"></i>
      </button>-->
	</Group>
</template>
<script>
import AppGroup from './AppGroup.vue';

export default {
	extends: AppGroup,
	data() {
		return {
			optionItems: ['verticalAlign', 'textAlign'],
			verticalAlign: undefined,
			textAlign: undefined
		};
	},
	methods: {
		setTextAlign(type) {
			this.textAlign = type;
			this.$sheet.setTextAlign(this.textAlign);
		},
		setVerticalAlign(type) {
			this.verticalAlign = type;
			this.$sheet.setVerticalAlign(this.verticalAlign);
		}
	}
};
</script>
<style lang="scss"></style>
