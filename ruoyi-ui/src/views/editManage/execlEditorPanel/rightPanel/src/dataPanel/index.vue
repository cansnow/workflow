<template>
  <div>
    <el-button size="mini" @click="open" style="margin-left: 6px">添加</el-button>
    <div style="padding-top: 10px">
      <el-tree
        draggable
        :allow-drop="() => false"
        :data="data"
        :props="defaultProps"
        @node-drag-start="handleDragStart"
      ></el-tree>
    </div>
    <!-- <div draggable id="test" style="cursor: pointer;" @drag="handleDrag">test</div> -->
  </div>
</template>

<script>
import Tree from './src/tree.vue';
export default {
  inject: ['$rightPanel'],
  data() {
    return {
      data: [
        {
          label: '省份',
          children: [
            {
              label: '城市',
              children: [
                { label: '区域1' },
                { label: '区域2' },
                { label: '区域3' },
              ]
            },
            { label: '城市2' },
            { label: '城市3' },
          ]
        }
      ],
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    };
  },
  methods: {
    open() {
      const h = this.$createElement;
      this.$msgbox({
        title: '数据管理',
        message: h(Tree, { 
          props: { data: this.data, defaultProps: this.defaultProps },
          on: {
            hhhh: function() { console.log('ddd') },
          },
        }),
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '执行中...';
            setTimeout(() => {
              done();
              setTimeout(() => {
                instance.confirmButtonLoading = false;
              }, 300);
            }, 3000);
          } else {
            done();
          }
        }
      }).then(action => {
        this.$message({
          type: 'info',
          message: 'action: ' + action
        });
      });
    },
    handleNodeClick(data) {
      console.log(data);
    },
    handleDragStart(node, event) {
      event.dataTransfer.setData("Text", node.label + ' ' + node.id);
      console.log('handleDragStart');
    },
  },
}
</script>
