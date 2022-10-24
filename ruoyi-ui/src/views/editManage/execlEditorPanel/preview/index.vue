<template>
  <div>
    <PreviewDiv />
  </div>
</template>

<script>
import PreviewDiv from '@/components/exceleditor/components/preview/index.vue';
import { getTemplateInfoById } from '@/api/editManage';
import cache from '@/plugins/cache';

export default {
  name: 'Preview',
  components: {
    PreviewDiv
  },
  created: function() {
    const tempId = this.$route.params.id;
    const _this = this;
    this.$piniastore.setPreviewData({});
    const temp = { ifPreview: true, query: _this.$route.query, data: [] };
    if (tempId) {
      // 编辑预览
      getTemplateInfoById(tempId).then((res) => {
        let dataTemp = res.data.data;
        if (dataTemp.includes('&gt;')) {
          dataTemp = dataTemp.replaceAll('&gt;', '>');
        }
        if (dataTemp.includes('&lt;')) {
          dataTemp = dataTemp.replaceAll('&lt;', '<');
        }
        const data = JSON.parse(dataTemp);
        // const sheet = res.data.sheet;
        Object.assign(temp, { name: res.data.title });
				if (data instanceof Array && res.code == 200 && data.length > 0) {
          // const index = data.findIndex((item) => item.title == sheet);
          const cacheData = cache.session.getJSON('PreviewData');
          // Object.assign(temp, index != -1 ? data[index] : data[0]);
          // if (!!cacheData) {
          //   Object.assign(temp, cacheData);
          // }
          Object.assign(temp, { data: cacheData || data });
          _this.$piniastore.setPreviewData(temp);
        }
      });
    } else {
      // 新建预览
      // const temp = { ifPreview: true, query: this.$route.query };
      const cacheData = cache.session.getJSON('PreviewData');
      if (!!cacheData) {
        Object.assign(temp, { data: cacheData });
      }
      this.$piniastore.setPreviewData(temp);
    }
	}
}
</script>
