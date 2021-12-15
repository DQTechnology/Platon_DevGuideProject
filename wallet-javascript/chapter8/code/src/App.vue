<template>
    <div class="digging">
        <router-view />
    </div>
</template>

<script>
export default {
    data() {
        return {
            counter: 0
        };
    },

    mounted() {
        this.judgeJumpPage();
    },

    methods: {
        /**
         * 判断跳转的页面
         */
        async judgeJumpPage() {
            if (window.location.pathname !== "/popup.html") {
                this.$store.commit("SetOpenInTab", true);
            } else {
                this.$store.commit("SetOpenInTab", false);
            }

            //

            if (window.location.hash !== "#/restore-vault") {
                let IsPwdExist = await this.digging.PasswordManager.IsPasswordExist();
                if (IsPwdExist) {
                    this.$router.push("/main");
                    // 密码存在说明已经创建密码,然后判断是否已经解锁,未解锁,跳转到界面页面
                    // let isUnlock = await this.digging.PasswordManager.IsUnlock();
                    // if (!isUnlock) {
                    //     this.$router.push("/unlock");
                    // } else {

                    // }
                } else {
                    if (window.location.pathname === "/popup.html") {
                        chrome.tabs.create({ url: "home.html#/welcome" });
                    }
                }
            }
        }
    }
};
</script>

<style lang="less">
html,
body {
    padding: 0;
    margin: 0;
    font-size: 16px;
}
</style>
