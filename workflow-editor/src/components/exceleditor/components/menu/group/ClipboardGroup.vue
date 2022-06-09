<template>
	<Group name="剪贴板">
		<button type="button" @click="paste"><i class="mdi mdi-content-paste" title="粘贴"></i></button>
		<button type="button" @click="cut"><i class="mdi mdi-content-cut" title="剪切"></i></button>
		<button type="button" @click="copy"><i class="mdi mdi-content-copy" title="复制"></i></button>
		<button type="button" @click="openFormatpainter" :class="{ selected: formatpainter }"><i class="mdi mdi-brush-variant" title="格式刷"></i></button>
	</Group>
</template>
<script>
import AppGroup from './AppGroup.vue';

export default {
	extends: AppGroup,
	computed: {
		formatpainter() {
			return this.$sheet.curAction == 'formatpainter';
		}
	},
	methods: {
		copy() {
			this.$sheet.doCopySelection();
		},
		cut() {
			this.$sheet.doCutSelection();
		},
		paste() {
			this.$sheet.doPasteSelection();
		},
		openFormatpainter() {
			this.$sheet.doOpenFormatpainter();
		}
	}
};
</script>
<style lang="scss"></style>
