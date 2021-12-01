<template>
  <div class="digging">
    <router-view />
  </div>
</template>

<script>

export default {
  data() {
    return {
      counter: 0,
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
      let IsPwdExist = await this.digging.PasswordManager.IsPasswordExist();
      if (IsPwdExist) {
        // 密码存在说明已经创建密码,然后判断是否已经解锁,未解锁,跳转到界面页面
        let isUnlock = await this.digging.PasswordManager.IsUnlock();
        if (!isUnlock) {
          this.$router.push("/unlock");
        }
      } else {
        if (this.IsOpenByPopup()) {
          chrome.tabs.create({ url: "index.html#/welcome" });
        }
      }
    },
    /**
     * 判断是不是通过点击右上角图标进来的
     */
    IsOpenByPopup() {
      return (
        !window.location.href ||
        !window.location.hash ||
        window.location.hash === "#/"
      );
    },
  },
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