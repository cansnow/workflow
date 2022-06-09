<template>
	<Group name="合并">
		<button title="自动换行" :selected="whiteSpace == 'normal'" @click="setWhiteSpace"><i class="mdi mdi-undo" title="撤销"></i></button>

		<button title="合并后居中" @click="setMerge"><i class="mdi mdi-undo" title="撤销"></i></button>
	</Group>
</template>
<script>
import AppGroup from './AppGroup.vue';

export default {
	extends: AppGroup,
	data() {
		return {
			optionItems: ['whiteSpace'],
			whiteSpace: undefined
		};
	},
	methods: {
		setWhiteSpace() {
			this.whiteSpace = this.whiteSpace == 'normal' ? undefined : 'normal';
			this.$sheet.setSelctionExpandStyle({
				whiteSpace: this.whiteSpace
			});
		},
		setMerge() {
			this.$sheet.doMergeCell('center');
		}
	}
};
</script>
<style lang="scss"></style>
