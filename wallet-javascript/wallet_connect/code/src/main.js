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
    Popover,
    Tooltip
} from "element-ui";

Vue.prototype.$message = Message;

import App from "./App.vue";
import "./assets/common.less";

import validator from "./validator.js";
import router from "./router.js";


import store from "./store";

import "element-ui/lib/theme-chalk/index.css";

Vue.use(Tooltip);
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



installPlugin(validator, "validator");

Vue.config.productionTip = process.NODE_ENV === "production";

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app");
