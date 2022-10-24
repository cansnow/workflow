<template>
	<Group name="插图">
		<button title="图片"><i class="mdi mdi-undo" title="撤销"></i></button>
		<button text="图片"><i class="mdi mdi-undo" title="撤销"></i></button>

		<button title="图片"><i class="mdi mdi-undo" title="撤销"></i></button>
		<button text="形状"><i class="mdi mdi-undo" title="撤销"></i></button>
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
		}
	},
	methods: {}
};
</script>
<style lang="scss"></style>
