<template>
  <div>
    <Sheet
      style="height: 100%;"
      :ref="'sheet_'+index"
      :options="data"
      :sheetIndex="index"
    />
  </div>
</template>

<script>
export default {
  // name: 'Preview',
  data() {
    return {
      data: {},
    };
  },
  methods: {
  },
}
</script>