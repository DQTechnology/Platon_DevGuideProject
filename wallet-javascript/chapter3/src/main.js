import Vue from 'vue';

import {
  Message,
  Button,
  Form,
  FormItem,
  Input,
  Dropdown,
  DropdownMenu,
  DropdownItem,
} from 'element-ui';

Vue.prototype.$message = Message;

import App from './App.vue';

import './assets/common.less';

import router from './router.js';

import validator from "./validator.js";

let mifun = chrome.extension.getBackgroundPage().window;

import 'element-ui/lib/theme-chalk/index.css';
Vue.use(Button);
Vue.use(Form);
Vue.use(FormItem);
Vue.use(Input);

Vue.use(Dropdown);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);

// 加载自定义插件
function installPlugin(plugin, name) {
  const p = plugin;
  p.install = function () {
    Vue.prototype[name] = this;
  };
  Vue.use(plugin);
}


installPlugin(mifun, 'mifun'); // api注册到全局
installPlugin(validator, 'validator'); // api注册到全局


Vue.config.productionTip = process.NODE_ENV === 'production';

new Vue({
  router,
  render: h => h(App),
}).$mount('#app');
