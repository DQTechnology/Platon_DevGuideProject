import Vue from "vue";
import Router from "vue-router";

const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err);
};
Vue.use(Router);

const router = new Router({
    base: process.env.BASE_URL,
    routes: [
        {
            path: "/",
            name: "main",
            component: () => import("@/page/main-page.vue")
        },
        {
            path: "/send-lat",
            name: "send-lat",
            component: () => import("@/page/send-lat-page.vue")
        }
    ]
});

const routesAfterLogin = [];

Vue.prototype.$routesAfterLogin = routesAfterLogin;

export default router;
