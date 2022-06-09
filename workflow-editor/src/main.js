import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

import { createPinia } from 'pinia'
Vue.use(createPinia())

import piniastore from "@/components/Editor/store";
Vue.prototype.$piniastore =piniastore();

Vue.use(ElementUI);

new Vue({
  render: h => h(App),
}).$mount('#app')
