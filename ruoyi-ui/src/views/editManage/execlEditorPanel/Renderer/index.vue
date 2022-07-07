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
    console.warn('location', location);
    this.$piniastore.setPreviewData({});
    const tempId = this.$route.params.id;
    console.log('id', this.$route.params.id);
    if (tempId) {
      getTemplateInfoById(tempId).then((res) => {
        const data = JSON.parse(res.data.data);
        const sheet = res.data.sheet;
				if (data instanceof Array && res.code == 200 && data.length > 0) {
					const index = data.findIndex((item) => item.title == sheet);
          if (index != -1) {
            this.$piniastore.setPreviewData(data[index]);
          } else {
            this.$piniastore.setPreviewData(data[0]);
          }
        }
      });
    }
    
	}
}
</script>
