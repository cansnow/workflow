<template>
	<Group name="撤销">
		<button type="button" @click="undo" :class="{ disabled: canUndo }"><i class="mdi mdi-undo" title="撤销"></i></button>
		<button type="button" @click="redo" :class="{ disabled: canRedo }"><i class="mdi mdi-redo" title="恢复"></i></button>
	</Group>
</template>
<script>
import Group from './Group.vue';

export default {
	inject: ['$menu'],
	components: {
		Group
	},
	data() {
		return {};
	},
	computed: {
		$sheet() {
			return this.$menu.$sheet;
		},
		canUndo() {
			return this.$sheet.undoList.length > 0;
		},
		canRedo() {
			return this.$sheet.redoList.length > 0;
		}
	},
	methods: {
		undo() {
			this.$sheet.doUndo();
		},
		redo() {
			this.$sheet.doRedo();
		}
	}
};
</script>
<style lang="scss"></style>
