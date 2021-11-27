import Vue from 'vue';
import Router from 'vue-router';

const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}
Vue.use(Router);

const router = new Router({
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/welcome',
            name: 'welcome',
            component: () => import('@/page/welcome-page.vue'),
        },
        {
            path: '/select-action',
            name: 'select-action',
            component: () => import('@/page/select-action-page.vue'),
        },
        {
            path: '/create-password',
            name: 'create-password',
            component: () => import('@/page/create-password-page.vue'),
        },


    ]
});


const routesAfterLogin = [

];

Vue.prototype.$routesAfterLogin = routesAfterLogin;

export default router;
