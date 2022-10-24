<template>
  <div>
    <Preview />
  </div>
</template>

<script>
import Preview from '@/components/exceleditor/components/preview/index.vue';
import { getTemplateInfoById } from '@/api/editManage';

export default {
  name: 'Renderer',
  components: { Preview },
  created: function() {
    const tempId = this.$route.params.id;
    this.$piniastore.setPreviewData({});
    const _this = this;
    if (tempId) {
      getTemplateInfoById(tempId).then((res) => {
        const data = JSON.parse(res.data.data.replaceAll('&lt;', '<').replaceAll('&gt;', '>'));
        // const sheet = res.data.sheet;
        const temp = { ifPreview: false, query: _this.$route.query, name: res.data.title };
				if (data instanceof Array && res.code == 200 && data.length > 0) {
          Object.assign(temp, { data });
           _this.$piniastore.setPreviewData(temp);
					// const index = data.findIndex((item) => item.title == sheet);
          // if (index != -1) {
          //   Object.assign(temp, data[index]);
          //   _this.$piniastore.setPreviewData(temp);
          // } else {
          //   Object.assign(temp, data[0]);
          //   _this.$piniastore.setPreviewData(temp);
          // }
        }
      });
    }
	}
}
</script>
