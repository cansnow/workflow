<template>
  <div :class="puncture ? 'releaseDialog' : ''">
    <el-dialog
			:title="title"
			:visible.sync="dialogVisible"
			:width="width"
			:modal="!puncture"
      :append-to-body="appendToBody"
			:close-on-click-modal="!puncture"
			:close-on-press-escape="!puncture"
			:before-close="handleClose"
      :show-close="false"
      :fullscreen="true"
      :center="true"
    >
      <slot></slot>
      <span slot="title"></span>
			<span slot="footer" class="dialog-footer">
				<el-button @click="handleClose">取 消</el-button>
				<el-button type="primary" @click="handleIsOk">确 定</el-button>
			</span>
		</el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      default: '提示',
    },
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    puncture: {
      type: Boolean,
      default: true,
    },
    width: {
      type: String,
      default: '100%',
    },
    appendToBody: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    handleClose() {
      this.$emit('handleClose');
    },
    handleIsOk() {
      this.$emit('handleIsOk');
    },
  },
}
</script>

<style>
  .releaseDialog .el-dialog__wrapper {
		pointer-events:none;
	}
	.releaseDialog .el-dialog {
		pointer-events:auto;
	}
  .releaseDialog .el-dialog__header {
    display: none;
  }
  .releaseDialog .el-dialog {
    height: calc(100vh - 46px);
    margin-top: 46px;
  }
</style>