import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false

import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

import { createPinia } from 'pinia'
Vue.use(createPinia())

import piniastore from "@/components/Editor/store";
Vue.prototype.$piniastore =piniastore();

Vue.use(Antd);

new Vue({
  render: h => h(App),
}).$mount('#app')
