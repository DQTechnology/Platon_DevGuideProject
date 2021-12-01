<template>
  <div class="digding">
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
    if (this.IsOpenByPopup()) {
      chrome.tabs.create({ url: "index.html#/welcome" });
    }
  },

  methods: {
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
}

.digding {
  .el-button--primary {
    background-color: #037dd6 !important;
    border: 2px solid #037dd6 !important;
    border-radius: 6px !important;
    &:hover,
    &:active {
      border-color: #0372c3 !important;
      background-color: #0372c3 !important;
    }
  }
}

</style>