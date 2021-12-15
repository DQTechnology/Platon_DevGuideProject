import Vue from "vue";

import {
    Message,
    Button,
    Form,
    FormItem,
    Input,
    Dropdown,
    DropdownMenu,
    DropdownItem,
    Checkbox,
    Table,
    TableColumn,
    Pagination,
    Dialog,
    Popover
} from "element-ui";

Vue.prototype.$message = Message;

import App from "./App.vue";
import "./assets/common.less";

import validator from "./validator.js";
import router from "./router.js";

import api from "./api";

import store from "./store";

import "element-ui/lib/theme-chalk/index.css";

let digging = chrome.extension.getBackgroundPage().window.digging;
Vue.use(Dialog);
Vue.use(Button);
Vue.use(Form);
Vue.use(FormItem);
Vue.use(Input);
Vue.use(Checkbox);
Vue.use(Table);
Vue.use(TableColumn);
Vue.use(Pagination);
Vue.use(Dropdown);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);
Vue.use(Popover);

function installPlugin(plugin, name) {
    const p = plugin;
    p.install = function() {
        Vue.prototype[name] = this;
    };
    Vue.use(plugin);
}

installPlugin(digging, "digging"); // 注册digging到全局

// 设置默认网络
api.SwitchNetwork();

installPlugin(api, "api"); // api注册到全局

installPlugin(validator, "validator");

Vue.config.productionTip = process.NODE_ENV === "production";

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");
