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
  Checkbox
} from 'element-ui';

Vue.prototype.$message = Message;

import App from './App.vue';
import './assets/common.less';

import validator from "./validator.js";
import router from './router.js';

import store from './store';

import 'element-ui/lib/theme-chalk/index.css';
let digging = chrome.extension.getBackgroundPage().window.digging;

Vue.use(Button);
Vue.use(Form);
Vue.use(FormItem);
Vue.use(Input);
Vue.use(Checkbox);

Vue.use(Dropdown);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);


function installPlugin(plugin, name) {
  const p = plugin;
  p.install = function () {
    Vue.prototype[name] = this;
  };
  Vue.use(plugin);
}


installPlugin(digging, 'digging'); // 注册digging到全局


installPlugin(validator, 'validator');


Vue.config.productionTip = process.NODE_ENV === 'production';

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app');
