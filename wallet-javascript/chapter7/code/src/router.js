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
            path: "/welcome",
            name: "welcome",
            component: () => import("@/page/welcome-page.vue")
        },
        {
            path: "/select-action",
            name: "select-action",
            component: () => import("@/page/select-action-page.vue")
        },
        {
            path: "/create-password",
            name: "create-password",
            component: () => import("@/page/create-password-page.vue")
        },

        {
            path: "/seed-phrase",
            name: "seed-phrase",
            component: () => import("@/page/seed-phrase-page.vue")
        },

        {
            path: "/unlock",
            name: "unlock",
            component: () => import("@/page/unlock-page.vue")
        },
        {
            path: "/seed-phrase-confirm",
            name: "seed-phrase-confirm",
            component: () => import("@/page/seed-phrase-confirm-page.vue")
        },
        {
            path: "/import-seed-phrase",
            name: "import-seed-phrase",
            component: () => import("@/page/import-seed-phrase-page.vue")
        },
        {
            path: "/restore-vault",
            name: "restore-vault",
            component: () => import("@/page/restore-vault-page.vue")
        },
        {
            path: "/main",
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
