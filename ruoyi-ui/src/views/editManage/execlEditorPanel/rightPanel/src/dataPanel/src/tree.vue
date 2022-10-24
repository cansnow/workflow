<template>
  <el-tree
    :data="data"
    show-checkbox
    node-key="id"
    default-expand-all
    :props="defaultProps"
    @node-click="handleNodeClick"
  >
  </el-tree>
</template>

<script>
export default {
  props: ['data', 'defaultProps'],
  methods: {
    handleNodeClick(data) {
      console.log(data);
      this.$emit('nodeClick', data);
    }
  },
}
</script>